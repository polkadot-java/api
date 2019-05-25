package org.polkadot.types.rpc;


import java.util.Objects;

/**
 * @name ChainProperties
 * Wraps the properties retrieved from the chain via the `system.properties` RPC call.
 */
public class ChainProperties extends Json {


    public ChainProperties(Object value) {
        super(value);
    }

    /**
     * The token decimals, if defined (de-facto standard only)
     */
    public Double getTokenDecimals() {
        Object tokenDecimals = this.get("tokenDecimals");
        return tokenDecimals == null
                ? null
                : Double.parseDouble(tokenDecimals.toString());
    }

    /**
     * The token system, if defined (de-facto standard only)
     */
    public String getTokenSymbol() {
        return Objects.toString(this.get("tokenSymbol"), null);
    }
}
