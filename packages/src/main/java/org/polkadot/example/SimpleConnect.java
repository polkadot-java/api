package org.polkadot.example;

import com.alibaba.fastjson.JSON;
import com.onehilltech.promises.Promise;
import org.polkadot.api.promise.ApiPromise;
import org.polkadot.rpc.provider.ws.WsProvider;

public class SimpleConnect {
    public static void main(String[] args) {
        WsProvider wsProvider = new WsProvider("ws://127.0.0.1:9944");

        Promise<ApiPromise> ready = ApiPromise.create(wsProvider);


        ready.then(api -> {
            return Promise.all(
                    //api.rpc().system().function("chain").invoke(),
                    //api.rpc().system().function("name").invoke(),
                    //api.rpc().system().function("version").invoke()
                    api.rpc().state().function("getMetadata").invoke()
                    //api.rpc().chain().function("getRuntimeVersion").invoke(),
                    //api.rpc().chain().function("getBlockHash").invoke(0)


                    //        this._runtimeMetadata = await this._rpcBase.state.getMetadata();
                    //this._runtimeVersion = await this._rpcBase.chain.getRuntimeVersion();
                    //this._genesisHash = await this._rpcBase.chain.getBlockHash(0);
            );
            //IRpcFunction chain = api.rpc().system().function("chain");
            //IRpcFunction name = api.rpc().system().function("name");
            //IRpcFunction version = api.rpc().system().function("version");
            //Promise invoke = chain.invoke();
            //return invoke;
        }).then((results) -> {
            for (Object result : results) {
                System.out.println(result);
                System.out.println(result);
                System.out.println(JSON.toJSONString(ready, true));
            }
            System.out.println("You are connected to chain [" + results.get(0) + "] using [" + results.get(1) + "] v[" + results.get(2) + "]");
            return null;
        })._catch((err) -> {
            err.printStackTrace();
            return null;
        });
        //
        //ready.then(api -> {
        //    IRpcFunction chain = api.rpc().system().function("chain");
        //    Promise invoke = chain.invoke();
        //    return invoke;
        //}).then((Object codec) -> {
        //    System.out.println(" chain : " + codec);
        //    return null;
        //});


        //ready.then(api -> {
        //    IRpcFunction chain = api.rpc().system().function("chain");
        //    Promise<IRpcFunction.Unsubscribe> invoke = chain.invoke(1, 2, 4,
        //            (IRpcFunction.SubscribeCallback<Text>) (Text o) -> {
        //                System.out.println(o);
        //            }
        //    );
        //    return invoke;
        //}).then((IRpcFunction.Unsubscribe callback) -> Promise.value(callback.unsubscribe()));

        //apiPromisePromise.then((api) -> {
        //    api.getRpc().getSystem().getMethod("chain").invoke1(null);
        //    return Promise.value(null);
        //});

    }
}
