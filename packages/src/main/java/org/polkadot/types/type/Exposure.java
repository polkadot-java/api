package org.polkadot.types.type;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Compact;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;

/**
 * A snapshot of the stake backing a single validator in the system
 */
public class Exposure extends Struct {

    public Exposure(Object value) {
        super(new Types.ConstructorDef()
                        .add("total", Compact.with(TypesUtils.getConstructorCodec(Balance.class)))
                        .add("own", Compact.with(TypesUtils.getConstructorCodec(Balance.class)))
                        .add("others", Vector.with(TypesUtils.getConstructorCodec(IndividualExposure.class)))
                , value);
    }


    /**
     * The validator"s own stash that is exposed
     */
    public Balance getOwn() {
        Compact own = this.getField("own");
        return new Balance(own.toBn());
    }

    /**
     * The total balance backing this validator
     */
    public Balance getTotal() {

        Compact own = this.getField("total");
        return new Balance(own.toBn());
    }

    /**
     * The portions of nominators stashes that are exposed
     */
    public Vector<IndividualExposure> getOthers() {
        return this.getField("others");
    }
}
