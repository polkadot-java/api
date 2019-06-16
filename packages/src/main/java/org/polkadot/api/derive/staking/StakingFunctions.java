package org.polkadot.api.derive.staking;

import com.onehilltech.promises.Promise;
import org.polkadot.api.ApiBase;
import org.polkadot.api.Types.ApiInterfacePromise;
import org.polkadot.api.derive.Types;
import org.polkadot.api.derive.balances.BalancesFunctions;
import org.polkadot.types.codec.CodecUtils;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.type.AccountId;

import java.util.List;
import java.util.stream.Collectors;

public class StakingFunctions {


    static Promise allBonds(ApiInterfacePromise api, List<AccountId> stashIds) {
        List<Promise> collect = stashIds.stream().map(id -> {
            //      (api.query.staking.bonded(id) as Observable<Option<AccountId>>)
            return api.query().section("staking").function("bonded").call(id);
        }).collect(Collectors.toList());

        return Promise.all(collect.toArray(new Promise[0]));
    }

    /**
     * @description From the list of stash accounts, retrieve the list of controllers
     */
    public static Types.DeriveRealFunction controllers(ApiInterfacePromise api) {
        return new Types.DeriveRealFunction() {
            //(): Observable<[Array<AccountId>, Array<Option<AccountId>>]> =>
            @Override
            public Promise call(Object... args) {

                return api.query().section("staking").function("validators").call()
                        .then(result -> {
                            List<Object> resultList = CodecUtils.arrayLikeToList(result);
                            List<AccountId> stashIds = (List<AccountId>) resultList.get(0);

                            return Promise.all(
                                    Promise.value(stashIds),
                                    Promise.value(allBonds(api, stashIds))
                            );
                        });
            }
        };
    }


    /**
     * Get the balances for all intentions and their nominators
     */
    public static Types.DeriveRealFunction intentionsBalances(ApiInterfacePromise api) {
        return new Types.DeriveRealFunction() {
            //(): Observable<DerivedBalancesMap> =>
            @Override
            public Promise call(Object... args) {

                //    (api.query.staking.intentions() as Observable<Vector<AccountId>>)

                return api.query().section("staking").function("intentions").call()
                        .then(result -> {
                            Vector<AccountId> vector = (Vector<AccountId>) result;
                            return BalancesFunctions.validatingBalances(api).call(vector);
                        });
            }
        };
    }

}
