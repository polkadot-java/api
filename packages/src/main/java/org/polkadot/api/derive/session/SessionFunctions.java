package org.polkadot.api.derive.session;

import com.onehilltech.promises.Promise;
import org.polkadot.api.ApiBase;
import org.polkadot.api.Types.ApiInterfacePromise;
import org.polkadot.api.derive.Types;
import org.polkadot.api.derive.chain.ChainFunctions;
import org.polkadot.types.codec.Option;
import org.polkadot.types.type.BlockNumber;

import java.math.BigInteger;

public class SessionFunctions {


    public static Types.DeriveRealFunction eraLength(ApiInterfacePromise api) {
        return new Types.DeriveRealFunction() {
            // (): Observable<BN> =>
            @Override
            public Promise call(Object... args) {
                return Promise.all(
                        //api.query.session.sessionLength(),
                        //api.query.staking.sessionsPerEra()
                        api.query().section("session").function("sessionLength").call(),
                        api.query().section("staking").function("sessionsPerEra").call()
                ).then(results -> {
                    BlockNumber sessionLength = results.size() > 0 && results.get(0) != null
                            ? (BlockNumber) results.get(0)
                            : new BlockNumber(BigInteger.ONE);
                    BlockNumber sessionsPerEra = results.size() > 1 && results.get(1) != null
                            ? (BlockNumber) results.get(1)
                            : new BlockNumber(BigInteger.ONE);

                    return Promise.value(sessionLength.multiply(sessionsPerEra));
                });
            }
        };
    }


    public static Types.DeriveRealFunction sessionProgress(ApiInterfacePromise api) {
        return new Types.DeriveRealFunction() {
            // (): Observable<BN> =>
            @Override
            public Promise call(Object... args) {
                return Promise.all(
                        ChainFunctions.bestNumber(api).call(),
                        api.query().section("session").function("sessionLength").call(),
                        api.query().section("session").function("lastLengthChange").call()
                ).then(results -> {
                    BlockNumber bestNumber = (BlockNumber) results.get(0);
                    BlockNumber sessionLength = (BlockNumber) results.get(1);
                    Option<BlockNumber> lastLengthChange = (Option<BlockNumber>) results.get(2);

                    /*
                     (bestNumber || new BN(0))
            .sub((lastLengthChange as Option<BlockNumber>).unwrapOr(new BN(0)))
            .add(sessionLength as BlockNumber || new BN(1))
            .mod(sessionLength as BlockNumber || new BN(1))
                     */

                    BigInteger result = bestNumber.subtract((BigInteger) lastLengthChange.unwrapOr(BigInteger.ZERO))
                            .add(sessionLength)
                            .mod(sessionLength);

                    return Promise.value(result);
                });
            }
        };
    }


    public static Types.DeriveRealFunction eraProgress(ApiInterfacePromise api) {
        return new Types.DeriveRealFunction() {
            //(): Observable<BN> =>
            @Override
            public Promise call(Object... args) {
                return Promise.all(
                        sessionProgress(api).call(),
                        api.query().section("session").function("currentIndex").call(),
                        api.query().section("session").function("sessionLength").call(),
                        api.query().section("staking").function("lastEraLengthChange").call(),
                        api.query().section("staking").function("sessionsPerEra").call()
                ).then(results -> {
                    BigInteger sessionProgress = (BigInteger) results.get(0);
                    BlockNumber currentIndex = (BlockNumber) results.get(1);
                    BlockNumber sessionLength = (BlockNumber) results.get(2);
                    BlockNumber lastEraLengthChange = (BlockNumber) results.get(3);
                    BlockNumber sessionsPerEra = (BlockNumber) results.get(4);

                    BigInteger result = currentIndex.subtract(lastEraLengthChange)
                            .mod(sessionsPerEra)
                            .multiply(sessionLength)
                            .add(sessionProgress);

                    return Promise.value(result);
                });
            }
        };
    }
}
