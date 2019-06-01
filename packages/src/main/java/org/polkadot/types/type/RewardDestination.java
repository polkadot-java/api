package org.polkadot.types.type;


import com.google.common.collect.Lists;
import org.polkadot.types.codec.Enum;

/**
 * @name RewardDestination
 * @description A destination account for payment
 */
public class RewardDestination extends Enum {
    public RewardDestination(Object value) {
        super(Lists.newArrayList(
                // Pay into the stash account, increasing the amount at stake accordingly.
                "Staked",
                // Pay into the stash account, not increasing the amount at stake.
                "Stash",
                // Pay into the controller account.
                "Controller"
                ), value
        );
    }

}
