package org.polkadot.rpc.json;

import com.google.common.collect.Lists;
import org.polkadot.rpc.json.types.JsonRpcMethod;
import org.polkadot.rpc.json.types.JsonRpcMethodOpt;
import org.polkadot.rpc.json.types.JsonRpcParam;
import org.polkadot.rpc.json.types.JsonRpcSection;

import java.util.HashMap;
import java.util.Map;

public class JsonAuthor {

    static final JsonRpcMethodOpt pendingExtrinsics = new JsonRpcMethodOpt(
            "Returns all pending extrinsics, potentially grouped by sender",
            "PendingExtrinsics"
    );


    static final JsonRpcMethodOpt submitExtrinsic = new JsonRpcMethodOpt(
            "Submit a fully formatted extrinsic for block inclusion",
            Lists.newArrayList(new JsonRpcParam("extrinsic", "Extrinsic")),
            "Hash",
            true);

    static final JsonRpcMethodOpt submitAndWatchExtrinsic = new JsonRpcMethodOpt(
            "Subscribe and watch an extrinsic until unsubscribed",
            Lists.newArrayList(new JsonRpcParam("extrinsic", "Extrinsic")),
            new String[]{
                    "extrinsicUpdate",
                    "submitAndWatchExtrinsic",
                    "unwatchExtrinsic"
            },
            "ExtrinsicStatus",
            true);


    static final java.lang.String section = "author";

    static final Map<String, JsonRpcMethod> methods = new HashMap<>();

    static {
        methods.put("pendingExtrinsics", new JsonRpcMethod(pendingExtrinsics, section, "pendingExtrinsics"));
        methods.put("submitExtrinsic", new JsonRpcMethod(submitExtrinsic, section, "submitExtrinsic"));
        methods.put("submitAndWatchExtrinsic", new JsonRpcMethod(submitAndWatchExtrinsic, section, "submitAndWatchExtrinsic"));
    }

    static final JsonRpcSection author = new JsonRpcSection(
            false,
            false,
            "Authoring of network items",
            section,
            methods);

}

