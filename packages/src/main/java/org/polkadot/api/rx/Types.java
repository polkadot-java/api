package org.polkadot.api.rx;

import io.reactivex.Observable;
import org.polkadot.api.ApiBase;
import org.polkadot.rpc.provider.IProvider;
import org.polkadot.types.Codec;

public interface Types {


    //export type RxResult = Observable<Codec>;

    abstract class RxResult extends Observable<Codec> {

    }
    //
    //export interface ApiRxInterface extends ApiBase<RxResult, RxResult> {
    //    readonly isConnected: Observable<boolean>;
    //    readonly isReady: Observable<ApiRxInterface>;
    //}

    abstract class ApiRxInterface extends ApiBase<RxResult, RxResult> {


        private Observable<Boolean> isConnected;
        private Observable<ApiRxInterface> isReady;

        public ApiRxInterface(IProvider provider, ApiType apiType) {
            super(provider, apiType);
        }

        public Observable<Boolean> getIsConnected() {
            return isConnected;
        }

        public Observable<ApiRxInterface> getIsReady() {
            return isReady;
        }
    }
}
