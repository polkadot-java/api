package org.polkadot.api.promise;

import com.onehilltech.promises.Promise;
import org.polkadot.api.ApiBase;
import org.polkadot.api.types.Types;
import org.polkadot.rpc.provider.IProvider;
import org.polkadot.rpc.provider.ws.WsProvider;
import org.polkadot.types.Types.CodecCallback;

import java.util.List;

/**
 * # @polkadot/api/promise
 *
 * ## Overview
 *
 * @name ApiPromise
 *
 * ApiPromise is a standard JavaScript wrapper around the RPC and interfaces on the Polkadot network. As a full Promise-based, all interface calls return Promises, including the static `.create(...)`. Subscription calls utilise `(value) => {}` callbacks to pass through the latest values.
 *
 * The API is well suited to real-time applications where either the single-shot state is needed or use is to be made of the subscription-based features of Polkadot (and Substrate) clients.
 *
 * @see [[ApiRx]]
 *
 * ## Usage
 *
 * Making rpc calls -
 *
 * ```java
 * import org.polkadot.api.promise.ApiPromise;
 *
 * // initialise via static create
 * ApiPromise api = ApiPromise.create();
 *
 * // make a subscription to the network head
 * api.rpc.chain.subscribeNewHead((header) => {
 *   //System.out.println(`Chain is at #${header.blockNumber}`);
 * });
 * ```
 * Subscribing to chain state -
 *
 * ```java
 * import org.polkadot.api.promise.ApiPromise;
 *
 * // initialise a provider with a specific endpoint
 * WsProvider provider = new WsProvider("wss://example.com:9944")
 *
 * // initialise via isReady & new with specific provider
 * ApiPromise api = new ApiPromise(provider).isReady;
 *
 * // retrieve the block target time
 * int blockPeriod = api.query.timestamp.blockPeriod().toNumber();
 * int last = 0;
 *
 * // subscribe to the current block timestamp, updates automatically (callback provided)
 * api.query.timestamp.now((timestamp) => {
 *   const elapsed = last
 *     ? `, ${timestamp.toNumber() - last}s since last`
 *     : '';
 *
 *   last = timestamp.toNumber();
 *   //System.out.println(`timestamp ${timestamp}${elapsed} (${blockPeriod}s target)`);
 * });
 * ```
 * Submitting a transaction -
 *
 * ```java
 * import org.polkadot.api.promise.ApiPromise;
 *
 * ApiPromise.create().then((api) => {
 *   int nonce = api.query.system.accountNonce(keyring.alice.address());
 *
 *   api.tx.balances
 *     // create transfer
 *     transfer(keyring.bob.address(), 12345)
 *     // sign the transcation
 *     .sign(keyring.alice, { nonce })
 *     // send the transaction (optional status callback)
 *     .send((status) => {
 *       console.log(`current status ${status.type}`);
 *     })
 *     // retrieve the submitted extrinsic hash
 *     .then((hash) => {
 *       //System.out.println(`submitted with hash ${hash}`);
 *     });
 * });
 * ```
 */
public class ApiPromise extends ApiBase {

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
	*   int timestamp = await api.query.timestamp.now();
	*
	*   //System.out.println(`lastest block timestamp ${timestamp}`);
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
	*   api.rpc.subscribeNewHead((header) => {
	*     //System.out.println(`new block #${header.blockNumber.toNumber()}`);
	*   });
	* });
	* ```
	*/
    ApiPromise(IProvider iProvider) {
        super(iProvider, ApiType.PROMISE);

        this.isReadyPromise = new Promise<ApiPromise>((handler) -> {
            ApiPromise.super.once(IProvider.ProviderInterfaceEmitted.ready,
                    args -> handler.resolve(ApiPromise.this));
        });

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //TODO 2019-05-10 00:25
        //this.emit(IProvider.ProviderInterfaceEmitted.ready, this);
    }

    @Override
    public ApiType getType() {
        return ApiType.PROMISE;
    }

    @Override
    protected Types.BaseResult onCall(Types.OnCallFunction method, List params, CodecCallback callback, boolean needsCallback) {
        //TODO 2019-05-05 21:07
        throw new UnsupportedOperationException();
    }
}
