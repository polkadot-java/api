package org.polkadot.types.primitive;

import org.polkadot.types.Codec;

/**
 * @name Null
 * Implements a type that does not contain anything (apart from `null`)
 */
public class Null implements Codec {
  /**
   * The length of the value when encoded as a Uint8Array
   */
    @Override
    public int getEncodedLength() {
        return 0;
    }

  /**
   * Checks if the value is an empty value (always true)
   */
    @Override
    public boolean isEmpty() {
        return true;
    }

  /**
   * Compares the value of the input to see if there is a match
   */
    @Override
    public boolean eq(Object other) {
        return other instanceof Null || other == null;
    }

  /**
   * Returns a hex string representation of the value
   */
    @Override
    public String toHex() {
        return "0x";
    }

  /**
   * Converts the Object to JSON, typically used for RPC transfers
   */
    @Override
    public Object toJson() {
        return null;
    }

  /**
   * Returns the string representation of the value
   */
    @Override
    public String toString() {
        return "";
    }

  /**
   * Encodes the value as a Uint8Array as per the parity-codec specifications
   * @param isBare true when the value has none of the type-specific prefixes (internal)
   */
    @Override
    public byte[] toU8a(boolean isBare) {
        return new byte[0];
    }
}
