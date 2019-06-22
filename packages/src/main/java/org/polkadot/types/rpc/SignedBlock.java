package org.polkadot.types.rpc;

import org.polkadot.types.Types;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.type.Block;
import org.polkadot.types.type.Hash;
import org.polkadot.types.type.Justification;

/**
 * A Block that has been signed and contains a Justification
 */
public class SignedBlock extends Struct {
    public static class SignedBlockValue {
        Block.BlockValue block;
        byte[] justification;
    }

    //  constructor (value?: SignedBlockValue | Uint8Array) {
    public SignedBlock(Object value) {
        super(new Types.ConstructorDef()
                        .add("block", Block.class)
                        .add("justification", Justification.class)
                , value);
    }


    /**
     * The wrapped Block
     */
    public Block getBlock() {
        return this.getField("block");
    }

    /**
     * Block/header {@link org.polkadot.types.type.Hash}
     */
    public Hash getHash() {
        return this.getBlock().getHash();
    }

    /**
     * The wrapped Justification
     */
    public Justification getJustification() {
        return this.getField("justification");
    }
}
