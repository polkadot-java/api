package org.polkadot.types.type;

import com.google.common.collect.Maps;
import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.primitive.Bytes;
import org.polkadot.types.primitive.U32;
import org.polkadot.types.type.BftAuthoritySignature.BftAuthoritySignatureValue;

import java.util.List;
import java.util.Map;

/**
 * @name Justification
 * A generic justification as a stream of [[Bytes]], this is specific per consensus implementation
 */
public class Justification extends Bytes {
    public Justification(Object value) {
        super(value);
    }

    public static class RhdJustificationValue {

        //roundNumber?: AnyNumber,
        //hash?: AnyU8a,
        //signatures?: Array<BftAuthoritySignatureValue>

        Object roundNumber;
        Hash hash;
        List<BftAuthoritySignatureValue> signatures;
    }

    /**
     * @name RhdJustification
     * [[Justification]] for the Rhododendron consensus algorithm
     */
    public static class RhdJustification extends Struct {

        public static Map<String, String> JSON_MAP = Maps.newLinkedHashMap();

        static {
            JSON_MAP.put("roundNumber", "round_number");
        }


        //  constructor (value?: RhdJustificationValue | Uint8Array) {
        public RhdJustification(Object value) {
            super(new Types.ConstructorDef()
                            .add("roundNumber", U32.class)
                            .add("hash", Hash.class)
                            .add("signatures", Vector.with(TypesUtils.getConstructorCodec(BftAuthoritySignature.class)))
                    , value, JSON_MAP);
        }


        /**
         * The justification [[Hash]]
         */
        public Hash getHash() {
            return this.getField("hash");
        }

        /**
         * The round this justification wraps as a [[U32]]
         */
        public U32 getRoundNumber() {
            return this.getField("roundNumber");
        }

        /**
         * The [[BftAuthoritySignature]] array
         */
        public Vector<BftAuthoritySignature> getSignatures() {
            return this.getField("signatures");
        }
    }
}
