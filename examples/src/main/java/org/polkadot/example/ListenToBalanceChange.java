package org.polkadot.example;

import com.onehilltech.promises.Promise;
import org.polkadot.api.Types.QueryableModuleStorage;
import org.polkadot.api.Types.QueryableStorage;
import org.polkadot.api.Types.QueryableStorageFunction;
import org.polkadot.api.promise.ApiPromise;
import org.polkadot.direct.IRpcFunction;
import org.polkadot.rpc.provider.ws.WsProvider;


public class ListenToBalanceChange {
    static String Alice = "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY";

    static String endPoint = "wss://poc3-rpc.polkadot.io/";
    //static String endPoint = "wss://substrate-rpc.parity.io/";
    //static String endPoint = "ws://45.76.157.229:9944/";
    //static String endPoint = "ws://127.0.0.1:9944";

    static void initEndPoint(String[] args) {
        if (args != null && args.length >= 1) {
            endPoint = args[0];
            System.out.println(" connect to endpoint [" + endPoint + "]");
        } else {
            System.out.println(" connect to default endpoint [" + endPoint + "]");
        }
    }

    public static void main(String[] args) {
        // Create an await for the API
        //Promise<ApiPromise> ready = ApiPromise.create();
        initEndPoint(args);

        WsProvider wsProvider = new WsProvider(endPoint);

        Promise<ApiPromise> ready = ApiPromise.create(wsProvider);

        ready.then(api -> {
            // Retrieve the initial balance. Since the call has no callback, it is simply a promise
            // that resolves to the current on-chain value
            QueryableStorage query = api.query();
            QueryableModuleStorage balances = query.section("balances");
            QueryableStorageFunction freeBalance = balances.function("freeBalance");
            return (Promise<IRpcFunction.Unsubscribe<Promise>>) freeBalance.call(Alice, (IRpcFunction.SubscribeCallback) o -> System.out.println("freeBalance result " + o)
            );
        }).then((IRpcFunction.Unsubscribe<Promise> result) -> {
                    System.out.println(" set unsubscribe " + result);
                    return null;
                }
        )._catch((err) -> {
            err.printStackTrace();
            return Promise.value(err);
        });


        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
