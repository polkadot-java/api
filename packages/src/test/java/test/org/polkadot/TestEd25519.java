package test.org.polkadot;

import com.google.common.collect.Lists;
import com.google.common.primitives.UnsignedBytes;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.polkadot.common.keyring.Keyring;
import org.polkadot.common.keyring.Types;
import org.polkadot.common.keyring.address.Defaults;
import org.polkadot.utils.Utils;

import java.util.Arrays;
import java.util.List;

public class TestEd25519 {

    Keyring keypair;

    int[] publicKeyOneUb = new int[]{47, 140, 97, 41, 216, 22, 207, 81, 195, 116, 188, 127, 8, 195, 230, 62, 209, 86, 207, 120, 174, 251, 74, 101, 80, 217, 123, 135, 153, 121, 119, 238};
    int[] publicKeyTwoUb = new int[]{215, 90, 152, 1, 130, 177, 10, 183, 213, 75, 254, 211, 201, 100, 7, 58, 14, 225, 114, 243, 218, 166, 35, 37, 175, 2, 26, 104, 247, 7, 81, 26};


    byte[] publicKeyOne;
    byte[] publicKeyTwo;
    byte[] seedOne = Utils.stringToU8a("12345678901234567890123456789012");
    byte[] seedTwo = Utils.hexToU8a("0x9d61b19deffd5a60ba844af492ec2cc44449c5697b326919703bac031cae7f60");


    @BeforeClass
    public void init() {
        publicKeyOne = new byte[publicKeyOneUb.length];
        publicKeyTwo = new byte[publicKeyTwoUb.length];

        for (int i = 0; i < publicKeyOne.length; i++) {
            publicKeyOne[i] = UnsignedBytes.checkedCast(publicKeyOneUb[i]);
        }

        for (int i = 0; i < publicKeyTwo.length; i++) {
            publicKeyTwo[i] = UnsignedBytes.checkedCast(publicKeyTwoUb[i]);
        }
    }

    @Before
    public void before() {
        keypair = new Keyring(new Types.KeyringOptions("ed25519"));
        keypair.addFromSeed(seedOne, new Types.KeyringPairMeta(), "ed25519");
    }

    /**
     * it('adds the pair', () => {
     * expect(
     * keypair.addFromSeed(seedTwo, {}).publicKey()
     * ).toEqual(publicKeyTwo);
     * });
     */
    @Test
    public void addsThePair() {
        byte[] bytes = keypair.addFromSeed(seedTwo, new Types.KeyringPairMeta(), keypair.getType()).publicKey();
        if (!Arrays.equals(bytes, publicKeyTwo)) {
            throw new RuntimeException();
        }
    }

    /*
     expect(
        keypair.addFromUri(
          'seed sock milk update focus rotate barely fade car face mechanic mercy'
        ).address()
      ).toEqual('5DkQP32jP4DVJLWWBRBoZF2tpWjqFrcrTBo6H5NcSk7MxKCC');
    });
     */
    @Test
    public void createsEed25519PairViaMnemonicToSeed() {
        String address = keypair.addFromUri("seed sock milk update focus rotate barely fade car face mechanic mercy",
                new Types.KeyringPairMeta(), keypair.getType()).address();

        if (!address.equals("5DkQP32jP4DVJLWWBRBoZF2tpWjqFrcrTBo6H5NcSk7MxKCC")) {
            //TODO 2019-05-23 08:25
            throw new UnsupportedOperationException();
        }
    }

    /*
    it('adds from a mnemonic', () => {
      setPrefix(68);

      expect(
        keypair.addFromMnemonic('moral movie very draw assault whisper awful rebuild speed purity repeat card', {}).address()
      ).toEqual('7sPsxWPE5DzAyPT3VuoJYw5NTGscx9QYN9oddQx4kALKC3hH');
    });
     */
    @Test
    public void addsFromAMnemonic() {
        Defaults.prefix = 68;

        String address = keypair.addFromMnemonic("moral movie very draw assault whisper awful rebuild speed purity repeat card",
                new Types.KeyringPairMeta(), keypair.getType()).address();
        if (!address.equals("7sPsxWPE5DzAyPT3VuoJYw5NTGscx9QYN9oddQx4kALKC3hH")) {
            //TODO 2019-05-23 08:27
            throw new UnsupportedOperationException();
        }
    }

    /*
    it('allows publicKeys retrieval', () => {
  keypair.addFromSeed(seedTwo, {});

  expect(
    keypair.getPublicKeys()
  ).toEqual([ publicKeyOne, publicKeyTwo ]);
});
     */
    @Test
    public void allowsPublicKeysRetrieval() {
        keypair.addFromSeed(seedTwo, new Types.KeyringPairMeta(), keypair.getType());

        List<byte[]> publicKeys = keypair.getPublicKeys();

        List<byte[]> check = Lists.newArrayList(publicKeyOne, publicKeyTwo);

        for (int i = 0; i < publicKeys.size(); i++) {

            byte[] bytes = publicKeys.get(i);

            if (!Arrays.equals(bytes, check.get(i))) {
                //TODO 2019-05-23 08:29
                throw new UnsupportedOperationException();
            }
        }
    }


    /*
    it('allows retrieval of a specific item', () => {
      expect(
        keypair.getPair(publicKeyOne).publicKey()
      ).toEqual(publicKeyOne);
    });
     */
    @Test
    public void allowsRetrievalOfASpecificItem() {
        //keypair.getPair(publicKeyOne)
    }
}
