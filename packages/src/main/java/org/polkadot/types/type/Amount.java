package org.polkadot.types.type;

/**
 * @name Amount
 * @description The Substrate Amount representation as a [[Balance]].
 */
public class Amount extends Balance {
    public Amount(Object value) {
        super(value);
    }
}
