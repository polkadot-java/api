package org.polkadot.types.primitive;


/**
 * Data retrieved via Storage queries and data for KeyValue pairs
 */
public class StorageData extends Bytes {
    public StorageData(Object value) {
        super(value);
    }
}
