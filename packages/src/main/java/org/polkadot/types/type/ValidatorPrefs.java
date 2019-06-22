package org.polkadot.types.type;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Compact;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.primitive.U32;

/**
 * Validator preferences
 */
public class ValidatorPrefs extends Struct {
    public ValidatorPrefs(Object value) {
        super(new Types.ConstructorDef()
                        .add("unstakeThreshold", Compact.with(TypesUtils.getConstructorCodec(U32.class)))
                        .add("validatorPayment", Compact.with(TypesUtils.getConstructorCodec(Balance.class)))
                , value);
    }


    /**
     * The unstake threshold as {@link org.polkadot.types.primitive.U32}
     */
    public U32 getUnstakeThreshold() {
        return this.getField("unstakeThreshold");
    }

    /**
     * The payment config for the validator as a {@link org.polkadot.types.codec.Compact} {@link org.polkadot.types.type.Balance}
     */
    public Compact getValidatorPayment() {
        return this.getField("validatorPayment");
    }
}
