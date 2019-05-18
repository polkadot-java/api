package org.polkadot.rpc.provider;

import com.onehilltech.promises.Promise;
import org.polkadot.common.EventEmitter;

import java.util.List;

public interface IProvider {


    @FunctionalInterface
    interface CallbackHandler<T, U> {

        /**
         * Performs this operation on the given arguments.
         *
         * @param t the first input argument
         * @param u the second input argument
         */
        void callback(T t, U u);
    }


    class SubscriptionHandler {
        CallbackHandler<Object, Object> callBack;
        String type;

        public SubscriptionHandler() {
        }

        public SubscriptionHandler(CallbackHandler<Object, Object> callBack, String type) {
            this.callBack = callBack;
            this.type = type;
        }

        public CallbackHandler<Object, Object> getCallBack() {
            return callBack;
        }

        public void setCallBack(CallbackHandler<Object, Object> callBack) {
            this.callBack = callBack;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }


    @FunctionalInterface
    interface ProviderInterfaceEmitCb {
        void handler(Object... value);
    }


    //export type ProviderInterface$Emitted = 'connected' | 'disconnected' | 'error';
    //export type RpcRxInterface$Events = ProviderInterface$Emitted;
    //export type ApiInterface$Events = RpcRxInterface$Events | 'ready';

    enum ProviderInterfaceEmitted implements EventEmitter.EventType {
        // 'connected' | 'disconnected' | 'error';
        ready,
        connected,
        disconnected,
        error
    }

    boolean isHasSubscriptions();

    IProvider clone();

    void disconnect();

    boolean isConnected();

    //TODO 2019-04-26 15:09
    void on(ProviderInterfaceEmitted emitted, EventEmitter.EventListener cb);

    Promise<String> send(String method, List<Object> params, SubscriptionHandler subscriptionHandler);

    //TODO 2019-04-26 15:10
    Promise<String> subscribe(String type, String method, List<Object> params, CallbackHandler cb);

    //TODO 2019-04-26 15:10
    Promise<String> unsubscribe(String type, String method, int id);
    //readonly hasSubscriptions: boolean;
    //clone (): ProviderInterface;
    //disconnect (): void;
    //isConnected (): boolean;
    //export type ProviderInterface$Emitted = 'connected' | 'disconnected' | 'error';


    //on (type: ProviderInterface$Emitted, sub: ProviderInterface$EmitCb): void;
    //send (method: string, params: Array<any>): Promise<any>;
    //subscribe (type: string, method: string, params: Array<any>, cb: ProviderInterface$Callback): Promise<number>;
    //unsubscribe (type: string, method: string, id: number): Promise<boolean>;
}
