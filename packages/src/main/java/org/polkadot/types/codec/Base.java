package org.polkadot.types.codec;

/**
 * TODO check
 *
 * @param <T>
 */
public class Base<T extends Object> {
    protected T raw;

    public Base(T t) {
        this.raw = t;
    }

    /**
     * @description Returns the base runtime type name for this instance
     */
    public String toRawType() {
        return "Base";
    }
}
