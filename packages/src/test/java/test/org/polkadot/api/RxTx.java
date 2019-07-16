package test.org.polkadot.api;

import com.onehilltech.promises.Promise;
import io.reactivex.Observable;
import org.polkadot.api.SubmittableExtrinsic;
import org.polkadot.api.rx.ApiRx;
import org.polkadot.common.keyring.Types;
import org.polkadot.example.TestingPairs;
import org.polkadot.rpc.provider.ws.WsProvider;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.polkadot.utils.crypto.Types.KeypairType_SR;

public class RxTx {

    static ApiRx api;

    static Map<String, Types.KeyringPair> keyring;
    static String endPoint = "ws://127.0.0.1:9944";

    static Object testSync = new Object();

    public static void initApi() {
        System.out.println("========== run Before ");

        WsProvider wsProvider = new WsProvider(endPoint);

        Observable<ApiRx> ready = ApiRx.create(wsProvider);

        CountDownLatch countDownLatch = new CountDownLatch(1);

        ready.subscribe((apiRx) -> {
            api = apiRx;
            countDownLatch.countDown();
            //System.out.println("freeBalance result " + apiRx);
        });


        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        keyring = TestingPairs.testKeyringPairs(KeypairType_SR);
    }

    //-Djava.library.path=./libs
    public static void main(String[] args) {
        initApi();

        makeTransfer();
        //makeProposal();
    }

    //@After
    public void releaseTest() {
        System.out.println("========== run After ");

        synchronized (testSync) {
            try {
                testSync.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("========== After After ");

    }

    static void makeTransfer() {

        System.out.println(" ======= makeTransfer");

        api.query()
                .section("system")
                .function("accountNonce")
                .call(keyring.get("alice").address())
                .switchMap((nonce) -> {

                    System.out.println(" nonce " + nonce);

                    org.polkadot.types.Types.IExtrinsic sign = api.tx()
                            .section("balances")
                            .function("transfer")
                            .call(keyring.get("bob").address(), 123)
                            .sign(keyring.get("alice"), new org.polkadot.types.Types.SignatureOptions().setNonce(nonce));

                    SubmittableExtrinsic<Promise> sign1 = (SubmittableExtrinsic) sign;
                    return sign1.send();
                })
                .subscribe((result) -> {
                    SubmittableExtrinsic.SubmittableResult submittableResult = (SubmittableExtrinsic.SubmittableResult) result;
                    System.out.println("result  " + result + " \n " + result.getClass());
                    System.out.println("status  " + submittableResult.getStatus());
                    if (submittableResult.getStatus().isFinalized()) {
                        System.exit(0);
                    }
                });
    }

    static void makeProposal() {

        System.out.println(" ======= makeTransfer");

        api.query()
                .section("system")
                .function("accountNonce")
                .call(keyring.get("alice").address())
                .switchMap((nonce) -> {

                    System.out.println(" nonce " + nonce);

                    org.polkadot.types.Types.IExtrinsic sign = api.tx()
                            .section("democracy")
                            .function("propose")
                            .call(
                                    api.tx().section("consensus").function("setCode").call("0xdeadbeef"),
                                    10000
                            )
                            .sign(keyring.get("alice"), new org.polkadot.types.Types.SignatureOptions().setNonce(nonce));

                    SubmittableExtrinsic<Promise> sign1 = (SubmittableExtrinsic) sign;
                    return sign1.send();
                })
                .subscribe((result) -> {
                    SubmittableExtrinsic.SubmittableResult submittableResult = (SubmittableExtrinsic.SubmittableResult) result;
                    System.out.println("result  " + result + " \n " + result.getClass());
                    System.out.println("status  " + submittableResult.getStatus());
                    if (submittableResult.getStatus().isFinalized()) {
                        System.exit(0);
                    }
                });
    }

    public void waitFinish() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
