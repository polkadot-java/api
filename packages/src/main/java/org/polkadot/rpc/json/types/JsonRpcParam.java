package org.polkadot.rpc.json.types;


public class JsonRpcParam {
    private boolean isOptional;
    private String name;
    //private String type;
    private String type;

    public JsonRpcParam(String name, String type, boolean isOptional) {
        this.isOptional = isOptional;
        this.name = name;
        this.type = type;
    }

    public JsonRpcParam(String name, String type) {
        this(name, type, false);
    }

    public boolean isOptional() {
        return isOptional;
    }

    public void setOptional(boolean optional) {
        isOptional = optional;
    }

    public java.lang.String getName() {
        return name;
    }

    public void setName(java.lang.String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
