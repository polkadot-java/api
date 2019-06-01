package org.polkadot.types.codec;

import org.polkadot.types.Codec;

import java.math.BigInteger;

public interface Compactable extends Codec {
    BigInteger toBn();

    long toNumber();

    int bitLength();
}
