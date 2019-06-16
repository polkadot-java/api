package org.polkadot.api.derive.democracy;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.onehilltech.promises.Promise;
import org.apache.commons.collections4.CollectionUtils;
import org.polkadot.api.ApiBase;
import org.polkadot.api.Types.ApiInterfacePromise;
import org.polkadot.api.Types.QueryableModuleStorage;
import org.polkadot.api.Types.QueryableStorageFunction;
import org.polkadot.api.derive.Types;
import org.polkadot.api.derive.balances.BalancesFunctions;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.CodecUtils;
import org.polkadot.types.codec.Option;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.type.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DemocracyFunctions {

    public static Types.DeriveRealFunction votes(ApiInterfacePromise api) {

        Types.DeriveRealFunction ret = new Types.DeriveRealFunction() {
            //(referendumId: BN, accountIds: Array<AccountId> = []): Observable<Array<Vote>> => {
            @Override
            public Promise call(Object... args) {
                BigInteger referendumId = new BigInteger(args[0].toString());
                List<AccountId> accountIds = (List<AccountId>) args[1];

                if (CollectionUtils.isEmpty(accountIds)) {
                    return Promise.value(Lists.newArrayList());
                } else {
                    QueryableModuleStorage<Promise> democracy = api.query().section("democracy");
                    QueryableStorageFunction<Promise> voteOf = democracy.function("voteOf");
                    List<Promise> collect = accountIds.stream().map(accountId -> voteOf.call(Lists.newArrayList(referendumId, accountId))).collect(Collectors.toList());

                    return Promise.all(collect.toArray(new Promise[0]));
                }
            }
        };

        return ret;
    }


    /**
     * @name ReferendumInfoExtended
     * @description A [[ReferendumInfo]] with an additional `index` field
     */
    public static class ReferendumInfoExtended extends ReferendumInfo {
        private ReferendumIndex index;

        //  constructor (value: ReferendumInfo | ReferendumInfoExtended, index?: BN | number) {
        public ReferendumInfoExtended(ReferendumInfo value, int index) {
            super(value);

            this.index = value instanceof ReferendumInfoExtended
                    ? ((ReferendumInfoExtended) value).index
                    : new ReferendumIndex(index);
        }

        /**
         * @description Convenience getter, returns the referendumIndex
         */
        public ReferendumIndex getIndex() {
            return this.index;
        }

        /**
         * @description Creates the JSON representation
         */
        @Override
        public Object toJson() {
            JSONObject jsonObject = (JSONObject) super.toJson();
            jsonObject.put("index", this.index.toJson());
            return jsonObject;
        }
    }


    public static Types.DeriveRealFunction referendumInfo(ApiInterfacePromise api) {
        return new Types.DeriveRealFunction() {
            //(index: BN | number): Observable<Option<ReferendumInfoExtended>> => {
            @Override
            public Promise call(Object... args) {
                Number index = (Number) args[0];

                QueryableStorageFunction<Promise> referendumInfoOf = api.query().section("democracy").function("referendumInfoOf");

                return referendumInfoOf.call(index.intValue())
                        .then(result -> {
                            Option<ReferendumInfo> optionInfo = (Option<ReferendumInfo>) result;
                            ReferendumInfo info = (ReferendumInfo) optionInfo.unwrapOr(null);

                            return Promise.value(new Option<ReferendumInfoExtended>(
                                    TypesUtils.getConstructorCodec(ReferendumInfoExtended.class),
                                    info == null
                                            ? null
                                            : new ReferendumInfoExtended(info, index.intValue())
                            ));
                        });
            }
        };
    }


    public static Types.DeriveRealFunction referendumInfos(ApiInterfacePromise api) {

        Types.DeriveRealFunction referendumInfoOf = referendumInfo(api);

        return new Types.DeriveRealFunction() {
            //(ids: Array<BN | number> = []): Observable<Array<Option<ReferendumInfoExtended>>> => {
            @Override
            public Promise call(Object... args) {
                List<Object> ids = (List<Object>) args[0];

                if (CollectionUtils.isEmpty(ids)) {
                    return Promise.value(Lists.newArrayList());
                } else {
                    List<Promise> collect = ids.stream().map(referendumInfoOf::call).collect(Collectors.toList());
                    return Promise.all(collect.toArray(new Promise[0]));
                }
            }
        };
    }

    public static Types.DeriveRealFunction referendums(ApiInterfacePromise api) {

        return new Types.DeriveRealFunction() {
            //(): Observable<Array<Option<ReferendumInfoExtended>>> =>
            @Override
            public Promise call(Object... args) {

                return Promise.all(
                        api.query().section("democracy").function("nextTally").call(),
                        api.query().section("democracy").function("referendumCount").call()
                ).then(results -> {
                    ReferendumIndex nextTally = (ReferendumIndex) results.get(0);
                    ReferendumIndex referendumCount = (ReferendumIndex) results.get(1);

                    if (referendumCount != null
                            && nextTally != null
                            && referendumCount.compareTo(nextTally) > 0
                            && referendumCount.compareTo(BigInteger.ZERO) >= 0) {

                        int arrayLength = referendumCount.subtract(nextTally).intValue();
                        ArrayList<Integer> list = Lists.newArrayList();
                        for (int i = 0; i < arrayLength; i++) {
                            nextTally.add(BigInteger.valueOf(i));
                            list.add(nextTally.intValue());
                        }
                        //[...Array(referendumCount.sub(nextTally).toNumber())].map((_, i) =>
                        //              nextTally.addn(i)
                        //            )
                        return referendumInfos(api).call(list);
                    } else {
                        return Promise.value(Lists.newArrayList());
                    }
                });
            }
        };
    }


    public static Types.DeriveRealFunction referendumVotesFor(ApiInterfacePromise api) {
        Types.DeriveRealFunction ret = new Types.DeriveRealFunction() {
            //  return (referendumId: BN | number): Observable<Array<DerivedReferendumVote>> =>
            @Override
            public Promise call(Object... args) {
                Object referendumId = args[0];
                ///    (api.query.democracy.votersFor(referendumId) as Observable<Vector<AccountId>>).pipe(
                return api.query().section("democracy").function("votersFor").call(referendumId)
                        .then(result -> {
                            Vector<AccountId> votersFor = (Vector<AccountId>) result;

                            return Promise.all(
                                    Promise.value(votersFor),
                                    votes(api).call(referendumId, votersFor),
                                    BalancesFunctions.votingBalances(api).call(votersFor)
                            );
                        }).then(results -> {
                            List<Object> resultList = CodecUtils.arrayLikeToList(results);
                            Vector<AccountId> votersFor = (Vector<AccountId>) resultList.get(0);
                            List<Vote> votes = (List<Vote>) resultList.get(1);
                            List<Types.DerivedBalances> balances = (List<Types.DerivedBalances>) resultList.get(2);
                            List<Types.DerivedReferendumVote> ret = Lists.newArrayList();

                            for (int index = 0; index < votersFor.size(); index++) {
                                AccountId accountId = votersFor.get(index);
                                Balance balance = balances.size() > index
                                        ? balances.get(index).votingBalance
                                        : new Balance(0);
                                Vote vote = votes.size() > index
                                        ? votes.get(index)
                                        : new Vote(0);


                                Types.DerivedReferendumVote derivedReferendumVote = new Types.DerivedReferendumVote(accountId, balance, vote);
                                ret.add(derivedReferendumVote);
                            }


                            return Promise.value(ret);
                        });
            }
        };
        return ret;
    }


}
