package org.polkadot.types.type;

import org.polkadot.types.Types;
import org.polkadot.types.codec.EnumType;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.primitive.Null;
import org.polkadot.types.primitive.U32;

/**
 * @name EventRecord
 * @description A record for an [[Event]] (as specified by [[Metadata]]) with the specific [[Phase]] of
 * application.
 */
public class EventRecord extends Struct {
    public EventRecord(Object value) {
        super(new Types.ConstructorDef()
                        .add("phase", Phase.class)
                        .add("event", Event.class)
                , value);
    }

    /**
     * @description The [[Event]] this record refers to
     */
    public Event getEvent() {
        return this.getField("event");
    }

    /**
     * @description The [[Phase]] where the event was generated
     */
    public Phase getPahase() {
        return this.getField("phase");
    }


    /**
     * @name ApplyExtrinsic
     * @description The [[Phase]] where the extrinsic is applied
     */
    public static class ApplyExtrinsic extends U32 {
        public ApplyExtrinsic(Object value) {
            super(value);
        }
    }

    /**
     * @name Finalization
     * @description The [[Phase]] where the extrinsic is being Finalized
     */
    public static class Finalization extends Null {
    }

    /**
     * @name Phase
     * @description An [[EnumType]] that indicates the specific phase where the [[EventRecord]] was generated
     */
    //export class Phase extends EnumType<ApplyExtrinsic | Finalization> {
    public static class Phase extends EnumType {
        public Phase(Object value, int index) {
            super(new Types.ConstructorDef()
                            .add("ApplyExtrinsic", ApplyExtrinsic.class)
                            .add("Finalization", Finalization.class)
                    , value, index, null);
        }


        /**
         * @description Returns the item as a [[ApplyExtrinsic]]
         */
        public ApplyExtrinsic asApplyExtrinsic() {
            return (ApplyExtrinsic) this.value();
        }

        /**
         * @description Returns the item as a [[Finalization]]
         */
        public Finalization asFinalization() {
            return (Finalization) this.value();
        }

        /**
         * @description true when this is a ApplyExtrinsic
         */
        public boolean isApplyExtrinsic() {
            return this.getType().equals("ApplyExtrinsic");
        }

        /**
         * @description true when this is a ApplyExtrinsic
         */
        public boolean isFinalization() {
            return this.getType().equals("Finalization");
        }
    }


}
