package org.polkadot.rpc.json.types;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class JsonRpcMethod {
    private String alias;
    private String description;
    private boolean isDeprecated;
    private boolean isHidden;
    private boolean isSigned;
    private boolean isSubscription;
    private String method;
    private List<JsonRpcParam> params;
    private String[] pubsub;
    private String section;
    private String type;

    public JsonRpcMethod(JsonRpcMethodOpt rpcMethodOpt, String section, String method) {
        this.description = rpcMethodOpt.description;
        this.isDeprecated = rpcMethodOpt.isDeprecated;
        this.isHidden = rpcMethodOpt.isHidden;
        this.isSigned = rpcMethodOpt.isSigned;
        //this.isSubscription = rpcMethodOpt.isSubscription;
        this.isSubscription = ArrayUtils.isNotEmpty(rpcMethodOpt.pubsub);
        this.method = method;
        this.params = rpcMethodOpt.params;
        if (this.params == null) {
            this.params = new ArrayList<>();
        }
        this.pubsub = rpcMethodOpt.pubsub;
        this.section = section;
        this.type = rpcMethodOpt.type;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDeprecated() {
        return isDeprecated;
    }

    public void setDeprecated(boolean deprecated) {
        isDeprecated = deprecated;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public boolean isSigned() {
        return isSigned;
    }

    public void setSigned(boolean signed) {
        isSigned = signed;
    }

    public boolean isSubscription() {
        return isSubscription;
    }

    public void setSubscription(boolean subscription) {
        isSubscription = subscription;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<JsonRpcParam> getParams() {
        return params;
    }

    public void setParams(List<JsonRpcParam> params) {
        this.params = params;
    }

    public String[] getPubsub() {
        return pubsub;
    }

    public void setPubsub(String[] pubsub) {
        this.pubsub = pubsub;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
