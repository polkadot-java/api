package org.polkadot.types.type;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Tuple;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.primitive.U64;
import org.polkadot.utils.MapUtils;

import java.util.List;

/**
 * @name StoredPendingChange
 * @description Stored pending change for a Grandpa events
 */
public class StoredPendingChange extends Struct {

    public static class NextAuthorityValue {
        Number index;
        byte[] sessionKey;
    }

    public static class StoredPendingChangeValue {
        Number scheduledAt;
        Number delay;
        //    nextAuthorities?: Array<Uint8Array | NextAuthorityValue>
        List<NextAuthorityValue> nextAuthorities;
    }


    /**
     * @name NextAuthority
     * @description The next authority available as [[SessionKey]]
     */
    public static class NextAuthority extends Tuple {
        public NextAuthority(Object value) {
            super(new Types.ConstructorDef()
                            .add("SessionKey", SessionKey.class)
                            .add("U64", U64.class)
                    , value);
        }

        public U64 getIndex() {
            return this.getFiled(1);
        }

        public SessionKey getSessionKey() {
            return this.getFiled(0);
        }
    }

    //        constructor (value?: Uint8Array | StoredPendingChangeValue) {
    public StoredPendingChange(Object value) {
        super(new Types.ConstructorDef()
                        .add("scheduledAt", BlockNumber.class)
                        .add("delay", BlockNumber.class)
                        .add("nextAuthorities", Vector.with(TypesUtils.getConstructorCodec(NextAuthority.class)))
                , value
                , MapUtils.ofMap("scheduledAt", "scheduled_at", "nextAuthorities", "next_authorities"));
    }


    public BlockNumber getDelay() {
        return this.getField("delay");
    }

    public Vector<NextAuthority> getNextAuthorities() {
        return this.getField("nextAuthorities");
    }

    public BlockNumber getScheduledAt() {
        return this.getField("scheduledAt");
    }
}
