package org.polkadot.types.type;


import org.polkadot.types.Types;
import org.polkadot.types.codec.Struct;

/**
 * @name TreasuryProposal
 * @description A Proposal made for Treasury
 */
public class TreasuryProposal extends Struct {
    public TreasuryProposal(Object value) {
        super(new Types.ConstructorDef()
                        .add("proposer", AccountId.class)
                        .add("value", Balance.class)
                        .add("beneficiary", AccountId.class)
                        .add("bond", Balance.class)
                , value);
    }


    /**
     * @description The beneficiary
     */
    public AccountId getBeneficiary() {
        return this.getField("beneficiary");
    }

    /**
     * @description The bond
     */
    public Balance getBond() {
        return this.getField("bond");
    }

    /**
     * @description The proposer
     */
    public AccountId getProposer() {
        return this.getField("proposer");
    }

    /**
     * @description The bond
     */
    public Balance getValue() {
        return this.getField("value");
    }
}