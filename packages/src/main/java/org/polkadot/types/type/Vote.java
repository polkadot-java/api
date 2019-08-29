package org.polkadot.types.type;

import org.polkadot.types.primitive.I8;

import java.math.BigInteger;

/**
 * A number of lock periods, plus a vote, one way or the other.
 */
public class Vote extends I8 {
    public Vote(Object value) {
        super(decodeVote(value));
    }

    static Object decodeVote(Object value) {
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue()
                    ? -1 : 0;
        }
        return value;
    }


    /**
     * true is the wrapped value is a positive vote
     */
    public boolean isAye() {
        return this.compareTo(BigInteger.ZERO) <= 0;
    }

    /**
     * true is the wrapped value is a negative vote
     */
    public boolean isNay() {
        return !this.isAye();
    }

    /**
     * @description Returns the base runtime type name for this instance
     */
    @Override
    public String toRawType() {
        return "Vote";
    }
}
