package org.polkadot.rpc.json.types;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class JsonRpcMethodOpt {
    public String description;
    public boolean isDeprecated;
    public boolean isHidden;
    public boolean isSigned;
    public boolean isSubscription;
    public List<JsonRpcParam> params = new ArrayList<>();
    public String[] pubsub;
    public String type;

    public JsonRpcMethodOpt(String description, List<JsonRpcParam> params, String[] pubsub, String type, boolean isSigned) {
        this.description = description;
        this.isSigned = isSigned;
        this.params = params;
        this.pubsub = pubsub;
        this.type = type;
    }

    public JsonRpcMethodOpt(String description, List<JsonRpcParam> params, String type, boolean isSigned) {
        this.description = description;
        this.isSigned = isSigned;
        this.params = params;
        this.type = type;
    }

    public JsonRpcMethodOpt(String description, List<JsonRpcParam> params, String[] pubsub, String type) {
        this.description = description;
        this.params = params;
        this.pubsub = pubsub;
        this.type = type;
    }

    public JsonRpcMethodOpt(String description, List<JsonRpcParam> params, String type) {
        this.description = description;
        this.params = params;
        this.type = type;
    }

    public JsonRpcMethodOpt(String description, String type) {
        this.description = description;
        this.params = Lists.newArrayList();
        this.type = type;
    }

    //description: string,
    //isDeprecated?: boolean,
    //isHidden?: boolean,
    //isSigned?: boolean,
    //isSubscription?: boolean,
    //params: Array<RpcParam>,
    //pubsub?: PubSub,
    //type: String
}
