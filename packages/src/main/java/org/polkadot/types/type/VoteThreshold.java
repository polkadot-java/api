package org.polkadot.types.type;

import com.google.common.collect.Lists;
import org.polkadot.types.codec.Enum;

/**
 * Voting threshold, used inside proposals to set change the voting tally
 */
public class VoteThreshold extends Enum {
    //  constructor (index?: number | Uint8Array) {
    public VoteThreshold(Object index) {
        super(Lists.newArrayList(
                "Super majority approval",
                "Super majority rejection",
                "Simple majority")
                , index);
    }
}
