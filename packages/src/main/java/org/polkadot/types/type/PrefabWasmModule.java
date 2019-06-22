package org.polkadot.types.type;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Compact;
import org.polkadot.types.codec.Option;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.primitive.Bytes;
import org.polkadot.types.primitive.Null;
import org.polkadot.types.primitive.U32;

/**
 * Struct to encode the vesting schedule of an individual account
 */
public class PrefabWasmModule extends Struct {

    public static class PrefabWasmModuleReserved extends Option<Null> {
        public PrefabWasmModuleReserved() {
            super(TypesUtils.getConstructorCodec(Null.class), null);
        }
    }

    public PrefabWasmModule(Object value) {
        super(new Types.ConstructorDef()
                        .add("scheduleVersion", Compact.with(TypesUtils.getConstructorCodec(U32.class)))
                        .add("initial", Compact.with(TypesUtils.getConstructorCodec(U32.class)))
                        .add("maximum", Compact.with(TypesUtils.getConstructorCodec(U32.class)))
                        .add("_reserved", PrefabWasmModuleReserved.class)
                        .add("code", Bytes.class)

                , value);
    }

    /**
     * The code as {@link org.polkadot.types.primitive.Bytes}
     */
    public Bytes getCode() {
        return this.getField("code");
    }

    /**
     * The initial as {@link org.polkadot.types.codec.Compact}
     */
    public Compact getInitial() {
        return this.getField("initial");
    }

    /**
     * The maximum as {@link org.polkadot.types.codec.Compact}
     */
    public Compact getMaximum() {
        return this.getField("maximum");
    }

    /**
     * The scheduleVersion value as {@link org.polkadot.types.codec.Compact}
     */
    public Compact getScheduleVersion() {
        return this.getField("scheduleVersion");
    }
}
