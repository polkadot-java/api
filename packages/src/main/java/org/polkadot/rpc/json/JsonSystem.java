package org.polkadot.rpc.json;

import org.polkadot.rpc.json.types.JsonRpcMethod;
import org.polkadot.rpc.json.types.JsonRpcMethodOpt;
import org.polkadot.rpc.json.types.JsonRpcSection;

import java.util.HashMap;
import java.util.Map;

public class JsonSystem {

    static final JsonRpcMethodOpt name = new JsonRpcMethodOpt(
            "Retrieves the node name",
            "Text");

    static final JsonRpcMethodOpt version = new JsonRpcMethodOpt(
            "Retrieves the version of the node",
            "Text");

    static final JsonRpcMethodOpt chain = new JsonRpcMethodOpt(
            "Retrieves the chain",
            "Text");


    static final JsonRpcMethodOpt properties = new JsonRpcMethodOpt(
            "Get a custom set of properties as a JSON object, defined in the chain spec",
            "ChainProperties");


    static final JsonRpcMethodOpt health = new JsonRpcMethodOpt(
            "Return health status of the node",
            "Health");


    static final JsonRpcMethodOpt peers = new JsonRpcMethodOpt(
            "Returns the currently connected peers",
            "Vec<PeerInfo>");

    static final JsonRpcMethodOpt networkState = new JsonRpcMethodOpt(
            "Returns current state of the network",
            "NetworkState");


    static final java.lang.String section = "system";

    static final Map<java.lang.String, JsonRpcMethod> methods = new HashMap<>();

    static {
        methods.put("name", new JsonRpcMethod(name, section, "name"));
        methods.put("version", new JsonRpcMethod(version, section, "version"));
        methods.put("chain", new JsonRpcMethod(chain, section, "chain"));
        methods.put("properties", new JsonRpcMethod(properties, section, "properties"));

        methods.put("health", new JsonRpcMethod(health, section, "health"));
        methods.put("peers", new JsonRpcMethod(peers, section, "peers"));
        methods.put("networkState", new JsonRpcMethod(networkState, section, "networkState"));

    }

    static final JsonRpcSection system = new JsonRpcSection(
            false,
            false,
            "Methods to retrieve system info",
            section,
            methods);

}
