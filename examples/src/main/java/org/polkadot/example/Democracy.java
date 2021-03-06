package org.polkadot.example;

import com.onehilltech.promises.Promise;
import org.polkadot.api.SubmittableExtrinsic;
import org.polkadot.api.Types;
import org.polkadot.api.promise.ApiPromise;
import org.polkadot.direct.IRpcFunction;
import org.polkadot.rpc.provider.ws.WsProvider;
import org.polkadot.types.rpc.ExtrinsicStatus;
import org.polkadot.types.type.BlockNumber;
import org.polkadot.types.type.Event;
import org.polkadot.types.type.EventRecord;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Democracy {
    static String Alice = "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY";

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

    public static void main(String[] args) throws InterruptedException {
        // Create an await for the API
        //Promise<ApiPromise> ready = ApiPromise.create();
        initEndPoint(args);

        //testVote();
        //waitLock();
        
        testReferendumInfoOf();

        waitLock();
        testReferendumVotesFor();

        waitLock();
        testVotingCountdown();

    }

    static Object lock = new Object();

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

    public static void testVote() {
        System.out.println("=========start testVote=========");

        Promise<ApiPromise> promise = newApi();
        promise.then(api -> {

            Types.SubmittableExtrinsicFunction<Promise> vote = api.tx().section("democracy").function("vote");
            SubmittableExtrinsic<Promise> call = vote.call(13, true);
            call.signAndSendCb(Alice, new SubmittableExtrinsic.StatusCb() {
                @Override
                public Object callback(SubmittableExtrinsic.SubmittableResult result) {
                    ExtrinsicStatus status = result.getStatus();
                    List<EventRecord> events = result.getEvents();
                    if (status.isFinalized()) {
                        System.out.println("Successful transfer  with hash " + status.asFinalized().toHex());
                    } else {
                        System.out.println("Status of transfer: " + status.getType());
                    }

                    System.out.println("Events");
                    for (EventRecord event : events) {
                        EventRecord.Phase phase = event.getPhase();
                        Event eventEvent = event.getEvent();

                        System.out.println("\t" + phase.toString()
                                + ": " + eventEvent.getSection() + "." + eventEvent.getMethod()
                                + " " + eventEvent.getData().toString());
                    }

                    return null;
                }
            });
            return null;
        })._catch(err -> {
            err.printStackTrace();
            return null;
        });
        /*

const ALICE = '5GoKvZWG5ZPYL1WUovuHW3zJBWBP5eT8CbqjdRY4Q6iMaDtZ';

const transfer = api.tx.democracy.vote(13,true)

transfer.signAndSend(ALICE, ({ events = [], status }) => {
  if (status.isFinalized) {
    console.log('Successful transfer'  with hash ' + status.asFinalized.toHex());
  } else {
    console.log('Status of transfer: ' + status.type);
  }

  events.forEach(({ phase, event: { data, method, section } }) => {
    console.log(phase.toString() + ' : ' + section + '.' + method + ' ' + data.toString());
  });
});

         */
    }

    static Promise<ApiPromise> newApi() {

        WsProvider wsProvider = new WsProvider(endPoint);

        Promise<ApiPromise> ready = ApiPromise.create(wsProvider);

        return ready;
    }


    public static void testReferendumInfoOf() {

        WsProvider wsProvider = new WsProvider(endPoint);

        Promise<ApiPromise> ready = ApiPromise.create(wsProvider);

        System.out.println("=========start testReferendumInfoOf=========");


        ready.then(api -> {
            return api.query().section("democracy").function("referendumInfoOf").call(2, new IRpcFunction.SubscribeCallback<Object>() {
                @Override
                public void callback(Object codec) {
                    System.out.println("referendumInfoOf callback : " + codec);
                }
            });
        }).then(result -> {
            System.out.println("referendumInfoOf result  " + result);
            notifyLock();
            return null;
        })._catch(err -> {
            err.printStackTrace();
            return null;
        });
    }

    public static void testReferendumVotesFor() {


        WsProvider wsProvider = new WsProvider(endPoint);

        Promise<ApiPromise> ready = ApiPromise.create(wsProvider);

        System.out.println("=========start testReferendumVotesFor=========");

        ready.then(api -> {
            Types.DeriveSection democracy = api.derive().section("democracy");
            Types.DeriveMethod function = democracy.function("referendumVotesFor");
            return function.call(10, new IRpcFunction.SubscribeCallback() {
                        @Override
                        public void callback(Object result) {
                            System.out.println("referendumVotesFor callback : " + result);
                        }
                    }
            );
        }).then(result -> {
            System.out.println("referendumVotesFor result " + result);
            notifyLock();
            return null;
        })._catch(err -> {
            err.printStackTrace();
            return null;
        });
    }

    public static void testVotingCountdown() {
        WsProvider wsProvider = new WsProvider(endPoint);
        Promise<ApiPromise> ready = ApiPromise.create(wsProvider);
        AtomicReference<ApiPromise> readyApi = new AtomicReference<>();

        System.out.println("=========start testVotingCountdown=========");

        ready.then(api -> {

                    Types.QueryableStorage<Promise> query = api.query();
                    Types.QueryableModuleStorage<Promise> democracy = query.section("democracy");

                    Types.QueryableStorageFunction<Promise> votingPeriod = democracy.function("votingPeriod");
                    Types.QueryableStorageFunction<Promise> launchPeriod = democracy.function("launchPeriod");
                    readyApi.set(api);
                    return Promise.all(
                            votingPeriod.call(),
                            launchPeriod.call()
                    );
                }
        ).then(
                (results) -> {

                    BlockNumber votingPeriod = (BlockNumber) results.get(0);
                    BlockNumber launchPeriod = (BlockNumber) results.get(1);

                    System.out.println("@votingPeriod:" + votingPeriod);
                    System.out.println("*launchPeriod:" + launchPeriod);

                    ApiPromise api = readyApi.get();
                    Types.DeriveSection chain = api.derive().section("chain");
                    Types.DeriveMethod bestNumber = chain.function("bestNumber");

                    return bestNumber.call((IRpcFunction.SubscribeCallback) o -> {

                        System.out.println("SubscribeCallback : " + o);

                        BlockNumber bestNum = (BlockNumber) o;

                        BigInteger votingCountdown = votingPeriod.subtract(bestNum.mod(votingPeriod).add(BigInteger.ONE));
                        BigInteger launchCountdown = launchPeriod.subtract(bestNum.mod(launchPeriod).add(BigInteger.ONE));
                        System.out.println("@votingCountdown:" + votingCountdown);
                        System.out.println("*launchCountdown:" + launchCountdown);
                    });
                }
        )._catch((err) -> {
            err.printStackTrace();
            return Promise.value(err);
        });


    }
}
