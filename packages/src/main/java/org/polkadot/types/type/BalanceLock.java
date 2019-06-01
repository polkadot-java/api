package org.polkadot.types.type;


import org.polkadot.types.Types;
import org.polkadot.types.codec.Struct;

/**
 * @name BalanceLock
 * @description The Substrate BalanceLock for staking
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
     * @description The amount
     */
    public Balance getAmount() {
        return this.getField("amount");
    }

    /**
     * @description The lock id
     */
    public LockIdentifier getId() {
        return this.getField("id");
    }

    /**
     * @description The reasons
     */
    public WithdrawReasons getReasons() {
        return this.getField("reasons");
    }

    /**
     * @description Until when this is available
     */
    public BlockNumber getUntil() {
        return this.getField("until");
    }


}
