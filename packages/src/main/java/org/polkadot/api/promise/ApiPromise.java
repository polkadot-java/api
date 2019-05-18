package org.polkadot.api.promise;

import com.onehilltech.promises.Promise;
import org.polkadot.api.ApiBase;
import org.polkadot.api.types.Types;
import org.polkadot.rpc.provider.IProvider;
import org.polkadot.rpc.provider.ws.WsProvider;
import org.polkadot.types.Types.CodecCallback;

import java.util.List;

public class ApiPromise extends ApiBase {

    private Promise isReadyPromise;

    public static Promise<ApiPromise> create(IProvider iProvider) {
        ApiPromise apiPromise = new ApiPromise(iProvider);
        return apiPromise.isReadyPromise;
    }


    public static Promise<ApiPromise> create() {
        ApiPromise apiPromise = new ApiPromise(new WsProvider());
        return apiPromise.isReadyPromise;
    }

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
