package org.polkadot.types.type;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Compact;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.primitive.generic.AccountId;

/**
 * The Substrate IndividualExposure for staking
 */
public class IndividualExposure extends Struct {
    public IndividualExposure(Object value) {
        super(new Types.ConstructorDef()
                        .add("who", AccountId.class)
                        .add("value", Compact.with(TypesUtils.getConstructorCodec(Balance.class)))
                , value);
    }

    /**
     * The value
     */
    public Balance getValue() {
        Compact compact = this.getField("value");
        return new Balance(compact.toBn());
    }

    /**
     * The AccountId
     */
    public AccountId getWho() {
        return this.getField("who");
    }
}
