package test.org.polkadot;

import com.alibaba.fastjson.JSON;
import com.onehilltech.promises.Promise;
import org.polkadot.api.Types.QueryableModuleStorage;
import org.polkadot.api.Types.QueryableStorage;
import org.polkadot.api.Types.QueryableStorageFunction;
import org.polkadot.api.promise.ApiPromise;
import org.polkadot.direct.IRpcFunction;
import org.polkadot.type.storage.Types;
import org.polkadot.types.primitive.StorageKey;

import java.util.Set;


public class ListenToBalanceChange {
    static String Alice = "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY";

    public static void main(String[] args) {
        // Create an await for the API
        Promise<ApiPromise> ready = ApiPromise.create();


        ready.then(api -> {
            // Retrieve the initial balance. Since the call has no callback, it is simply a promise
            // that resolves to the current on-chain value
            QueryableStorage query = api.query();
            Types.Storage storage = api.queryOri();

            Types.ModuleStorage substrate = storage.substrate();


            for (String sectionName : query.sectionNames()) {
                Types.ModuleStorage section = storage.section(sectionName);
                Set<String> functionNames = section.functionNames();
                for (String functionName : functionNames) {
                    StorageKey.StorageFunction function = section.function(functionName);
                    System.out.println(" query functions " + sectionName + " - " + functionName);
                    if (functionName.equals("freeBalance")) {
                        System.out.println();
                    }
                }
            }

            for (String functionName : substrate.functionNames()) {
                System.out.println(" substrate query functions substrate - " + functionName);

            }


            byte[] apply = query.section("balances").function("freeBalance").apply(Alice);

            QueryableModuleStorage balances = query.section("balances");
            QueryableStorageFunction freeBalance = balances.function("freeBalance");
            return freeBalance.call(Alice, (IRpcFunction.SubscribeCallback) o -> System.out.println("freeBalance result " + o)
            );
            //return Promise.value(apply);
        }).then(
                (result) -> {
                    System.out.println(result);
                    //System.out.println(Arrays.toString(result));
                    System.out.println(JSON.toJSON(result));
                    return null;
                }
        )

                ._catch((err) -> {
                    err.printStackTrace();
                    return Promise.value(err);
                });


        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
