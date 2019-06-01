package org.polkadot.example;

import com.onehilltech.promises.Promise;
import org.polkadot.api.SubmittableExtrinsic;
import org.polkadot.api.promise.ApiPromise;
import org.polkadot.common.keyring.Keyring;
import org.polkadot.common.keyring.Types;
import org.polkadot.rpc.provider.ws.WsProvider;
import org.polkadot.types.type.Hash;


public class MakeTransfer {
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

    static {
        System.loadLibrary("jni");
        System.out.println("load ");
    }

    public static void main(String[] args) throws InterruptedException {
        // Create an await for the API
        //Promise<ApiPromise> ready = ApiPromise.create();
        initEndPoint(args);

        WsProvider wsProvider = new WsProvider(endPoint);

        Promise<ApiPromise> ready = ApiPromise.create(wsProvider);

        String BOB = "5FHneW46xGXgs5mUiveU4sbTyGBzmstUspZC92UhjJM694ty";

        /////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////

        ready.then(api -> {

            Types.KeyringOptions options = new Types.KeyringOptions(org.polkadot.utils.crypto.Types.KeypairType_SR);
            Keyring keyring = new Keyring(options);
            Types.KeyringPair alice = keyring.addFromUri("//Alice", null, options.getType());
            org.polkadot.api.Types.SubmittableExtrinsicFunction function = api.tx().section("balances").function("transfer");
            SubmittableExtrinsic transfer = function.call(BOB, 111);
            // Add alice to our keyring with a hard-deived path (empty phrase, so uses dev)
            //const alice = keyring.addFromUri('//Alice');
            return transfer.signAndSend(alice, new org.polkadot.types.Types.SignatureOptions());
            // Create a extrinsic, transferring 12345 units to Bob
            //const transfer = api.tx.balances.transfer(BOB, 12345);

        }).then(result -> {
            // Sign and send the transaction using our account
            //const hash = await transfer.signAndSend(alice);
            Hash hash = (Hash) result;
            System.out.println("Transfer sent with hash" + hash.toHex());
            return null;
        })._catch(err -> {
            err.printStackTrace();
            return null;
        });


    }
}
