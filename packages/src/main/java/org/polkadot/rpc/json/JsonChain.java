package org.polkadot.rpc.json;

import com.google.common.collect.Lists;
import org.polkadot.rpc.json.types.JsonRpcMethod;
import org.polkadot.rpc.json.types.JsonRpcMethodOpt;
import org.polkadot.rpc.json.types.JsonRpcParam;
import org.polkadot.rpc.json.types.JsonRpcSection;

import java.util.HashMap;
import java.util.Map;

public class JsonChain {


    static final JsonRpcMethodOpt getHeader = new JsonRpcMethodOpt(
            "Retrieves the header for a specific block",
            Lists.newArrayList(new JsonRpcParam("hash", "Hash", true)),
            "Header"
    );


    static final JsonRpcMethodOpt getBlock = new JsonRpcMethodOpt(
            "Get header and body of a relay chain block",
            Lists.newArrayList(new JsonRpcParam("hash", "Hash", true)),
            "SignedBlock");


    static final JsonRpcMethodOpt getBlockHash = new JsonRpcMethodOpt(
            "Get the block hash for a specific block",
            Lists.newArrayList(new JsonRpcParam("blockNumber", "BlockNumber", true)),
            "Hash");

    static final JsonRpcMethodOpt getFinalizedHead = new JsonRpcMethodOpt(
            "Get hash of the last finalised block in the canon chain",
            Lists.newArrayList(),
            "Hash");

    static final JsonRpcMethodOpt getRuntimeVersion = new JsonRpcMethodOpt(
            "Get the runtime version (alias of state_getRuntimeVersion)",
            Lists.newArrayList(new JsonRpcParam("hash", "Hash", true)),
            "RuntimeVersion");


    static final JsonRpcMethodOpt subscribeNewHead = new JsonRpcMethodOpt(
            "Retrieves the best header via subscription",
            Lists.newArrayList(),
            new String[]{
                    "newHead",
                    "subscribeNewHead",
                    "unsubscribeNewHead"
            },
            "Header");


    static final JsonRpcMethodOpt subscribeFinalizedHeads = new JsonRpcMethodOpt(
            "Retrieves the best finalized header via subscription",
            Lists.newArrayList(),
            new String[]{
                    "finalizedHead",
                    "subscribeFinalisedHeads",
                    "unsubscribeFinalisedHeads"
            },
            "Header");


    static final JsonRpcMethodOpt subscribeRuntimeVersion = new JsonRpcMethodOpt(
            "Retrieves the runtime version via subscription",
            Lists.newArrayList(),
            new String[]{
                    "runtimeVersion",
                    "subscribeRuntimeVersion",
                    "unsubscribeRuntimeVersion"
            },
            "RuntimeVersion");

    static final java.lang.String section = "chain";

    static final Map<String, JsonRpcMethod> methods = new HashMap<>();

    static {
        methods.put("getHeader", new JsonRpcMethod(getHeader, section, "getHeader"));
        methods.put("getBlock", new JsonRpcMethod(getBlock, section, "getBlock"));
        methods.put("getBlockHash", new JsonRpcMethod(getBlockHash, section, "getBlockHash"));
        methods.put("getFinalizedHead", new JsonRpcMethod(getFinalizedHead, section, "getFinalizedHead"));
        methods.put("getRuntimeVersion", new JsonRpcMethod(getRuntimeVersion, section, "getRuntimeVersion"));
        methods.put("subscribeNewHead", new JsonRpcMethod(subscribeNewHead, section, "subscribeNewHead"));
        methods.put("subscribeFinalizedHeads", new JsonRpcMethod(subscribeFinalizedHeads, section, "subscribeFinalizedHeads"));
        methods.put("subscribeRuntimeVersion", new JsonRpcMethod(subscribeRuntimeVersion, section, "subscribeRuntimeVersion"));
    }

    static final JsonRpcSection chain = new JsonRpcSection(
            false,
            false,
            "Retrieval of chain data",
            section,
            methods);

}
