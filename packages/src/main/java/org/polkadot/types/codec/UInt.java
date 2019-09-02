package org.polkadot.types.codec;


import org.polkadot.types.type.Balance;
import org.polkadot.utils.Utils;

/**
 * UInt
 * A generic unsigned integer codec. For Substrate all numbers are LE encoded,
 * this handles the encoding and decoding of those numbers. Upon construction
 * the bitLength is provided and any additional use keeps the number to this
 * length. This extends `BN`, so all methods available on a normal `BN` object
 * is available here.
 *
 * @noInheritDoc
 */
public class UInt extends AbstractInt implements Compactable {
    public UInt(Object value, int bitLength, boolean isHexJson) {
        super(false, value, bitLength, isHexJson);
    }

    public UInt(Object value, int bitLength) {
        super(false, value, bitLength, false);
    }

    /**
     * Returns a hex string representation of the value
     */
    @Override
    public String toHex() {
        return Utils.bnToHex(this, false, false, this.bitLength());
    }

    /**
     * @param isBare true when the value has none of the type-specific prefixes (internal)
     *               Encodes the value as a Uint8Array as per the parity-codec specifications
     */
    @Override
    public byte[] toU8a(boolean isBare) {
        return Utils.bnToU8a(this, true, false, this.bitLength());
    }

    @Override
    public long toNumber() {
        return this.longValue();
    }


    /**
     * @description Returns the base runtime type name for this instance
     */
    @Override
    public String toRawType() {
        // NOTE In the case of balances, which have a special meaning on the UI
        // and can be interpreted differently, return a specifc value for it so
        // underlying it always matches (no matter which length it actually is)
        return this instanceof Balance
                ? "Balance"
                : "u" + this.bitLength();
    }

}
