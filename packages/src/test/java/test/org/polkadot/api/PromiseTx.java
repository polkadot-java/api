package test.org.polkadot.api;

import com.onehilltech.promises.Promise;
import org.polkadot.api.SubmittableExtrinsic;
import org.polkadot.api.promise.ApiPromise;
import org.polkadot.common.keyring.Types;
import org.polkadot.example.TestingPairs;
import org.polkadot.rpc.provider.ws.WsProvider;
import org.polkadot.types.rpc.ExtrinsicStatus;
import org.polkadot.types.type.Event;
import org.polkadot.types.type.EventRecord;
import org.polkadot.utils.UtilsCrypto;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.polkadot.utils.crypto.Types.KeypairType_SR;

public class PromiseTx {
    static String endPoint = "ws://127.0.0.1:9944";
    static ApiPromise api;
    static Map<String, Types.KeyringPair> keyring;

    //-Djava.library.path=./libs
    public static void main(String[] args) {
        initApi();

        //makeTransferSignThenSend();
        makeAProposal();
    }

    static void initApi() {
        System.out.println("========== run Before ");

        WsProvider wsProvider = new WsProvider(endPoint);
        Promise<ApiPromise> ready = ApiPromise.create(wsProvider);

        CountDownLatch countDownLatch = new CountDownLatch(1);

        ready.then(readyApi -> {
            api = readyApi;
            countDownLatch.countDown();
            return null;
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        keyring = TestingPairs.testKeyringPairs(KeypairType_SR);
    }


    static void makeATransferAndUseNewBalanceToTransfersToNew() {
        ((Promise) api.tx()
                .section("balances")
                .function("transfer")
                .call(keyring.get("bob").address(), 123)
                .signAndSendCb(keyring.get("alice"), new SubmittableExtrinsic.StatusCb() {
                    @Override
                    public Object callback(SubmittableExtrinsic.SubmittableResult result) {
                        List<EventRecord> events = result.getEvents();
                        ExtrinsicStatus status = result.getStatus();

                        System.out.println("Transaction status:" + status.getType());

                        if (status.isFinalized()) {
                            System.out.println("Completed at block hash " + status.value().toHex());
                            System.out.println("Events:");

                            for (EventRecord event : events) {
                                Event eventEvent = event.getEvent();
                                System.out.println("\t" + event.getPhase().toString()
                                        + ": " + eventEvent.getSection() + "." + eventEvent.getMethod()
                                        + " " + eventEvent.getData().toString());
                            }

                            ///////
                            api.tx()
                                    .section("balances")
                                    .function("transfer")
                                    .call(keyring.get("alice").address(), 123)
                                    .signAndSendCb(keyring.get("dave"), new SubmittableExtrinsic.StatusCb() {
                                        @Override
                                        public Object callback(SubmittableExtrinsic.SubmittableResult result) {
                                            List<EventRecord> events = result.getEvents();
                                            ExtrinsicStatus status = result.getStatus();

                                            System.out.println("Transaction status:" + status.getType());

                                            if (status.isFinalized()) {
                                                System.out.println("Completed at block hash " + status.value().toHex());
                                                System.out.println("Events:");

                                                for (EventRecord event : events) {
                                                    Event eventEvent = event.getEvent();
                                                    System.out.println("\t" + event.getPhase().toString()
                                                            + ": " + eventEvent.getSection() + "." + eventEvent.getMethod()
                                                            + " " + eventEvent.getData().toString());
                                                }
                                                ///////
                                                System.exit(0);

                                            }
                                            return null;
                                        }
                                    });

                        }
                        return null;
                    }
                }))
                .then(result -> {
                    System.out.println("result : " + result);
                    return null;
                })
                ._catch(err -> {
                    err.printStackTrace();
                    return null;
                });
    }


    static void makeAProposal() {
        ((Promise) api
                .tx()
                .section("democracy")
                .function("propose")
                .call(
                        api.tx().section("consensus").function("setCode").call(UtilsCrypto.randomAsHex(4096)),
                        10000
                )
                .signAndSend(keyring.get("alice"), new org.polkadot.types.Types.SignatureOptions()))
                .then(result -> {
                    System.out.println("result : " + result);
                    return null;
                })
                ._catch(err -> {
                    err.printStackTrace();
                    return null;
                });
    }

    static void makeTransferSignAndSendNoCb() {
        ((Promise) api
                .tx()
                .section("balances")
                .function("transfer")
                .call(keyring.get("bob").address(), 123)
                .signAndSendCb(keyring.get("alice"), null))
                .then(result -> {
                    System.out.println("result : " + result);
                    return null;
                })
                ._catch(err -> {
                    err.printStackTrace();
                    return null;
                });
    }


    static void makeTransferSignAndSendViaSignerSadPath() {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        CountDownLatch finalCountDownLatch = countDownLatch;
        api.setSigner(null);
        ((Promise) api
                .tx()
                .section("balances")
                .function("transfer")
                .call(keyring.get("bob").address(), 123)
                .signAndSendCb(keyring.get("alice").address(), null))
                .then(result -> {
                    finalCountDownLatch.countDown();
                    return null;
                })
                ._catch(err -> {
                    err.printStackTrace();
                    finalCountDownLatch.countDown();
                    return null;
                });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        countDownLatch = new CountDownLatch(1);
        CountDownLatch finalCountDownLatch2 = countDownLatch;

        SingleAccountSigner signer = new SingleAccountSigner(keyring.get("dave"));
        ///// no cb
        api.setSigner(signer);
        ((Promise) api
                .tx()
                .section("balances")
                .function("transfer")
                .call(keyring.get("eve").address(), 123)
                .signAndSendCb(keyring.get("alice").address(), null))
                .then(result -> {
                    finalCountDownLatch2.countDown();
                    return null;
                })
                ._catch(err -> {
                    err.printStackTrace();
                    finalCountDownLatch2.countDown();
                    return null;
                });

        try {
            finalCountDownLatch2.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        countDownLatch = new CountDownLatch(1);
        CountDownLatch finalCountDownLatch3 = countDownLatch;


        ///// witch cb
        api.setSigner(signer);
        ((Promise) api
                .tx()
                .section("balances")
                .function("transfer")
                .call(keyring.get("eve").address(), 123)
                .signAndSendCb(keyring.get("alice").address(), new SubmittableExtrinsic.StatusCb() {
                    @Override
                    public Object callback(SubmittableExtrinsic.SubmittableResult result) {
                        return null;
                    }
                }))
                .then(result -> {
                    finalCountDownLatch3.countDown();
                    return null;
                })
                ._catch(err -> {
                    err.printStackTrace();
                    finalCountDownLatch3.countDown();
                    return null;
                });


        try {
            finalCountDownLatch3.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void makeTransferSignAndSendViaSigner() {
        SingleAccountSigner signer = new SingleAccountSigner(keyring.get("alice"));
        api.setSigner(signer);

        System.out.println(" setSigner ");

        ((Promise) api
                .tx()
                .section("balances")
                .function("transfer")
                .call(keyring.get("bob").address(), 123)
                .signAndSendCb(keyring.get("alice").address(), new SubmittableExtrinsic.StatusCb() {
                    @Override
                    public Object callback(SubmittableExtrinsic.SubmittableResult result) {
                        return logSubmittableResult(result);
                    }
                }))
                .then(result -> {
                    System.out.println("result : " + result);
                    return null;
                })
                ._catch(err -> {
                    err.printStackTrace();
                    return null;
                });
    }


    static void makeTransferSignAndSend() {
        ((Promise) api
                .tx()
                .section("balances")
                .function("transfer")
                .call(keyring.get("bob").address(), 123)
                .signAndSendCb(keyring.get("alice"), new SubmittableExtrinsic.StatusCb() {
                    @Override
                    public Object callback(SubmittableExtrinsic.SubmittableResult result) {
                        return logSubmittableResult(result);
                    }
                }))
                .then(result -> {
                    System.out.println("result : " + result);
                    return null;
                })
                ._catch(err -> {
                    err.printStackTrace();
                    return null;
                });
    }

    static void makeTransferSignThenSendCompat() {
    }


    static void makeTransferSignThenSend() {
        api.query()
                .section("system")
                .function("accountNonce")
                .call(keyring.get("alice").address())
                .then((nonce) -> {
                    System.out.println("nonce  " + nonce);

                    org.polkadot.types.Types.IExtrinsic sign = api.tx()
                            .section("balances")
                            .function("transfer")
                            .call(keyring.get("bob").address(), 123)
                            .sign(keyring.get("alice"), new org.polkadot.types.Types.SignatureOptions().setNonce(nonce));
                    SubmittableExtrinsic<Promise> sign1 = (SubmittableExtrinsic) sign;
                    sign1.send(new SubmittableExtrinsic.StatusCb() {
                        @Override
                        public Object callback(SubmittableExtrinsic.SubmittableResult result) {
                            return logSubmittableResult(result);
                        }
                    });
                    return null;
                })
                ._catch(err -> {
                    err.printStackTrace();
                    return null;
                });


    }

    private static Object logSubmittableResult(SubmittableExtrinsic.SubmittableResult result) {
        List<EventRecord> events = result.getEvents();
        ExtrinsicStatus status = result.getStatus();

        System.out.println("Transaction status:" + status.getType());

        if (status.isFinalized()) {
            System.out.println("Completed at block hash " + status.value().toHex());
            System.out.println("Events:");

            for (EventRecord event : events) {
                Event eventEvent = event.getEvent();
                System.out.println("\t" + event.getPhase().toString()
                        + ": " + eventEvent.getSection() + "." + eventEvent.getMethod()
                        + " " + eventEvent.getData().toString());
            }
            System.exit(0);
        }

        return null;
    }
}
