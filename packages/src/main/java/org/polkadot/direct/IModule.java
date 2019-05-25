package org.polkadot.direct;

import java.util.Set;

public interface IModule<S extends ISection> {
    S section(String section);

    Set<String> sectionNames();

    default void addSection(String sectionName, S section) {
    }
}
