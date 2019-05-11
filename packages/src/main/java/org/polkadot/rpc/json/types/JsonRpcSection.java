package org.polkadot.rpc.json.types;

import java.util.Map;

public class JsonRpcSection {

    public boolean isDeprecated;
    public boolean isHidden;
    public String description;
    public String section;
    public Map<String, JsonRpcMethod> rpcMethods;

    public JsonRpcSection(boolean isDeprecated, boolean isHidden, String description, String section, Map<String, JsonRpcMethod> rpcMethods) {
        this.isDeprecated = isDeprecated;
        this.isHidden = isHidden;
        this.description = description;
        this.section = section;
        this.rpcMethods = rpcMethods;
    }

    //isDeprecated: boolean,
    //isHidden: boolean,
    //description: string,
    //section: string,
    //methods: {
    //[index: string]: RpcInterfaceMethod
    //}
}
