package org.polkadot.api.derive.balances;

import com.google.common.collect.Lists;
import com.onehilltech.promises.Promise;
import org.apache.commons.collections4.CollectionUtils;
import org.polkadot.api.ApiBase;
import org.polkadot.api.Types.ApiInterfacePromise;
import org.polkadot.api.Types.QueryableModuleStorage;
import org.polkadot.api.derive.Types;
import org.polkadot.api.derive.accounts.AccountFunctions;
import org.polkadot.api.derive.accounts.AccountFunctions.AccountIdAndIndex;
import org.polkadot.types.type.AccountId;
import org.polkadot.types.type.Balance;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

public class BalancesFunctions {


    public static Types.DeriveRealFunction fees(ApiInterfacePromise api) {
        return new Types.DeriveRealFunction() {
            // (): Observable<DerivedFees> => {
            @Override
            public Promise call(Object... args) {

                QueryableModuleStorage<Promise> feesSection = api.query().section("fees");
                return Promise.all(
                        api.query().section("balances").function("creationFee").call(),

                        api.query().section("balances").function("existentialDeposit").call(),

                        feesSection != null
                                ? feesSection.function("transactionBaseFee").call()
                                : api.query().section("balances").function("transactionBaseFee").call(),

                        feesSection != null
                                ? feesSection.function("transactionByteFee").call()
                                : api.query().section("balances").function("transactionByteFee").call(),

                        api.query().section("balances").function("transferFee").call()
                ).then((results) -> {
                    //creationFee, existentialDeposit, transactionBaseFee, transactionByteFee, transferFee
                    BigInteger creationFee = (BigInteger) results.get(0);
                    BigInteger existentialDeposit = (BigInteger) results.get(1);
                    BigInteger transactionBaseFee = (BigInteger) results.get(2);
                    BigInteger transactionByteFee = (BigInteger) results.get(3);
                    BigInteger transferFee = (BigInteger) results.get(4);

                    return Promise.value(new Types.DerivedFees(creationFee, existentialDeposit, transactionBaseFee, transactionByteFee, transferFee));
                });
            }
        };

    }

    public static AccountId EMPTY_ACCOUNT = new AccountId(new byte[32]);


    public static Types.DeriveRealFunction votingBalance(ApiInterfacePromise api) {
        return new Types.DeriveRealFunction() {
            //(address: AccountIndex | AccountId | Address | string): Observable<DerivedBalances> => {
            @Override
            public Promise call(Object... args) {
                Object adress = args[0];
                return AccountFunctions.idAndIndex(api).call(adress)
                        .then(result -> {
                            AccountIdAndIndex idAndIndex = (AccountIdAndIndex) result;
                            if (idAndIndex.accountId != null) {
                                return Promise.all(
                                        Promise.value(idAndIndex.accountId),
                                        //api.query.balances.freeBalance(accountId),
                                        api.query().section("balances").function("freeBalance").call(idAndIndex.accountId),
                                        //            api.query.balances.reservedBalance(accountId)
                                        api.query().section("balances").function("reservedBalance").call(idAndIndex.accountId)
                                );
                            } else {
                                return Promise.all(
                                        Promise.value(null),
                                        Promise.value(null),
                                        Promise.value(null)
                                );
                            }
                        })
                        .then(results -> {
                            List resultList = (List) results;
                            AccountId accountId = resultList.get(0) == null
                                    ? EMPTY_ACCOUNT
                                    : (AccountId) resultList.get(0);

                            Balance freeBalance = resultList.get(1) == null
                                    ? new Balance(0)
                                    : (Balance) resultList.get(1);

                            Balance reservedBalance = resultList.get(2) == null
                                    ? new Balance(0)
                                    : (Balance) resultList.get(2);

                            Types.DerivedBalances derivedBalances = new Types.DerivedBalances(
                                    accountId,
                                    freeBalance,
                                    new Balance(0),
                                    reservedBalance,
                                    new Balance(0),
                                    new Balance(freeBalance.add(reservedBalance)),
                                    null);
                            return Promise.value(derivedBalances);
                        });
            }
        };
    }


