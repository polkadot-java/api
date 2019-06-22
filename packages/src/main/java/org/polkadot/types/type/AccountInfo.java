package org.polkadot.types.type;

import org.polkadot.types.Types;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.primitive.Bytes;
import org.polkadot.types.primitive.U64;

/**
 * An Account information structure for contracts
 */
public class AccountInfo extends Struct {
    public AccountInfo(Object value) {
        super(new Types.ConstructorDef()
                        .add("trieId", Bytes.class)
                        .add("currentMemStored", U64.class)
                , value
        );
    }


    /**
     * The size of stored value in octet
     */
    public U64 getCurrentMemStored() {
        return this.getField("currentMemStored");
    }

    /**
     * Unique ID for the subtree encoded as a byte
     */
    public Bytes getTrieId() {
        return this.getField("trieId");
    }
}
