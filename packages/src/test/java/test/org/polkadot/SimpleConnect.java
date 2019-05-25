package test.org.polkadot;

import com.onehilltech.promises.Promise;
import org.polkadot.api.promise.ApiPromise;
import org.polkadot.rpc.provider.ws.WsProvider;

public class SimpleConnect {
    public static void main(String[] args) {
        WsProvider wsProvider = new WsProvider("ws://127.0.0.1:9944");

        Promise<ApiPromise> ready = ApiPromise.create(wsProvider);
        ready.then(api -> Promise.all(
                api.rpc().system().function("chain").invoke(),
                api.rpc().system().function("name").invoke(),
                api.rpc().system().function("version").invoke()
        )).then((results) -> {
            System.out.println("You are connected to chain [" + results.get(0) + "] using [" + results.get(1) + "] v[" + results.get(2) + "]");
            return null;
        })._catch((err) -> {
            err.printStackTrace();
            return null;
        });

    }
}
