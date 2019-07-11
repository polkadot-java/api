package test.org.polkadot.api;

import com.alibaba.fastjson.JSON;
import com.onehilltech.promises.Promise;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.polkadot.api.promise.ApiPromise;
import org.polkadot.common.keyring.Types;
import org.polkadot.direct.IRpcFunction;
import org.polkadot.example.TestingPairs;
import org.polkadot.rpc.provider.ws.WsProvider;
import org.polkadot.types.Codec;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.polkadot.utils.crypto.Types.KeypairType_ED;

public class PromiseAlex {


    private ApiPromise api;

    private AtomicBoolean sync = new AtomicBoolean(false);

    private Map<String, Types.KeyringPair> keyring;
    static String endPoint = "ws://127.0.0.1:9944";
    //static String endPoint = "wss://poc3-rpc.polkadot.io/";

    @Before
    public void initApi() {
        System.out.println("========== run Before ");

        Object lock = new Object();

        WsProvider wsProvider = new WsProvider(endPoint);

        Promise<ApiPromise> ready = ApiPromise.create(wsProvider);

        ready.then(api -> {
            this.api = api;
            synchronized (lock) {
                lock.notify();
            }
            return null;

        });

        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        keyring = TestingPairs.testKeyringPairs(KeypairType_ED);
    }

    @AfterClass
    public static void waitAllFinish() {
        System.out.println("========== run waitAllFinish ");
        try {
            Thread.sleep(10000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void waitFinish() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void retrievesTheListOfValidators() {
        System.out.println("========== run retrievesTheListOfValidators ");

        api.query().section("staking").function("validators").call(
                (IRpcFunction.SubscribeCallback<Object>) (Object result) ->
                {
                    //System.out.println("retrievesTheListOfValidators result: " + result + " : " + result.getClass());
                    System.out.println("api.query.staking.validators(): " + ((Codec) result).toJson());
                    System.exit(0);
                }
        );

        waitFinish();
    }

    @Test
    public void retrievesASingleValue() {
        System.out.println("========== run retrievesASingleValue ");


        api.query().section("staking").function("validators").call(
                "0xa62601bb840b2ee9ae90cbc8aefa0d01",
                (IRpcFunction.SubscribeCallback<Object>) (Object result) ->
                {
                    //System.out.println("Chain is at block: " + JSON.toJSONString(header));
                    System.out.println("api.query.staking.validators(id):" + ((Codec) result).toJson());

                    System.exit(0);
                }
        );

        waitFinish();
    }

    @Test
    public void derivesAListOfTheControllers() {
        System.out.println("========== run derivesAListOfTheControllers ");

        api.derive().section("staking").function("controllers").call(
                (IRpcFunction.SubscribeCallback<Object>) (Object result) ->
                {
                    //System.out.println("Chain is at block: " + JSON.toJSONString(header));
                    System.out.println("api.derive.staking.controllers: result: " + JSON.toJSONString(result, true) + " : " + result.getClass());
                    System.exit(0);
                }
        );

        waitFinish();
    }

    @Test
    @Ignore
    public void retrieveTheListOfNominators() {
        System.out.println("========== run retrieveTheListOfNominators ");

        api.query().section("staking").function("nominators").call(
                (IRpcFunction.SubscribeCallback<Object>) (Object result) ->
                {
                    //System.out.println("Chain is at block: " + JSON.toJSONString(header));
                    System.out.println("retrieveTheListOfNominators result: " + result + " : " + result.getClass());

                    System.exit(0);
                }
        );

        waitFinish();
    }
}
