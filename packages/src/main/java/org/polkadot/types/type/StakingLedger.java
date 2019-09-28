package org.polkadot.types.type;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Compact;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.primitive.generic.AccountId;

/**
 * The ledger of a (bonded) stash
 */
public class StakingLedger extends Struct {
    public StakingLedger(Object value) {
        super(new Types.ConstructorDef()
                        .add("stash", AccountId.class)
                        .add("total", Compact.with(TypesUtils.getConstructorCodec(Balance.class)))
                        .add("active", Compact.with(TypesUtils.getConstructorCodec(Balance.class)))
                        .add("unlocking", Vector.with(TypesUtils.getConstructorCodec(UnlockChunk.class)))
                , value);
    }

    /**
     * The total amount of the stash's balance that will be at stake in any forthcoming rounds
     */
    public Balance getActive() {
        Compact active = this.getField("active");
        return new Balance(active.toBn());
    }

    /**
     * The stash account whose balance is actually locked and at stake
     */
    public AccountId getStash() {
        return this.getField("stash");
    }

    /**
     * The total amount of the stash's balance that we are currently accounting for. It's just `active` plus all the `unlocking` balances
     */
    public Balance getTotal() {
        Compact total = this.getField("total");
        return new Balance(total.toBn());
    }

    /**
     * Any balance that is becoming free, which may eventually be transferred out of the stash (assuming it doesn't get slashed first)
     */
    public Vector<UnlockChunk> getUnlocking() {
        return this.getField("unlocking");
    }
}
