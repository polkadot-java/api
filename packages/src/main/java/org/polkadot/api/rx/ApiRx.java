package org.polkadot.api.rx;

import io.reactivex.Observable;
import org.polkadot.api.ApiBase;
import org.polkadot.api.types.Types;
import org.polkadot.rpc.provider.IProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * # @polkadot/api/rx
 *
 *  ## Overview
 *
 * @name ApiRx
 *
 * ApiRx is a powerfull RxJS Observable wrapper around the RPC and interfaces on the Polkadot network. As a full Observable API, all interface calls return RxJS Observables, including the static `.create(...)`. In the same fashion and subscription-based methods return long-running Observables that update with the latest values.
 *
 * The API is well suited to real-time applications where the latest state is needed, unlocking the subscription-based features of Polkadot (and Substrate) clients. Some familiarity with RxJS is a requirement to use the API, however just understanding `.subscribe` and `.pipe` on Observables will unlock full-scale use thereof.
 *
 * @see [[ApiPromise]]
 *
 * ## Usage
 *
 * Making rpc calls -
 *
 * ```java
 * import org.polkadot.api.rx.ApiRx;
 *
 * // initialise via Promise & static create
 * ApiRx api = ApiRx.create().toPromise();
 *
 * // make a call to retrieve the current network head
 * api.rpc.chain.subscribeNewHead().subscribe((header) => {
 *   //System.out.println(`Chain is at #${header.blockNumber}`);
 * });
 * ```
 * Subscribing to chain state -
 *
 * ```java
 * import org.polkadot.api.rx.ApiRx;
 *
 *
 * // initialise a provider with a specific endpoint
 * WsProvider provider = new WsProvider('wss://example.com:9944')
 *
 * // initialise via isReady & new with specific provider
 * new ApiRx(provider)
 *   .isReady
 *   .pipe(
 *     switchMap((api) =>
 *       combineLatest([
 *         api.query.timestamp.blockPeriod(),
 *         api.query.timestamp.now().pipe(pairwise())
 *       ])
 *     )
 *   )
 *   .subscribe(([blockPeriod, timestamp]) => {
 *      const elapsed = timestamp[1].toNumber() - timestamp[0].toNumber();
 *      //System.out.println(`timestamp ${timestamp[1]} \nelapsed ${elapsed} \n(${blockPeriod}s target)`);
 *   });
 *
 * Submitting a transaction -
 *
 * ```java
 * import org.polkadot.api.rx.ApiRx;
 *
 * const keyring = testingPairs();
 *
 * // get api via Promise
 * ApiRx api = ApiRx.create().toPromise();
 *
 * // retrieve nonce for the account
 * api.query.system
 *   .accountNonce(keyring.alice.address())
 *   .pipe(
 *      first(),
 *      // pipe nonce into transfer
 *      switchMap((nonce) =>
 *        api.tx.balances
 *          // create transfer
 *          .transfer(keyring.bob.address(), 12345)
 *          // sign the transcation
 *          .sign(keyring.alice, { nonce })
 *          // send the transaction
 *          .send()
 *      )
 *   )
 *   // subscribe to overall result
 *   .subscribe(({ status }) => {
 *     if (status.isFinalized) {
 *       //System.out.println('Completed at block hash', status.asFinalized.toHex());
 *     }
 *   });
 * ```
 */
public class ApiRx extends ApiBase {

    private Observable<ApiRx> isReadyRx;

    private ApiRx(IProvider provider) {
        super(provider, ApiType.RX);

        //this.isReadyRx = Observable.fromCallable(() -> {
        //    super.on(IProvider.ProviderInterfaceEmitted.ready, args -> ApiRx.this);
        //})
        this.isReadyRx = Observable.fromFuture(CompletableFuture.supplyAsync(
                () -> {
                    CountDownLatch countDownLatch = new CountDownLatch(1);
                    super.on(IProvider.ProviderInterfaceEmitted.ready, args -> {
                        countDownLatch.countDown();
                    });
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return ApiRx.this;
                }
        ));
    }

	/**
	* Creates an ApiRx instance using the supplied provider. Returns an Observable containing the actual Api instance.
	*
	* @param provider provider that is passed to the class contructor
	*
	* **Example**
	*
	* ```java
	* import org.polkadot.api.rx.ApiRx;
	*
	* ApiRx.create()
	*   .pipe(
	*     switchMap((api) =>
	*       api.rpc.chain.subscribeNewHead()
	*   ))
	*   .subscribe((header) => {
	*     //System.out.println(`new block #${header.blockNumber.toNumber()}`);
	*   });
	* ```
	*/
    public static Observable<ApiRx> create(IProvider provider) {
        ApiRx apiRx = new ApiRx(provider);
        return apiRx.isReadyRx;
    }

    @Override
    protected Types.BaseResult onCall(Types.OnCallFunction method, List params, org.polkadot.types.Types.CodecCallback callback, boolean needsCallback) {
        return null;
    }
}
