package org.polkadot.api.rx;

import com.google.common.collect.Lists;
import com.onehilltech.promises.Promise;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import org.polkadot.api.ApiBase;
import org.polkadot.api.Types;
import org.polkadot.direct.IRpcFunction;
import org.polkadot.rpc.provider.IProvider;
import org.polkadot.utils.RxUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * # @polkadot/api/rx
 * <p>
 * ## Overview
 * <p>
 * ApiRx is a powerfull RxJS Observable wrapper around the RPC and interfaces on the Polkadot network. As a full Observable API, all interface calls return RxJS Observables, including the static `.create(...)`. In the same fashion and subscription-based methods return long-running Observables that update with the latest values.
 * <p>
 * The API is well suited to real-time applications where the latest state is needed, unlocking the subscription-based features of Polkadot (and Substrate) clients. Some familiarity with RxJS is a requirement to use the API, however just understanding `.subscribe` and `.pipe` on Observables will unlock full-scale use thereof.
 * <p>
 * ## Usage
 * <p>
 * Making rpc calls -
 * <p>
 * <p>
 * ```java
 * import org.polkadot.api.rx.ApiRx;
 * <p>
 * // initialise via Promise & static create
 * ApiRx api = ApiRx.create().toPromise();
 * <p>
 * // make a call to retrieve the current network head
 * api.rpc.chain.subscribeNewHead().subscribe((header) => {
 * System.out.print("Chain is at ");
 * System.out.println(header.blockNumber);
 * });
 * ```
 * Subscribing to chain state -
 * <p>
 * ```java
 * import org.polkadot.api.rx.ApiRx;
 * <p>
 * // initialise a provider with a specific endpoint
 * WsProvider provider = new WsProvider('wss://example.com:9944')
 * <p>
 * // initialise via isReady & new with specific provider
 * new ApiRx(provider)
 * .isReady
 * .pipe(
 * switchMap((api) =>
 * combineLatest([
 * api.query.timestamp.blockPeriod(),
 * api.query.timestamp.now().pipe(pairwise())
 * ])
 * )
 * )
 * .subscribe(([blockPeriod, timestamp]) => {
 * int elapsed = timestamp[1] - timestamp[0];
 * System.out.printf("timestamp %d \nelapsed %d \n(%d target)", timestamp[1], elapsed, blockPeriod);
 * });
 * <p>
 * Submitting a transaction -
 * <p>
 * ```java
 * import org.polkadot.api.rx.ApiRx;
 * <p>
 * const keyring = testingPairs();
 * <p>
 * // get api via Promise
 * ApiRx api = ApiRx.create().toPromise();
 * <p>
 * // retrieve nonce for the account
 * api.query.system
 * .accountNonce(keyring.alice.address())
 * .pipe(
 * first(),
 * // pipe nonce into transfer
 * switchMap((nonce) =>
 * api.tx.balances
 * // create transfer
 * .transfer(keyring.bob.address(), 12345)
 * // sign the transcation
 * .sign(keyring.alice, { nonce })
 * // send the transaction
 * .send()
 * )
 * )
 * // subscribe to overall result
 * .subscribe(({ status }) => {
 * if (status.isFinalized) {
 * System.out.print("Completed at block hash ");
 * System.out.println(status.asFinalized.toHex());
 * }
 * });
 * ```
 */
public class ApiRx extends ApiBase<Observable> {

    private static final Logger logger = LoggerFactory.getLogger(ApiRx.class);

    private Observable<ApiRx> isReadyRx;

    private ApiRx(IProvider provider) {
        super(provider, ApiType.RX);

        this.isReadyRx = RxUtils.fromPromise(
                new Promise<>((handler) -> {
                    ApiRx.super.once(IProvider.ProviderInterfaceEmitted.ready,
                            args -> handler.resolve(ApiRx.this));
                })
        );
    }


    /**
     * Creates an ApiRx instance using the supplied provider. Returns an Observable containing the actual Api instance.
     *
     * @param provider provider that is passed to the class contructor
     *                 <p>
     *                 **Example**
     *                 <p>
     *                 ```java
     *                 import org.polkadot.api.rx.ApiRx;
     *                 <p>
     *                 ApiRx.create()
     *                 .pipe(
     *                 switchMap((api) =>
     *                 api.rpc.chain.subscribeNewHead()
     *                 ))
     *                 .subscribe((header) => {
     *                 System.out.print("new block ");
     *                 System.out.println(header.blockNumber);
     *                 });
     *                 ```
     */
    public static Observable<ApiRx> create(IProvider provider) {
        ApiRx apiRx = new ApiRx(provider);
        return apiRx.isReadyRx;
    }

    @Override
    protected Observable onCall(Types.OnCallFunction method, List<Object> params, boolean needCallback, IRpcFunction.SubscribeCallback callback) {

        if (!needCallback && callback == null) {
            Promise apply = method.apply(params.toArray(new Object[0]));
            return RxUtils.fromPromise(apply);
        }

        Observable ret = null;

        if (needCallback && callback == null) {
            BehaviorSubject<Object> subject = BehaviorSubject.create();
            //PublishSubject subject = PublishSubject.create();
            callback = new IRpcFunction.SubscribeCallback() {
                @Override
                public void callback(Object o) {
                    if (method instanceof StorageOnCallFunction && o instanceof List) {
                        subject.onNext(((List) o).get(0));
                    } else {
                        subject.onNext(o);
                    }
                }
            };
            //ret = publishSubject.publish().refCount();
            ret = subject;
        }

        List<Object> args = Lists.newArrayList();
        if (params != null) {
            args.addAll(params);
        }
        args.add(callback);

        Promise apply = method.apply(args.toArray(new Object[0]));

        if (ret != null) {
            //ret.doOnComplete(() -> System.out.println("doOnComplete"));
            //ret.doFinally(() -> System.out.println("doFinally"));
            //ret.doOnSubscribe((observer) -> System.out.println("doOnSubscribe " + observer));
            //ret.doOnTerminate(() -> System.out.println("doOnTerminate"));

            ret = ret.doOnDispose(new Action() {
                @Override
                public void run() throws Exception {
                    apply.then((result) -> {
                        IRpcFunction.Unsubscribe unsubscribe = (IRpcFunction.Unsubscribe) result;
                        logger.debug(" doOnDispose unsub");
                        unsubscribe.unsubscribe();
                        return null;
                    })._catch(err -> {
                        err.printStackTrace();
                        return null;
                    });
                }
            });
            return ret;
        }
        return RxUtils.fromPromise(apply);
    }
}
