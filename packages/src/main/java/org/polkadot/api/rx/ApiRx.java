package org.polkadot.api.rx;

import io.reactivex.Observable;
import org.polkadot.api.ApiBase;
import org.polkadot.api.types.Types;
import org.polkadot.rpc.provider.IProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

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

    public static Observable<ApiRx> create(IProvider provider) {
        ApiRx apiRx = new ApiRx(provider);
        return apiRx.isReadyRx;
    }

    @Override
    protected Types.BaseResult onCall(Types.OnCallFunction method, List params, org.polkadot.types.Types.CodecCallback callback, boolean needsCallback) {
        return null;
    }
}
