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
 *
 * ## Overview
 *
 * ApiPromise is a standard JavaScript wrapper around the RPC and interfaces on the Polkadot network. As a full Promise-based, all interface calls return Promises, including the static `.create(...)`. Subscription calls utilise `(value) => {}` callbacks to pass through the latest values.
 * 
 * The API is well suited to real-time applications where either the single-shot state is needed or use is to be made of the subscription-based features of Polkadot (and Substrate) clients.
 * 
 * ## Usage
 * 
 * Making rpc calls -
 * 
 * ```java
 * import org.polkadot.api.promise.ApiPromise;
 * // initialise via static create
 * ApiPromise api = ApiPromise.create();
 * // make a subscription to the network head
 * api.rpc.chain.subscribeNewHead((header) => {
 *     System.out.print("Chain is at ");
 *     System.out.println(header.blockNumber);
 * });
 * ```
 * Subscribing to chain state -
 *
 * ```java
 * import org.polkadot.api.promise.ApiPromise;
 * // initialise a provider with a specific endpoint
 * WsProvider provider = new WsProvider("wss://example.com:9944")
 * // initialise via isReady & new with specific provider
 * ApiPromise api = new ApiPromise(provider).isReady;
 * // retrieve the block target time
 * int blockPeriod = api.query.timestamp.blockPeriod().toNumber();
 * int last = 0;
 * // subscribe to the current block timestamp, updates automatically (callback provided)
 * api.query.timestamp.now((timestamp) => {
 *     int elapsed = 0;
 *     if(last > 0) elapsed = timestamp - last;
 *     last = timestamp.toNumber();
 *     System.out.printf("timestamp %d %d since last %d target", timestamp, elapsed, blockPeriod);
 * });
 * ```
 *
 * Submitting a transaction -
 * 
 * ```java
 * import org.polkadot.api.promise.ApiPromise;
 * 
 * ApiPromise.create().then((api) => {
 *     int nonce = api.query.system.accountNonce(keyring.alice.address());
 * 
 * api.tx.balances
 * // create transfer
 * .transfer(keyring.bob.address(), 12345)
 * // sign the transcation
 * .sign(keyring.alice, { nonce })
 * // send the transaction (optional status callback)
 * .send((status) => {
 *     System.out.print("current status ");
 *     System.out.println(status.type);
 * })
 * // retrieve the submitted extrinsic hash
 * .then((hash) => {
 *     System.out.print("submitted with hash ");
 *     System.out.println(hash);
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
     * **Example**  
     * 
     * ```java
     * import org.polkadot.api.promise.ApiPromise;
     * 
     * Api.create().then((api) => {
     * int timestamp = await api.query.timestamp.now();
     *     System.out.print("lastest block timestamp ");
     *     System.out.println(timestamp);
     * });
     * ```
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
     * 
     * **Example**  
     * 
     * ```java
     * import org.polkadot.api.promise.ApiPromise;
     * 
     * new Api().isReady.then((api) => {
     * api.rpc.subscribeNewHead((header) => {
     *   System.out.print("new block ");
     *   System.out.println(header.blockNumber);
     * });
     * });
     * ```
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
