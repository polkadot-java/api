package org.polkadot.api.derive.accounts;

import com.onehilltech.promises.Promise;
import org.polkadot.api.ApiBase;
import org.polkadot.api.Types.ApiInterfacePromise;
import org.polkadot.api.Types.QueryableModuleStorage;
import org.polkadot.api.derive.Types;
import org.polkadot.common.keyring.address.AddressCodec;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.type.AccountId;
import org.polkadot.types.type.AccountIndex;
import org.polkadot.utils.Utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.polkadot.types.type.AccountIndex.ENUMSET_SIZE;

public class AccountFunctions {


    public static Types.DeriveRealFunction indexToId(ApiInterfacePromise api) {

        return new Types.DeriveRealFunction() {
            // (_accountIndex: AccountIndex | string): Observable<AccountId> => {
            @Override
            public Promise call(Object... args) {
                QueryableModuleStorage<Promise> querySection = api.query().section("indices");
                if (querySection == null) {
                    querySection = api.query().section("balances");
                }
                AccountIndex accountIndex = args[0] instanceof AccountIndex
                        ? (AccountIndex) args[0]
                        : new AccountIndex(args[0]);

                return querySection.function("enumSet").call(accountIndex.divide(ENUMSET_SIZE))
                        .then((result) -> {
                            Vector<AccountId> accountIds = null;
                            if (result != null) {
                                accountIds = (Vector<AccountId>) result;
                            }

                            int index = accountIndex.mod(ENUMSET_SIZE).intValue();
                            if (accountIds != null && accountIds.size() > index) {
                                return Promise.value(accountIds.get(index));
                            } else {
                                return Promise.value(null);
                            }
                        });
            }
        };
    }


    public static class AccountIndexes extends LinkedHashMap<String, AccountIndex> {

    }

    /**
     * Returns all the indexes on the system - this is an unwieldly query since it loops through
     * all of the enumsets and returns all of the values found. This could be up to 32k depending
     * on the number of active accounts in the system
     */
    public static Types.DeriveRealFunction indexes(ApiInterfacePromise api) {

        return new Types.DeriveRealFunction() {
            //(): Observable<AccountIndexes> => {
            @Override
            public Promise call(Object... args) {
                //api.query.indices.nextEnumSet()
                return api.query().section("indices").function("nextEnumSet").call()
                        .then(result -> {
                            // use the nextEnumSet (which is a counter of the number of sets) to construct
                            // a range of values to query [0, 1, 2, ...]
                            AccountIndex next = (AccountIndex) result;

                            List<Promise> list = new ArrayList<>();
                            for (int index = 0; index < next.intValue() + 1; index++) {

                                //api.query.indices.enumSet(index)
                                // retrieve the full enum set for the specific index - each query can return
                                // up to ENUMSET_SIZE (64) records, each containing an AccountId
                                Promise call = api.query().section("indices").function("enumSet").call(index);

                                list.add(call);
                            }

                            return Promise.all(list.toArray(new Promise[0]));
                        }).then((results) -> {
                            AccountIndexes ret = new AccountIndexes();

                            List all = (List) results;
                            for (int outerIndex = 0; outerIndex < all.size(); outerIndex++) {
                                List list = (List) all.get(outerIndex);
                                for (int innerIndex = 0; innerIndex < list.size(); innerIndex++) {
                                    // re-create the index based on position 0 is [0][0] and likewise
                                    // 64 (0..63 in first) is [1][0] (the first index value in set 2)
                                    Object accountId = list.get(innerIndex);
                                    int index = (outerIndex * ENUMSET_SIZE.intValue()) + innerIndex;

                                    ret.put(accountId.toString(), new AccountIndex(index));
                                }
                            }

                            return Promise.value(ret);
                        });
            }
        };
    }


    public static Types.DeriveRealFunction idToIndex(ApiInterfacePromise api) {

        return new Types.DeriveRealFunction() {
            //(accountId: AccountId | string): Observable<AccountIndex | undefined> =>
            @Override
            public Promise call(Object... args) {
                Object accountId = args[0];
                return indexes(api).call()
                        .then((result) -> {
                            AccountIndexes indexes = (AccountIndexes) result;
                            if (indexes == null) {
                                return Promise.value(null);
                            } else {
                                return Promise.value(indexes.get(accountId.toString()));
                            }
                        });
            }
        };
    }

    public static class AccountIdAndIndex {
        public AccountId accountId;
        public AccountIndex accountIndex;

        public AccountIdAndIndex(AccountId accountId, AccountIndex accountIndex) {
            this.accountId = accountId;
            this.accountIndex = accountIndex;
        }
    }

    public static Types.DeriveRealFunction idAndIndex(ApiInterfacePromise api) {

        return new Types.DeriveRealFunction() {
            //(address?: Address | AccountId | AccountIndex | string | null): Observable<AccountIdAndIndex> => {
            @Override
            public Promise call(Object... args) {

                Object address = args[0];
                try {
                    // yes, this can fail, don't care too much, catch will catch it
                    byte[] decoded = Utils.isU8a(address)
                            ? Utils.u8aToU8a(address)
                            : AddressCodec.decodeAddress(address.toString());
                    if (decoded.length == 32) {
                        AccountId accountId = new AccountId(decoded);

                        return idToIndex(api).call(accountId)
                                .then(result -> {
                                    AccountIdAndIndex accountIdAndIndex = new AccountIdAndIndex(accountId, (AccountIndex) result);
                                    return Promise.value(accountIdAndIndex);
                                });
                    }

                    AccountIndex accountIndex = new AccountIndex(decoded);

                    return indexToId(api).call(accountIndex)
                            .then(result -> {
                                AccountIdAndIndex accountIdAndIndex = new AccountIdAndIndex((AccountId) result, accountIndex);
                                return Promise.value(accountIdAndIndex);
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                    return Promise.value(new AccountIdAndIndex(null, null));
                }
            }
        };
    }

}
