package org.polkadot.types.type;

import org.polkadot.types.primitive.Null;

/**
 * Where Origin occurs, it should be ignored as an internal-only value, so it should
 * never actually be constructed
 */
public class Origin extends Null {

    public Origin() {
        super();
        throw new RuntimeException("Origin should not be constructed, it is only a placeholder for compatibility");
    }
}
