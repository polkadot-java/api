package org.polkadot.rpc.json;

import com.google.common.collect.Lists;
import org.polkadot.rpc.json.types.JsonRpcMethod;
import org.polkadot.rpc.json.types.JsonRpcMethodOpt;
import org.polkadot.rpc.json.types.JsonRpcParam;
import org.polkadot.rpc.json.types.JsonRpcSection;

import java.util.HashMap;
import java.util.Map;

public class JsonState {


    static final JsonRpcMethodOpt call = new JsonRpcMethodOpt(
            "Perform a call to a builtin on the chain",
            Lists.newArrayList(
                    new JsonRpcParam("method", "Text"),
                    new JsonRpcParam("data", "Bytes"),
                    new JsonRpcParam("block", "Hash", true)

            ),
            "Bytes"
    );

    static final JsonRpcMethodOpt getStorage = new JsonRpcMethodOpt(
            "Retrieves the storage for a key",
            Lists.newArrayList(
                    new JsonRpcParam("key", "StorageKey"),
                    new JsonRpcParam("block", "Hash", true)

            ),
            "StorageData"
    );


    static final JsonRpcMethodOpt getStorageHash = new JsonRpcMethodOpt(
            "Retrieves the storage hash",
            Lists.newArrayList(
                    new JsonRpcParam("key", "StorageKey"),
                    new JsonRpcParam("block", "Hash", true)

            ),
            "Hash"
    );


    static final JsonRpcMethodOpt getStorageSize = new JsonRpcMethodOpt(
            "Retrieves the storage size",
            Lists.newArrayList(
                    new JsonRpcParam("key", "StorageKey"),
                    new JsonRpcParam("block", "Hash", true)

            ),
            "u64"
    );


    static final JsonRpcMethodOpt getMetadata = new JsonRpcMethodOpt(
            "Returns the runtime metadata",
            Lists.newArrayList(
                    new JsonRpcParam("block", "Hash", true)

            ),
            "Metadata"
    );


    static final JsonRpcMethodOpt getRuntimeVersion = new JsonRpcMethodOpt(
            "Get the runtime version",
            Lists.newArrayList(
                    new JsonRpcParam("block", "Hash", true)

            ),
            "RuntimeVersion"
    );


    static final JsonRpcMethodOpt queryStorage = new JsonRpcMethodOpt(
            "Query historical storage entries (by key) starting from a start block",
            Lists.newArrayList(
                    // @ts-ignore The Vec<> wrap is fine
                    new JsonRpcParam("keys", "Vec<StorageKey>"),
                    new JsonRpcParam("startBlock", "Hash"),
                    new JsonRpcParam("block", "Hash", true)

            ),
            // @ts-ignore The Vec<> wrap is fine
            "Vec<StorageChangeSet>"
    );


    static final JsonRpcMethodOpt subscribeStorage = new JsonRpcMethodOpt(
            "Subscribes to storage changes for the provided keys",
            Lists.newArrayList(
                    // @ts-ignore The Vec<> wrap is fine
                    new JsonRpcParam("keys", "Vec<StorageKey>")
            ),
            new String[]{
                    "storage",
                    "subscribeStorage",
                    "unsubscribeStorage"
            },
            "StorageChangeSet"
    );


    static final java.lang.String section = "state";

    static final Map<String, JsonRpcMethod> methods = new HashMap<>();

    static {
        methods.put("call", new JsonRpcMethod(call, section, "call"));
        methods.put("getStorage", new JsonRpcMethod(getStorage, section, "getStorage"));
        methods.put("getStorageHash", new JsonRpcMethod(getStorageHash, section, "getStorageHash"));
        methods.put("getStorageSize", new JsonRpcMethod(getStorageSize, section, "getStorageSize"));
        methods.put("getMetadata", new JsonRpcMethod(getMetadata, section, "getMetadata"));
        methods.put("getRuntimeVersion", new JsonRpcMethod(getRuntimeVersion, section, "getRuntimeVersion"));
        methods.put("queryStorage", new JsonRpcMethod(queryStorage, section, "queryStorage"));
        methods.put("subscribeStorage", new JsonRpcMethod(subscribeStorage, section, "subscribeStorage"));
    }

    static final JsonRpcSection state = new JsonRpcSection(
            false,
            false,
            "Query of state",
            section,
            methods);

}
