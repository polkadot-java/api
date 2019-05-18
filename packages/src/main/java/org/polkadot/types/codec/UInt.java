package org.polkadot.types.codec;


import org.polkadot.utils.Utils;

/**
 * @name UInt
 * @description A generic unsigned integer codec. For Substrate all numbers are LE encoded,
 * this handles the encoding and decoding of those numbers. Upon construction
 * the bitLength is provided and any additional use keeps the number to this
 * length. This extends `BN`, so all methods available on a normal `BN` object
 * is available here.
 * @noInheritDoc
 */
public class UInt extends AbstractInt {
    public UInt(Object value, int bitLength, boolean isHexJson) {
        super(false, value, bitLength, isHexJson);
    }

    public UInt(Object value, int bitLength) {
        super(false, value, bitLength, false);
    }

    /**
     * @description Returns a hex string representation of the value
     */
    @Override
    public String toHex() {
        return Utils.bnToHex(this, false, false, this.bitLength());
    }

    /**
     * @param isBare true when the value has none of the type-specific prefixes (internal)
     * @description Encodes the value as a Uint8Array as per the parity-codec specifications
     */
    @Override
    public byte[] toU8a(boolean isBare) {
        return Utils.bnToU8a(this, true, false, this.bitLength());
    }


    public int toNumber() {
        return this.intValue();
    }
}
