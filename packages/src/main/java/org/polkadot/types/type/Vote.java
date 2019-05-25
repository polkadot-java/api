package org.polkadot.types.type;

import org.polkadot.types.primitive.I8;

import java.math.BigInteger;

/**
 * @name Vote
 * @description A number of lock periods, plus a vote, one way or the other.
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
     * @description true is the wrapped value is a positive vote
     */
    public boolean isAye() {
        return this.compareTo(BigInteger.ZERO) <= 0;
    }

    /**
     * @description true is the wrapped value is a negative vote
     */
    public boolean isNay() {
        return !this.isAye();
    }
}
