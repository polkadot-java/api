package org.polkadot.types.type;


import org.polkadot.types.Types;
import org.polkadot.types.codec.Struct;

/**
 * Struct to encode the vesting schedule of an individual account
 */
public class VestingSchedule extends Struct {
    public VestingSchedule(Object value) {
        super(new Types.ConstructorDef()
                        .add("offset", Balance.class)
                        .add("perBlock", Balance.class)
                , value);
    }


    /**
     * The offset as {@link org.polkadot.types.type.Balance}
     */
    public Balance getOffset() {
        return this.getField("offset");
    }

    /**
     * The perBlock value as {@link org.polkadot.types.type.Balance}
     */
    public Balance getPerBlock() {
        return this.getField("perBlock");
    }
}