    public static Types.DeriveRealFunction votingBalances(ApiInterfacePromise api) {

        return new Types.DeriveRealFunction() {
            // (addresses?: Array<AccountId | AccountIndex | Address | string>): Observable<Array<DerivedBalances>> => {
            @Override
            public Promise call(Object... args) {
                List<Object> addresses = (List) args[0];

                if (CollectionUtils.isNotEmpty(addresses)) {
                    List<Promise> promiseList = addresses
                            .stream()
                            .map(address -> votingBalance(api).call(address))
                            .collect(Collectors.toList());

                    return Promise.all(promiseList.toArray(new Promise[0]));
                } else {
                    return Promise.value(Lists.newArrayList());
                }
            }
        };
    }


    public static Types.DeriveRealFunction votingBalancesNominatorsFor(ApiInterfacePromise api) {
        return new Types.DeriveRealFunction() {
            //(address: AccountId | AccountIndex | Address | string): Observable<Array<DerivedBalances>> => {
            @Override
            public Promise call(Object... args) {
                Object address = args[0];
                return AccountFunctions.idAndIndex(api).call(address)
                        .then(result -> {
                            AccountIdAndIndex accountIdAndIndex = (AccountIdAndIndex) result;

                            if (accountIdAndIndex.accountId != null) {
                                //          ? (api.query.staking.nominatorsFor(accountId) as Observable<Vector<AccountId>>)
                                return api.query().section("staking").function("nominatorsFor").call(accountIdAndIndex.accountId);
                            } else {
                                return Promise.value(Lists.newArrayList());
                            }
                        })
                        .then(results -> votingBalances(api).call(results));
            }
        };
    }


    public static Types.DeriveRealFunction validatingBalance(ApiInterfacePromise api) {
        return new Types.DeriveRealFunction() {
            //(address: AccountId | AccountIndex | Address | string): Observable<DerivedBalances> => {
            @Override
            public Promise call(Object... args) {
                Object address = args[0];
                return Promise.all(
                        votingBalance(api).call(address),
                        votingBalancesNominatorsFor(api).call(address)
                ).then(results -> {
                    Types.DerivedBalances balances = (Types.DerivedBalances) results.get(0);
                    List<Types.DerivedBalances> nominators = (List<Types.DerivedBalances>) results.get(1);

                    BigInteger nominatedBalance = BigInteger.valueOf(0);
                    for (Types.DerivedBalances nominator : nominators) {
                        nominatedBalance.add(nominator.votingBalance);
                    }

                    Types.DerivedBalances ret = new Types.DerivedBalances(balances.accountId,
                            balances.freeBalance,
                            new Balance(nominatedBalance),
                            balances.reservedBalance,
                            balances.votingBalance,
                            new Balance(
                                    nominatedBalance.add(balances.votingBalance)
                            ),
                            nominators);
                    return Promise.value(ret);
                });
            }
        };
    }


    public static Types.DeriveRealFunction validatingBalances(ApiInterfacePromise api) {
        return new Types.DeriveRealFunction() {
            //(accountIds: Array<AccountId | Address | string>): Observable<DerivedBalancesMap> => {
            @Override
            public Promise call(Object... args) {
                List<?> accountIds = (List) args[0];
                if (CollectionUtils.isEmpty(accountIds)) {
                    return Promise.value(new Types.DerivedBalancesMap());
                }

                List<Promise> collect = accountIds.stream().map(accountId -> validatingBalance(api).call(accountId)).collect(Collectors.toList());

                return Promise.all(collect.toArray(new Promise[0]))
                        .then(results -> {
                            Types.DerivedBalancesMap balances = new Types.DerivedBalancesMap();

                            for (Object result : results) {
                                Types.DerivedBalances derivedBalances = (Types.DerivedBalances) result;
                                balances.put(derivedBalances.accountId.toString(), derivedBalances);
                            }

                            return Promise.value(balances);
                        });
            }
        };
    }



}
