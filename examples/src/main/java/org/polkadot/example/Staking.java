package org.polkadot.example;

import com.alibaba.fastjson.JSON;
import com.onehilltech.promises.Promise;
import org.polkadot.api.Types;
import org.polkadot.api.promise.ApiPromise;
import org.polkadot.rpc.provider.ws.WsProvider;

public class Staking {
    static String Alice = "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY";
    static String CHARLIE = "5FmE1Adpwp1bT1oY95w59RiSPVu9QwzBGjKsE2hxemD2AFs8";

    static String controller = "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY";


    //static String endPoint = "wss://poc3-rpc.polkadot.io/";
    //static String endPoint = "wss://substrate-rpc.parity.io/";
    //static String endPoint = "ws://45.76.157.229:9944/";
    static String endPoint = "ws://127.0.0.1:9944";

    static void initEndPoint(String[] args) {
        if (args != null && args.length >= 1) {
            endPoint = args[0];
            System.out.println(" connect to endpoint [" + endPoint + "]");
        } else {
            System.out.println(" connect to default endpoint [" + endPoint + "]");
        }
    }

    static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        // Create an await for the API
        //Promise<ApiPromise> ready = ApiPromise.create();
        initEndPoint(args);

        testValidators();

        waitLock();
        testNominators();

        waitLock();
        testLedger();

        waitLock();
        testBonded();
    }

    static void waitLock() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static void notifyLock() {
        synchronized (lock) {
            lock.notify();
        }
    }


    static void testValidators() {

        WsProvider wsProvider = new WsProvider(endPoint);

        Promise<ApiPromise> ready = ApiPromise.create(wsProvider);

        ready.then(api -> {

                    System.out.println("=========start testValidators=========");

                    Types.QueryableStorage query = api.query();
                    Types.QueryableModuleStorage staking = query.section("staking");

                    Types.QueryableStorageFunction validators = staking.function("validators");

                    return Promise.all(
                            validators.call(controller)
                    );
                }
        ).then(
                (result) -> {
                    for (Object ret : result) {
                        System.out.println("validators : " + ret);
                        System.out.println("validators : " + ret.getClass());
                        System.out.println("validators :\n " + JSON.toJSONString(ret, true));
                        System.out.println(" =============");
                    }

                    notifyLock();
                    return null;
                }
        )._catch((err) -> {
            err.printStackTrace();
            return Promise.value(err);
        });


    }

    public static void testNominators() {

        WsProvider wsProvider = new WsProvider(endPoint);

        Promise<ApiPromise> ready = ApiPromise.create(wsProvider);


        ready.then(api -> {

                    System.out.println("=========start testNominators =========");

                    Types.QueryableStorage query = api.query();
                    Types.QueryableModuleStorage staking = query.section("staking");

                    Types.QueryableStorageFunction nominators = staking.function("nominators");

                    return Promise.all(
                            nominators.call(controller)
                    );
                }
        ).then(
                (result) -> {
                    for (Object ret : result) {
                        System.out.println("nominators : " + ret);
                        System.out.println("nominators : " + ret.getClass());
                        System.out.println("nominators :\n " + JSON.toJSONString(ret, true));
                        System.out.println(" =============");
                    }

                    notifyLock();

                    return null;
                }
        )._catch((err) -> {
            err.printStackTrace();
            return Promise.value(err);
        });


    }

    public static void testLedger() {

        WsProvider wsProvider = new WsProvider(endPoint);

        Promise<ApiPromise> ready = ApiPromise.create(wsProvider);

        ready.then(api -> {

                    System.out.println("=========start testLedger=========");

                    Types.QueryableStorage query = api.query();
                    Types.QueryableModuleStorage staking = query.section("staking");
                    Types.QueryableStorageFunction ledger = staking.function("ledger");

                    return Promise.all(
                            ledger.call(controller)
                    );
                }
        ).then(
                (result) -> {
                    for (Object ret : result) {
                        System.out.println("ledger : " + ret);
                        System.out.println("ledger : " + ret.getClass());
                        System.out.println("ledger :\n " + JSON.toJSONString(ret, true));
                        System.out.println(" =============");
                    }
                    notifyLock();

                    return null;
                }
        )._catch((err) -> {
            err.printStackTrace();
            return Promise.value(err);
        });


    }

    public static void testBonded() {

        WsProvider wsProvider = new WsProvider(endPoint);

        Promise<ApiPromise> ready = ApiPromise.create(wsProvider);

        ready.then(api -> {

                    System.out.println("=========start testBonded=========");

                    Types.QueryableStorage query = api.query();
                    Types.QueryableModuleStorage staking = query.section("staking");

                    Types.QueryableStorageFunction bonded = staking.function("bonded");

                    return Promise.all(
                            bonded.call(controller)
                    );
                }
        ).then(
                (result) -> {
                    for (Object ret : result) {
                        System.out.println("bonded : " + ret);
                        System.out.println("bonded : " + ret.getClass());
                        System.out.println("bonded :\n " + JSON.toJSONString(ret, true));
                        System.out.println(" =============");
                    }
                    notifyLock();

                    return null;
                }
        )._catch((err) -> {
            err.printStackTrace();
            return Promise.value(err);
        });


    }
}
