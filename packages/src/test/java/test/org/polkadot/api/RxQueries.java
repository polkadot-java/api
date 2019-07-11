package test.org.polkadot.api;

import io.reactivex.Observable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.polkadot.api.rx.ApiRx;
import org.polkadot.common.keyring.Types;
import org.polkadot.example.TestingPairs;
import org.polkadot.rpc.provider.ws.WsProvider;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.type.Balance;
import org.polkadot.types.type.Header;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.polkadot.utils.crypto.Types.KeypairType_ED;

public class RxQueries {

    private ApiRx api;

    private AtomicBoolean sync = new AtomicBoolean(false);

    private Map<String, Types.KeyringPair> keyring;
    static String endPoint = "ws://127.0.0.1:9944";

    static Object testSync = new Object();

    @Before
    public void initApi() {
        System.out.println("========== run Before ");

        WsProvider wsProvider = new WsProvider(endPoint);

        Observable<ApiRx> ready = ApiRx.create(wsProvider);

        CountDownLatch countDownLatch = new CountDownLatch(1);

        ready.subscribe((apiRx) -> {
            this.api = apiRx;
            countDownLatch.countDown();
            //System.out.println("freeBalance result " + apiRx);
        });


        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        keyring = TestingPairs.testKeyringPairs(KeypairType_ED);
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

    }


    public void waitFinish() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void queriesStateForABalance() {

        api.query()
                .section("balances")
                .function("freeBalance")
                .call(keyring.get("alice").address())
                .subscribe((result) -> {

                    Balance balance = (Balance) result;
                    System.out.println("freeBalance result " + result + " " + result.getClass());

                    Assert.assertTrue(balance.compareTo(BigInteger.ZERO) > 0);


                });

        waitFinish();
    }

    @Test
    public void makesAQueryAtASpecificBlock() {

        api.rpc()
                .chain()
                .function("getHeader")
                .invoke()
                .switchMap((result) -> {
                    Header header = (Header) result;
                    System.out.println(header + ":\n" + header.getHash());
                    //api.query.system.events.at( header.hash)
                    return api.query()
                            .section("system")
                            .function("events")
                            .at(header.getHash(), null);

                }).subscribe((result) -> {
            Vector<?> events = (Vector<?>) result;
            Assert.assertTrue(events.size() > 0);
        });

        waitFinish();
    }


}
