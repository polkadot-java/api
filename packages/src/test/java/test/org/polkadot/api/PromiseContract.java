package test.org.polkadot.api;

import com.alibaba.fastjson.JSON;
import com.onehilltech.promises.Promise;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.polkadot.api.SubmittableExtrinsic;
import org.polkadot.api.promise.ApiPromise;
import org.polkadot.common.keyring.Types;
import org.polkadot.example.TestingPairs;
import org.polkadot.rpc.provider.ws.WsProvider;
import org.polkadot.types.Codec;
import org.polkadot.types.ContractAbi;
import org.polkadot.types.Types.ContractABI;
import org.polkadot.types.rpc.ExtrinsicStatus;
import org.polkadot.types.type.EventRecord;
import org.polkadot.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.polkadot.utils.crypto.Types.KeypairType_SR;

public class PromiseContract {

    private ApiPromise api;

    private AtomicBoolean sync = new AtomicBoolean(false);

    private Map<String, Types.KeyringPair> keyring;
    static String endPoint = "ws://127.0.0.1:9944";
    //static String endPoint = "wss://poc3-rpc.polkadot.io/";

    static Object testSync = new Object();

    static ContractAbi abi;

    //-Djava.library.path=./libs

    @BeforeClass
    public static void initABI() {
        ContractABI contractABI = readABI();
        abi = new ContractAbi(contractABI);
    }

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

        keyring = TestingPairs.testKeyringPairs(KeypairType_SR);
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
    public void allowsPutCode() throws IOException {

        System.out.println("========== run allowsPutCode ");

        byte[] bytes = Files.readAllBytes(Paths.get("./data/incrementer-opt.wasm"));
        String code = Utils.u8aToHex(bytes);

        System.out.println(code);
        api.tx()
                .section("contract")
                .function("putCode")
                .call(200000, code)
                .signAndSendCb(keyring.get("alice"), new SubmittableExtrinsic.StatusCb() {
                    @Override
                    public Object callback(SubmittableExtrinsic.SubmittableResult result) {
                        System.out.println("  putCode result = " + result);
                        System.out.println("  putCode result json = " + JSON.toJSONString(result));

                        List<EventRecord> events = result.getEvents();
                        ExtrinsicStatus status = result.getStatus();

                        System.out.println("Transaction status:" + status.getType());

                        if (status.isFinalized()) {

                            EventRecord record = result.findRecord("contract", "CodeStored");

                            System.out.println("record : " + record);

                            if (record != null) {
                                Codec codeHash = record.getEvent().getData().get(0);
                                System.out.println("codeHash : " + codeHash);
                                System.exit(0);
                            }
                        }
                        return null;
                    }
                });


        waitFinish();
    }

    @Test
    public void allowsContract() throws IOException {
        //TODO 2019-07-11 14:55
    }

    public static String readFile(String fileName) {
        Path path = Paths.get(fileName);
        if (!Files.exists(path)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader bfr = Files.newBufferedReader(path)) {
            List<String> allLines = Files.readAllLines(path);
            for (String line : allLines) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        ContractABI contractABI = readABI();
        System.out.println();
    }

    private static ContractABI readABI() {
        String json = readFile("./data/incrementer.json");
        System.out.println(" readABI : \n" + json);
        ContractABI contractABI = JSON.parseObject(json, ContractABI.class);
        return contractABI;
    }

}
