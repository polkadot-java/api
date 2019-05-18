package org.polkadot.types.codec;

public class Base<T extends Object> {
    protected T raw;

    public Base(T t) {
        this.raw = t;
    }
}
