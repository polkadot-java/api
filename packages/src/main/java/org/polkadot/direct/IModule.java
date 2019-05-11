package org.polkadot.direct;

public interface IModule<S extends ISection> {
    S section(String section);
}
