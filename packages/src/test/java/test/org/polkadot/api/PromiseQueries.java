package test.org.polkadot.api;

import com.onehilltech.promises.Promise;
import org.junit.*;
import org.polkadot.api.promise.ApiPromise;
import org.polkadot.common.keyring.Types;
import org.polkadot.direct.IRpcFunction;
import org.polkadot.example.TestingPairs;
import org.polkadot.rpc.provider.ws.WsProvider;
import org.polkadot.types.codec.Tuple;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.type.Balance;
import org.polkadot.types.type.Header;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.polkadot.utils.crypto.Types.KeypairType_ED;

public class PromiseQueries {

    private ApiPromise api;

    private AtomicBoolean sync = new AtomicBoolean(false);

    private Map<String, Types.KeyringPair> keyring;
    static String endPoint = "ws://127.0.0.1:9944";

    static Object testSync = new Object();

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


    @Test
    public void checkNotNull() {
        System.out.println("========== run checkNotNull ");

        Assert.assertNotNull(api.getGenesisHash());
        Assert.assertNotNull(api.runtimeMetadata());
        Assert.assertNotNull(api.runtimeVersion);
        Assert.assertNotNull(api.rpc());
        Assert.assertNotNull(api.query());
        Assert.assertNotNull(api.tx());
        Assert.assertNotNull(api.derive());

        waitFinish();
    }

    public void done() {
        System.out.println(" done ");
        synchronized (testSync) {
            testSync.notify();
        }
    }

    public void waitFinish() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void queryStateForBalance() {
        System.out.println("========== run queryStateForBalance ");


        Types.KeyringPair alice = keyring.get("alice");


        api.query().section("balances").function("freeBalance").call(alice.address())
                .then((result) -> {

                    Assert.assertNotEquals(0, ((Balance) result).compareTo(BigInteger.ZERO));
                    System.out.println(result + " : " + result.getClass());

                    done();
                    return null;
                })
                ._catch((err) -> {
                    err.printStackTrace();
                    return null;
                });


        waitFinish();
    }


    @Test
    public void subscribesToRpc() {
        System.out.println("========== run subscribesToRpc ");

        api.rpc().chain().function("subscribeNewHead").invoke(
                (IRpcFunction.SubscribeCallback<Header>) (Header header) ->
                {
                    //System.out.println("Chain is at block: " + JSON.toJSONString(header));
                    System.out.println("subscribeNewHead Chain is at block: " + header.getBlockNumber());
                    Assert.assertNotEquals(0, header.getBlockNumber().compareTo(BigInteger.ZERO));

                    done();
                });

        waitFinish();
    }

    @Test
    public void subscribesToFinalized() {
        System.out.println("========== run subscribesToFinalized ");

        api.rpc().chain().function("subscribeFinalizedHeads").invoke(
                (IRpcFunction.SubscribeCallback<Header>) (Header header) ->
                {
                    //System.out.println("Chain is at block: " + JSON.toJSONString(header));
                    System.out.println("subscribesToFinalized Chain is at block: " + header.getBlockNumber());
                    Assert.assertNotEquals(0, header.getBlockNumber().compareTo(BigInteger.ZERO));

                    done();
                });

        waitFinish();
    }

    @Test
    public void subscribesToDerive() {
        System.out.println("========== run subscribesToDerive ");

        api.derive().section("chain").function("subscribeNewHead").call(
                (IRpcFunction.SubscribeCallback<Header>) (Header header) ->
                {
                    //System.out.println("Chain is at block: " + JSON.toJSONString(header));
                    System.out.println("subscribesToDerive Chain is at block: " + header.getBlockNumber());
                    Assert.assertNotEquals(0, header.getBlockNumber().compareTo(BigInteger.ZERO));

                    done();
                });

        waitFinish();
    }


    @Test
    public void subscribesToQueries() {
        System.out.println("========== run subscribesToQueries ");

        api.query().section("system").function("accountNonce").call(keyring.get("ferdie").address(),
                (IRpcFunction.SubscribeCallback<Object>) (Object nonce) ->
                {
                    //System.out.println("Chain is at block: " + JSON.toJSONString(header));
                    System.out.println("subscribesToQueries Nonce: " + nonce + " : " + nonce.getClass());
                    Assert.assertTrue(nonce instanceof BigInteger);

                    done();
                });

        waitFinish();
    }

    @Test
    public void subscribesToQueriesDefault() {
        System.out.println("========== run subscribesToQueriesDefault ");

        api.query().section("staking").function("validators").call(keyring.get("ferdie").address(),
                (IRpcFunction.SubscribeCallback<Object>) (Object prefs) ->
                {
                    //System.out.println("Chain is at block: " + JSON.toJSONString(header));
                    System.out.println("subscribesToQueriesDefault prefs: " + prefs + " : " + prefs.getClass());
                    Tuple tuple = (Tuple) prefs;

                    done();
                });


        waitFinish();
    }


    @Test
    public void makeAQueryAtASpecificBlock() {
        System.out.println("========== run makeAQueryAtASpecificBlock ");

        api.rpc().chain().function("getHeader").invoke()
                .then((result) -> {
                    //System.out.println("1111" + result + " : " + result.getClass());
                    Header header = (Header) result;
                    return api.query().section("system").function("events").at(header.getHash(), null);
                })
                .then((result) -> {
                    //System.out.println("2222" + result + " : " + result.getClass());
                    Vector events = (Vector) result;
                    Assert.assertNotEquals(events.size(), 0);

                    done();
                    return null;
                });

        waitFinish();
    }


}
