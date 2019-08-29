package org.polkadot.types.primitive;

import org.polkadot.types.codec.AbstractInt;
import org.polkadot.utils.Utils;

/**
 * A generic signed integer codec. For Substrate all numbers are LE encoded,
 * this handles the encoding and decoding of those numbers. Upon construction
 * the bitLength is provided and any additional use keeps the number to this
 * length. This extends `BN`, so all methods available on a normal `BN` object
 * is available here.
 *
 * @noInheritDoc
 */
public class Int extends AbstractInt {
    //constructor (value: AnyNumber = 0, bitLength: UIntBitLength = DEFAULT_UINT_BITS, isHexJson: boolean = true) {
    public Int(Object value, int bitLength, boolean isHexJson) {
        super(true, value, bitLength, isHexJson);
    }


    /**
     * Returns a hex string representation of the value
     */
    @Override
    public String toHex() {
        return Utils.bnToHex(this, false, true, this.bitLength());
    }


    /**
     * @param isBare true when the value has none of the type-specific prefixes (internal)
     *               Encodes the value as a Uint8Array as per the parity-codec specifications
     */
    @Override
    public byte[] toU8a(boolean isBare) {
        return Utils.bnToU8a(this, true, true, this.bitLength());
    }

    /**
     * @description Returns the base runtime type name for this instance
     */
    @Override
    public String toRawType() {
        return "i" + this.bitLength();
    }
}
