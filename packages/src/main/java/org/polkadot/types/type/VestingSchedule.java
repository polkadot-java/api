package org.polkadot.types.type;


import org.polkadot.types.Types;
import org.polkadot.types.codec.Struct;

/**
 * @name VestingSchedule
 * @description Struct to encode the vesting schedule of an individual account
 */
public class VestingSchedule extends Struct {
    public VestingSchedule(Object value) {
        super(new Types.ConstructorDef()
                        .add("offset", Balance.class)
                        .add("perBlock", Balance.class)
                , value);
    }


    /**
     * @description The offset as [[Balance]]
     */
    public Balance getOffset() {
        return this.getField("offset");
    }

    /**
     * @description The perBlock value as [[Balance]]
     */
    public Balance getPerBlock() {
        return this.getField("perBlock");
    }
}
