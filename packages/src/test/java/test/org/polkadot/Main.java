package test.org.polkadot;

import com.alibaba.fastjson.JSON;
import com.onehilltech.promises.Promise;
import org.polkadot.rpc.provider.IProvider;
import org.polkadot.rpc.provider.coder.RpcCoder;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class Main {
    static WsProviderDir wsProvider;

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        wsProvider = new WsProviderDir("ws://127.0.0.1:9944");
        wsProvider.connect();
        while (!wsProvider.isConnected()) {
            Thread.sleep(100);
        }



        test1();

    }

    static void test3() {

        String methodName = "state_getMetadata";

        Promise subscribe = wsProvider.send(methodName, new ArrayList<>(), null);
        subscribe.then(value -> {
            System.out.println(" then 1111onResolved " + value);
            //JSONObject jsonObject = JSONObject.parseObject((String) value);
            String s = JSON.toJSONString(value, true);
            System.out.println(s);
            return null;
        }).then(value -> {
            System.out.println(" then 2222 onResolved " + value);
            return null;
        })._catch((error) -> {
            System.out.println(" get error  " + error);
            return null;
        });
    }

    static void test2() {
        /*
const subscribeNewHead: RpcMethodOpt = {
                description: 'Retrieves the best header via subscription',
                params: [],
        pubsub: [
        'newHead',
                'subscribeNewHead',
                'unsubscribeNewHead'
  ],
        type: 'Header'
};
*/


        String subName = "chain_subscribeNewHead";
        String unsubName = "chain_unsubscribeNewHead";
        String subType = "chain_newHead";

        RpcCoder rpcCoder = new RpcCoder();


        String rpcStr = rpcCoder.encodeJson("chain_subscribeNewHead", new ArrayList<>());
        System.out.println(rpcStr);
        //wsProvider.send(rpcStr);

        IProvider.CallbackHandler<Object, Object> objectObjectCallbackHandler = (o, o2) -> {
            System.out.println(" callback " + o + " " + o2);
        };

        Promise subscribe = wsProvider.subscribe(subType, subName, new ArrayList<>(), objectObjectCallbackHandler);
        subscribe.then(value -> {
            System.out.println(" then 1111onResolved " + value);
            return null;
        }).then(value -> {
            System.out.println(" then 2222 onResolved " + value);
            return null;
        });
    }

    static void test1() throws URISyntaxException, InterruptedException {
        //wsProvider.send(" test str");

        RpcCoder rpcCoder = new RpcCoder();

        wsProvider.send("system_name", new ArrayList<>(), null);
        wsProvider.send("system_chain", new ArrayList<>(), null);
        wsProvider.send("system_version", new ArrayList<>(), null);


    }

}
