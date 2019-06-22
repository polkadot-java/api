package org.polkadot.types.type;


import org.polkadot.types.Types;
import org.polkadot.types.codec.Struct;

/**
 * The Substrate BalanceLock for staking
 */
public class BalanceLock extends Struct {

    public BalanceLock(Object value) {
        super(new Types.ConstructorDef()
                        .add("id", LockIdentifier.class)
                        .add("amount", Balance.class)
                        .add("until", BlockNumber.class)
                        .add("reasons", WithdrawReasons.class)
                , value);
    }

    /**
     * The amount
     */
    public Balance getAmount() {
        return this.getField("amount");
    }

    /**
     * The lock id
     */
    public LockIdentifier getId() {
        return this.getField("id");
    }

    /**
     * The reasons
     */
    public WithdrawReasons getReasons() {
        return this.getField("reasons");
    }

    /**
     * Until when this is available
     */
    public BlockNumber getUntil() {
        return this.getField("until");
    }


}
