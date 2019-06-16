package org.polkadot.api.derive.chain;

import com.google.common.collect.Lists;
import com.onehilltech.promises.Promise;
import org.polkadot.api.Types.ApiInterfacePromise;
import org.polkadot.api.Types.QueryableModuleStorage;
import org.polkadot.api.derive.Types;
import org.polkadot.types.type.AccountId;
import org.polkadot.types.type.BlockNumber;
import org.polkadot.types.type.Header;

import java.math.BigInteger;
import java.util.List;

public class ChainFunctions {


    /**
     * @description Get the latest block number.
     * @example <BR>
     * <p>
     * ```javascript
     * api.derive.chain.bestNumber((blockNumber) => {
     * console.log(`the current best block is #${blockNumber}`);
     * });
     * ```
     */
    public static Types.DeriveRealFunction bestNumber(ApiInterfacePromise api) {
        return new Types.DeriveRealFunction() {
            //(): Observable<BlockNumber> =>
            @Override
            public Promise call(Object... args) {

                return api.rpc().chain().function("subscribeNewHead").invoke()
                        .then(result -> {
                            Header header = (Header) result;
                            if (header != null && header.getBlockNumber() != null) {
                                return Promise.value(header.getBlockNumber());
                            }
                            //TODO 2019-05-25 00:25
                            throw new UnsupportedOperationException();
                        });
            }
        };
    }


    /**
     * @description Get the latest finalised block number.
     * example
     * <BR>
     * <p>
     * ```javascript
     * api.derive.chain.bestNumberFinalized((blockNumber) => {
     * console.log(`the current finalised block is #${blockNumber}`);
     * });
     * ```
     */
    public static Types.DeriveRealFunction bestNumberFinalized(ApiInterfacePromise api) {
        return new Types.DeriveRealFunction() {
            // (): Observable<BlockNumber> =>
            @Override
            public Promise call(Object... args) {
                return api.rpc().chain().function("subscribeFinalizedHeads").invoke()
                        .then(result -> {
                            Header header = (Header) result;
                            if (header != null && header.getBlockNumber() != null) {
                                return Promise.value(header.getBlockNumber());
                            }
                            //TODO 2019-05-25 00:25
                            throw new UnsupportedOperationException();
                        });
            }
        };
    }


    /**
     * @description Calculates the lag between finalised head and best head
     * @example <BR>
     * <p>
     * ```javascript
     * api.derive.chain.bestNumberLag((lag) => {
     * console.log(`finalised is ${lag} blocks behind head`);
     * });
     * ```
     */
    public static Types.DeriveRealFunction bestNumberLag(ApiInterfacePromise api) {
        return new Types.DeriveRealFunction() {
            //(): Observable<BlockNumber> =>
            @Override
            public Promise call(Object... args) {
                return Promise.all(
                        bestNumber(api).call(),
                        bestNumberFinalized(api).call()
                ).then(results -> {
                    BlockNumber bestNumber = (BlockNumber) results.get(0);
                    BlockNumber bestNumberFinalized = (BlockNumber) results.get(1);
                    BigInteger subtract = bestNumber.subtract(bestNumberFinalized);
                    BlockNumber ret = new BlockNumber(subtract);
                    return Promise.value(ret);
                });
            }
        };
    }

    /**
     * @description Get the a specific block header and extend it with the author
     * @example <BR>
     * <p>
     * ```javascript
     * const { author, blockNumber } = await api.derive.chain.getHeader('0x123...456');
     * <p>
     * console.log(`block #${blockNumber} was authored by ${author}`);
     * ```
     */
    public static Types.DeriveRealFunction getHeader(ApiInterfacePromise api) {
        return new Types.DeriveRealFunction() {
            //(hash: Uint8Array | string): Observable<HeaderExtended | undefined> =>
            @Override
            public Promise call(Object... args) {
                Object hash = args[0];

                QueryableModuleStorage<Promise> session = api.query().section("session");

                return Promise.all(
                        api.rpc().chain().function("getHeader").invoke(hash),
                        session == null
                                ? session.function("validators").at(hash, null)
                                : Promise.value(Lists.newArrayList())
                ).then(results -> {
                    Header header = (Header) results.get(0);
                    List<AccountId> validators = (List<AccountId>) results.get(1);

                    Header.HeaderExtended headerExtended = new Header.HeaderExtended(header, validators);
                    return Promise.value(headerExtended);
                })._catch(err -> {
                    // where rpc.chain.getHeader throws, we will land here - it can happen that
                    // we supplied an invalid hash. (Due to defaults, storeage will have an
                    // empty value, so only the RPC is affected). So return undefined
                    return Promise.value(null);
                });
            }
        };
    }


    //export type HeaderAndValidators = [Header, Array<AccountId>];
    public static class HeaderAndValidators {
        Header header;
        List<AccountId> accountIds;
    }

    /**
     * @description Subscribe to block headers and extend it with the author
     * @example <BR>
     * <p>
     * ```javascript
     * api.derive.chain.subscribeNewHead(({ author, blockNumber }) => {
     * console.log(`block #${blockNumber} was authored by ${author}`);
     * });
     * ```
     */
    public static Types.DeriveRealFunction subscribeNewHead(ApiInterfacePromise api) {
        return new Types.DeriveRealFunction() {
            // (): Observable<HeaderExtended> =>
            @Override
            public Promise call(Object... args) {
                return api.rpc().chain().function("subscribeNewHead").invoke()
                        .then(result -> {
                            Header header = (Header) result;
                            QueryableModuleStorage<Promise> session = api.query().section("session");
                            return Promise.all(
                                    Promise.value(header),
                                    session == null
                                            ? session.function("validators").at(header.getHash(), null)
                                            : Promise.value(Lists.newArrayList())

                            ).then(results -> {
                                Header header2 = (Header) results.get(0);
                                List<AccountId> validators = (List<AccountId>) results.get(1);

                                Header.HeaderExtended headerExtended = new Header.HeaderExtended(header2, validators);
                                return Promise.value(headerExtended);
                            });
                        });
            }
        };
    }

}
