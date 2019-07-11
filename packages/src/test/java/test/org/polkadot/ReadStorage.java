package test.org.polkadot;

import com.alibaba.fastjson.JSON;
import com.onehilltech.promises.Promise;
import org.polkadot.api.Types;
import org.polkadot.api.promise.ApiPromise;
import org.polkadot.direct.IRpcFunction;

public class ReadStorage {
    static String Alice = "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY";

    //public static void main(String[] args) {
    //    // Create an await for the API
    //    Promise<ApiPromise> ready = ApiPromise.create();
    //
    //
    //    ready.then(api -> {
    //                Types.QueryableStorage query = api.query();
    //                Types.QueryableModuleStorage timestamp = query.section("timestamp");
    //                Types.QueryableModuleStorage system = query.section("system");
    //                Types.QueryableModuleStorage session = query.section("session");
    //                //Types.QueryableStorageFunction freeBalance = balances.function("freeBalance");
    //
    //                return
    //
    //                        Promise.all(
    //
    //                                system.function("accountNonce").call(Alice, (IRpcFunction.SubscribeCallback) o -> System.out.println("accountNonce result " + o)),
    //                                timestamp.function("blockPeriod").call((IRpcFunction.SubscribeCallback) o -> System.out.println("blockPeriod result " + o)),
    //                                session.function("validators").call((IRpcFunction.SubscribeCallback) o -> System.out.println("validators result " + o))
    //                                /**
    //                                 *
    //                                 api.query.system.accountNonce(Alice),
    //                                 api.query.timestamp.blockPeriod(),
    //                                 api.query.session.validators()
    //                                 */
    //
    //
    //                                //system.function("accountNonce").call(Alice, (IRpcFunction.SubscribeCallback) o -> System.out.println("accountNonce result " + o))
    //                                //timestamp.function("blockPeriod").call((IRpcFunction.SubscribeCallback) o -> System.out.println("blockPeriod result " + o))
    //
    //
    //                                //session.function("validators").call((IRpcFunction.SubscribeCallback) o ->
    //                                //
    //                                //        {
    //                                //            System.out.println();
    //                                //            System.out.println("validators result " + o);
    //                                //        }
    //                                //)
    //
    //                        );
    //            }
    //
    //
    //    ).then(
    //            (result) -> {
    //                System.out.println(result);
    //                //System.out.println(Arrays.toString(result));
    //                System.out.println(JSON.toJSON(result));
    //                return null;
    //            }
    //    )
    //
    //            ._catch((err) -> {
    //                err.printStackTrace();
    //                return Promise.value(err);
    //            });
    //
    //
    //    try {
    //        Thread.sleep(20000);
    //    } catch (InterruptedException e) {
    //        e.printStackTrace();
    //    }
    //
    //}
}
