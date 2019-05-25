package org.polkadot.api.derive;

import com.onehilltech.promises.Promise;
import org.polkadot.direct.IFunction;
import org.polkadot.types.type.AccountId;
import org.polkadot.types.type.Balance;
import org.polkadot.types.type.Vote;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;

public interface Types {

    interface DeriveRealFunction extends IFunction {
        Promise call(Object... args);
    }

    class DerivedBalances {
        public AccountId accountId;
        public Balance freeBalance;
        public Balance nominatedBalance;
        public Balance reservedBalance;
        public Balance votingBalance;
        public Balance stakingBalance;
        public List<DerivedBalances> nominators;

        public DerivedBalances(AccountId accountId, Balance freeBalance, Balance nominatedBalance, Balance reservedBalance, Balance votingBalance, Balance stakingBalance, List<DerivedBalances> nominators) {
            this.accountId = accountId;
            this.freeBalance = freeBalance;
            this.nominatedBalance = nominatedBalance;
            this.reservedBalance = reservedBalance;
            this.votingBalance = votingBalance;
            this.stakingBalance = stakingBalance;
            this.nominators = nominators;
        }
    }

    class DerivedFees {
        BigInteger creationFee;
        BigInteger existentialDeposit;
        BigInteger transactionBaseFee;
        BigInteger transactionByteFee;
        BigInteger transferFee;

        public DerivedFees(BigInteger creationFee, BigInteger existentialDeposit, BigInteger transactionBaseFee, BigInteger transactionByteFee, BigInteger transferFee) {
            this.creationFee = creationFee;
            this.existentialDeposit = existentialDeposit;
            this.transactionBaseFee = transactionBaseFee;
            this.transactionByteFee = transactionByteFee;
            this.transferFee = transferFee;
        }
    }

    class DerivedBalancesMap extends LinkedHashMap<String, DerivedBalances> {
    }

    class DerivedReferendumVote {
        AccountId accountId;
        Balance balance;
        Vote vote;

        public DerivedReferendumVote(AccountId accountId, Balance balance, Vote vote) {
            this.accountId = accountId;
            this.balance = balance;
            this.vote = vote;
        }
    }

}
