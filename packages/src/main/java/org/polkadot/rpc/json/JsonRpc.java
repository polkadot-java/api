package org.polkadot.rpc.json;

import org.polkadot.rpc.json.types.JsonRpcSection;

public class JsonRpc {

    public static final JsonRpcSection author = JsonAuthor.author;
    public static final JsonRpcSection chain = JsonChain.chain;
    public static final JsonRpcSection state = JsonState.state;
    public static final JsonRpcSection system = JsonSystem.system;
}
