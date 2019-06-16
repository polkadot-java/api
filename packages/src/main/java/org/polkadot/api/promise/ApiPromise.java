package org.polkadot.api.promise;

import com.google.common.collect.Lists;
import com.onehilltech.promises.Promise;
import org.polkadot.api.ApiBase;
import org.polkadot.api.Types;
import org.polkadot.direct.IRpcFunction;
import org.polkadot.rpc.provider.IProvider;
import org.polkadot.rpc.provider.ws.WsProvider;

import java.util.List;

/**
 * # @polkadot/api/promise
 * <p>
 * ## Overview
 *
 * @name ApiPromise
 * <p>
 * ApiPromise is a standard JavaScript wrapper around the RPC and interfaces on the Polkadot network. As a full Promise-based, all interface calls return Promises, including the static `.create(...)`. Subscription calls utilise `(value) => {}` callbacks to pass through the latest values.
 * <p>
 * The API is well suited to real-time applications where either the single-shot state is needed or use is to be made of the subscription-based features of Polkadot (and Substrate) clients.
 * @see [[ApiRx]]
 * <p>
 * ## Usage
 * <p>
 * Making rpc calls -
 * <p>
 * ```java
 * import org.polkadot.api.promise.ApiPromise;
 * <p>
 * // initialise via static create
 * ApiPromise api = ApiPromise.create();
 * <p>
 * // make a subscription to the network head
 * api.rpc.chain.subscribeNewHead((header) => {
 * //System.out.println(`Chain is at #${header.blockNumber}`);
 * });
 * ```
 * Subscribing to chain state -
 * <p>
 * ```java
 * import org.polkadot.api.promise.ApiPromise;
 * <p>
 * // initialise a provider with a specific endpoint
 * WsProvider provider = new WsProvider("wss://example.com:9944")
 * <p>
 * // initialise via isReady & new with specific provider
 * ApiPromise api = new ApiPromise(provider).isReady;
 * <p>
 * // retrieve the block target time
 * int blockPeriod = api.query.timestamp.blockPeriod().toNumber();
 * int last = 0;
 * <p>
 * // subscribe to the current block timestamp, updates automatically (callback provided)
 * api.query.timestamp.now((timestamp) => {
 * const elapsed = last
 * ? `, ${timestamp.toNumber() - last}s since last`
 * : '';
 * <p>
 * last = timestamp.toNumber();
 * //System.out.println(`timestamp ${timestamp}${elapsed} (${blockPeriod}s target)`);
 * });
 * ```
 * Submitting a transaction -
 * <p>
 * ```java
 * import org.polkadot.api.promise.ApiPromise;
 * <p>
 * ApiPromise.create().then((api) => {
 * int nonce = api.query.system.accountNonce(keyring.alice.address());
 * <p>
 * api.tx.balances
 * // create transfer
 * transfer(keyring.bob.address(), 12345)
 * // sign the transcation
 * .sign(keyring.alice, { nonce })
 * // send the transaction (optional status callback)
 * .send((status) => {
 * console.log(`current status ${status.type}`);
 * })
 * // retrieve the submitted extrinsic hash
 * .then((hash) => {
 * //System.out.println(`submitted with hash ${hash}`);
 * });
 * });
 * ```
 */
public class ApiPromise extends ApiBase<Promise> {

    private Promise isReadyPromise;

    /**
     * Creates an ApiPromise instance using the supplied provider. Returns an Promise containing the actual Api instance.
     *
     * @param provider provider that is passed to the class contructor.
     *                 **Example**
     *                 <p>
     *                 ```java
     *                 import org.polkadot.api.promise.ApiPromise;
     *                 <p>
     *                 Api.create().then((api) => {
     *                 int timestamp = await api.query.timestamp.now();
     *                 <p>
     *                 //System.out.println(`lastest block timestamp ${timestamp}`);
     *                 });
     *                 ```
     */
    public static Promise<ApiPromise> create(IProvider iProvider) {
        ApiPromise apiPromise = new ApiPromise(iProvider);
        return apiPromise.isReadyPromise;
    }


    public static Promise<ApiPromise> create() {
        ApiPromise apiPromise = new ApiPromise(new WsProvider());
        return apiPromise.isReadyPromise;
    }

    /**
     * Creates an instance of the ApiPromise class
     *
     * @param provider provider that is passed to the class contructor.
     *                 <p>
     *                 **Example**
     *                 <p>
     *                 ```java
     *                 import org.polkadot.api.promise.ApiPromise;
     *                 <p>
     *                 new Api().isReady.then((api) => {
     *                 api.rpc.subscribeNewHead((header) => {
     *                 //System.out.println(`new block #${header.blockNumber.toNumber()}`);
     *                 });
     *                 });
     *                 ```
     */
    ApiPromise(IProvider iProvider) {
        super(iProvider, ApiType.PROMISE);

        this.isReadyPromise = new Promise<ApiPromise>((handler) -> {
            ApiPromise.super.once(IProvider.ProviderInterfaceEmitted.ready,
                    args -> handler.resolve(ApiPromise.this));
        });
    }

    @Override
    public ApiType getType() {
        return ApiType.PROMISE;
    }

    @Override
    protected Promise onCall(Types.OnCallFunction method, List<Object> params, boolean needCallback, IRpcFunction.SubscribeCallback callback) {
        List<Object> args = Lists.newArrayList();
        if (params != null) {
            args.addAll(params);
        }

        if (callback != null) {
            args.add(callback);
        }
        return method.apply(args.toArray(new Object[0]));
    }

}
