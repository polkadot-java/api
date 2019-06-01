package org.polkadot.types.type;

import com.google.common.collect.Lists;
import org.polkadot.types.codec.Set;
import org.polkadot.utils.MapUtils;

/**
 * @name WithdrawReasons
 * @description The Substrate WithdrawReasons for staking
 */
public class WithdrawReasons extends Set {


    public WithdrawReasons(Object value) {
        super(new SetValues(
                        MapUtils.ofMap(
                                "TransactionPayment", 0b00000001,
                                "Transfer", 0b00000010,
                                "Reserve", 0b00000100
                        )),
                value == null
                        ? Lists.newArrayList("header", "body", "justification")
                        : value);
    }


    /**
     * @description In order to reserve some funds for a later return or repatriation
     */
    public boolean isReserve() {
        return this.values().contains("Reserve");
    }

    /**
     * @description In order to pay for (system) transaction costs
     */
    public boolean isTransactionPayment() {
        return this.values().contains("TransactionPayment");
    }

    /**
     * @description In order to transfer ownership
     */
    public boolean isTransfer() {
        return this.values().contains("Transfer");
    }
}
