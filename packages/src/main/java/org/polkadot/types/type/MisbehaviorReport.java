package org.polkadot.types.type;

import org.polkadot.types.Types;
import org.polkadot.types.codec.EnumType;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.primitive.U32;
import org.polkadot.types.type.BftAuthoritySignature.BftHashSignature;
import org.polkadot.types.type.BftAuthoritySignature.BftHashSignatureValue;

/**
 * @name MisbehaviorReport
 * @description A Misbehaviour report of [[MisbehavioirKind]] against a specific [[AuthorityId]]
 */
public class MisbehaviorReport extends Struct {


    public static class BftAtReportValueSingle {
        private Number round;
        private BftHashSignatureValue a;

        public Number getRound() {
            return round;
        }

        public void setRound(Number round) {
            this.round = round;
        }

        public BftHashSignatureValue getA() {
            return a;
        }

        public void setA(BftHashSignatureValue a) {
            this.a = a;
        }
    }

    public static class BftAtReportValue {
        private BftAtReportValueSingle bftAtReportValueSingle;
        private BftHashSignatureValue b;

        public BftAtReportValueSingle getBftAtReportValueSingle() {
            return bftAtReportValueSingle;
        }

        public void setBftAtReportValueSingle(BftAtReportValueSingle bftAtReportValueSingle) {
            this.bftAtReportValueSingle = bftAtReportValueSingle;
        }

        public BftHashSignatureValue getB() {
            return b;
        }

        public void setB(BftHashSignatureValue b) {
            this.b = b;
        }
    }


    /**
     * @name BftAtReport
     * @description A report of a/b hash-signature pairs for a specific index. This is the same
     * structure as is used in BftDoublePrepare & BftDoubleCommit
     */
    // FIXME It is not entirely obvious from the actual Rust code what the specific
    // items in the structure is called, except a & b (one should be expected, the
    // other actual)
    public static class BftAtReport extends Struct {
        //        constructor (value? BftAtReportValue | Uint8Array) {
        public BftAtReport(Object value) {
            super(new Types.ConstructorDef()
                            .add("round", U32.class)
                            .add("a", BftHashSignature.class)
                            .add("b", BftHashSignature.class)
                    , value);
        }

        /**
         * @description The first report [[BftHashSignature]]
         */
        public BftHashSignature getA() {
            return this.getField("a");
        }

        /**
         * @description The second report [[BftHashSignature]]
         */
        public BftHashSignature getB() {
            return this.getField("b");
        }

        /**
         * @description The round this report applies to as [[U32]]
         */
        public U32 getRound() {
            return this.getField("round");
        }
    }


    /**
     * @name BftProposeOutOfTurn
     * @description A report for out-of-turn proposals
     */
    public static class BftProposeOutOfTurn extends Struct {
        //constructor (value? BftAtReportValue | Uint8Array) {

        public BftProposeOutOfTurn(Object value) {
            super(new Types.ConstructorDef()
                            .add("round", U32.class)
                            .add("a", BftHashSignature.class)
                    , value);

        }

        /**
         * @description The [[BftHashSignature]] the report applies to
         */
        public BftHashSignature getA() {
            return this.getField("a");
        }

        /**
         * @description The round as [[u32]]
         */
        public U32 getRound() {
            return this.getField("round");
        }
    }

    /**
     * @name BftDoublePropose
     * @description Report of a double-propose
     */
    public static class BftDoublePropose extends BftAtReport {
        public BftDoublePropose(Object value) {
            super(value);
        }
    }

    /**
     * @name BftDoublePrepare
     * @description Report of a double-prepare
     */
    public static class BftDoublePrepare extends BftAtReport {
        public BftDoublePrepare(Object value) {
            super(value);
        }
    }

    /**
     * @name BftDoubleCommit
     * @description Report of a double-commit
     */
    public static class BftDoubleCommit extends BftAtReport {
        public BftDoubleCommit(Object value) {
            super(value);
        }
    }

    /**
     * @name MisbehaviorKind
     * @description An [[EnumType]] containing a Bft misbehaviour
     */
    //EnumType<BftProposeOutOfTurn | BftDoublePropose | BftDoublePrepare | BftDoubleCommit> {
    public static class MisbehaviorKind extends EnumType {
        //        constructor (value? BftAtReportValue | Uint8Array, index? number) {
        public MisbehaviorKind(Object value, int index) {
            super(new Types.ConstructorDef()
                            .add("BftProposeOutOfTurn", BftProposeOutOfTurn.class)
                            .add("BftDoublePropose", BftDoublePropose.class)
                            .add("BftDoublePrepare", BftDoublePrepare.class)
                            .add("BftDoubleCommit", BftDoubleCommit.class)
                    , value, index, null);
        }

        /**
         * @description Returns the item as a [[BftDoubleCommit]]
         */
        public BftDoubleCommit asBftDoubleCommit() {
            return (BftDoubleCommit) this.value();
        }

        /**
         * @description Returns the item as a [[BftDoublePrepare]]
         */
        public BftDoublePrepare asBftDoublePrepare() {
            return (BftDoublePrepare) this.value();
        }

        /**
         * @description Returns the item as a [[BftDoublePropose]]
         */
        public BftDoublePropose asBftDoublePropose() {
            return (BftDoublePropose) this.value();
        }

        /**
         * @description Returns the item as a [[BftProposeOutOfTurn]]
         */
        public BftProposeOutOfTurn asBftProposeOutOfTurn() {
            return (BftProposeOutOfTurn) this.value();
        }
    }

//    type MisbehaviorReportValue = {
//            misbehavior? MisbehaviorKind | number,
//    parentHash? Hash | Uint8Array | string,
//    parentNumber? AnyNumber,
//    target? AuthorityId | string
//};

    public static class MisbehaviorReportValue {

    }


    //    constructor (value? MisbehaviorReportValue | Uint8Array) {
    public MisbehaviorReport(Object value) {
        super(new Types.ConstructorDef()
                        .add("parentHash", Hash.class)
                        .add("parentNumber", BlockNumber.class)
                        .add("target", AuthorityId.class)
                        .add("misbehavior", MisbehaviorKind.class)
                , value);
    }


    /**
     * @description The [[MisbehaviorKind]]
     */
    public MisbehaviorKind getMisbehavior() {
        return this.getField("misbehavior");
    }

    /**
     * @description The [[Hash]] of the parent block
     */
    public Hash getParentHash() {
        return this.getField("parentHash");
    }

    /**
     * @description The [[BlockNumber]] of the parent
     */
    public BlockNumber getParentNumber() {
        return this.getField("parentNumber");
    }

    /**
     * @description The [[authorityId]] the report applies to
     */
    public AuthorityId getTarget() {
        return this.getField("target");
    }
}

