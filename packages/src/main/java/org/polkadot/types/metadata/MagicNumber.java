package org.polkadot.types.metadata;

import org.polkadot.types.primitive.U32;

public class MagicNumber extends U32 {


    //export const MAGIC_NUMBER = new U32(0x6174656d); // `meta`, reversed for LE encoding
    //export const MAGIC_ERROR = -61746;
    public final static U32 MAGIC_NUMBER = new U32(0x6174656d);// `meta`, reversed for LE encoding
    public final static int MAGIC_ERROR = -61746;

    public MagicNumber(Object value) {
        super(value);
        assert this.eq(MAGIC_NUMBER) : "MagicNumber: expected " + MAGIC_NUMBER.toHex() + ", found " + this.toHex() + " " + MAGIC_ERROR;
    }

}
