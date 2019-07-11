package test.org.polkadot.api;

import com.onehilltech.promises.Promise;
import org.polkadot.common.keyring.Types;
import org.polkadot.types.Types.IExtrinsic;
import org.polkadot.types.Types.SignatureOptions;

public class SingleAccountSigner implements org.polkadot.api.Types.Signer {

    static int id = 0;
    private Types.KeyringPair keyringPair;

    public SingleAccountSigner(Types.KeyringPair keyringPair) {
        this.keyringPair = keyringPair;
    }


    @Override
    public Promise<Integer> sign(IExtrinsic extrinsic, String address, SignatureOptions options) {
        if (this.keyringPair == null || !this.keyringPair.address().equals(address)) {
            throw new RuntimeException("does not have the keyringPair");
        }
        extrinsic.sign(this.keyringPair, options);

        return Promise.value(++id);
    }

    @Override
    public void update(int id, Object status) {
        //TODO 2019-07-05 21:48
        throw new UnsupportedOperationException();
    }
}
