package org.polkadot.types.type;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.EnumType;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Tuple;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.primitive.Bytes;
import org.polkadot.types.primitive.Null;
import org.polkadot.types.primitive.U64;

/**
 * @name AttestedCandidate
 * @description An attested candidate
 */
public class AttestedCandidate extends Struct {


    public AttestedCandidate(Object value) {
        super(new Types.ConstructorDef()
                        .add("candidate", CandidateReceipt.class)
                        .add("validityVotes", Vector.with(TypesUtils.getConstructorCodec(ValidityVote.class)))
                        .add("availabilityVotes", Vector.with(TypesUtils.getConstructorCodec(AvailabilityVote.class)))
                , value);
    }

    public static class CandidateSignature extends Signature {
        public CandidateSignature(Object value) {
            super(value);
        }
    }

    public static class BalanceUpload extends Tuple {
        public BalanceUpload(Object value) {
            super(new Types.ConstructorDef()
                            .add("AccountId", AccountId.class)
                            .add("U64", U64.class),
                    value);
        }
    }


    public static class EgressQueueRoot extends Tuple {

        public EgressQueueRoot(Object value) {
            super(new Types.ConstructorDef()
                            .add("ParaId", ParaId.class)
                            .add("Hash", Hash.class),
                    value);
        }
    }

    public static class HeadData extends Bytes {
        public HeadData(Object value) {
            super(value);
        }
    }


    public static class CandidateReceipt extends Struct {
        CandidateReceipt(Object value) {
            super(new Types.ConstructorDef()
                            .add("parachainIndex", ParaId.class)
                            .add("collator", AccountId.class)
                            .add("signature", CandidateSignature.class)
                            .add("headData", HeadData.class)
                            .add("balanceUploads", Vector.with(TypesUtils.getConstructorCodec(BalanceUpload.class)))
                            .add("egressQueueRoots", Vector.with(TypesUtils.getConstructorCodec(EgressQueueRoot.class)))
                            .add("fees", U64.class)
                            .add("blockDataHash", Hash.class)
                    , value
            );
        }
    }

    public static class AvailabilityVote extends Tuple {
        public AvailabilityVote(Object value) {
            super(new Types.ConstructorDef()
                            .add("SessionKey", SessionKey.class)
                            .add("CandidateSignature", CandidateSignature.class),
                    value);
        }
    }

    public static class ExplicitCandidateSignature extends CandidateSignature {
        public ExplicitCandidateSignature(Object value) {
            super(value);
        }
    }

    class ImplicitCandidateSignature extends CandidateSignature {
        public ImplicitCandidateSignature(Object value) {
            super(value);
        }
    }


    //<Null | ImplicitCandidateSignature | ExplicitCandidateSignature>
    public static class ValidityAttestation extends EnumType {

        public ValidityAttestation(Object value) {

            // This Null is not in the original, however indexes start at 1, so add a
            // placeholder in the first position (which is basically non-valid)
            super(
                    new Types.ConstructorDef()
                            .add("Null", Null.class)
                            .add("ImplicitCandidateSignature", ImplicitCandidateSignature.class)
                            .add("ExplicitCandidateSignature", ExplicitCandidateSignature.class)
                    , value, -1, null);
        }


        /**
         * @description Returns the item as a [[ExplicitCandidateSignature]]
         */
        public ExplicitCandidateSignature asExplicitCandidateSignature() {
            return (ExplicitCandidateSignature) this.value();
        }

        /**
         * @description Returns the item as a [[ImplicitCandidateSignature]]
         */
        public ImplicitCandidateSignature asImplicitCandidateSignature() {
            return (ImplicitCandidateSignature) this.value();
        }
    }


    public static class ValidityVote extends Tuple {
        public ValidityVote(Object value) {
            super(new Types.ConstructorDef()
                            .add("SessionKey", SessionKey.class)
                            .add("ValidityAttestation", ValidityAttestation.class),
                    value);
        }
    }


}
