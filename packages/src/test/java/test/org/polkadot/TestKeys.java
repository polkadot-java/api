package test.org.polkadot;

import com.google.common.primitives.UnsignedBytes;
import org.polkadot.common.keyring.pair.Index;
import org.polkadot.common.keyring.pair.Types;
import org.polkadot.utils.Utils;

public class TestKeys {
    public static void main(String[] args) {

        int[] basePublicKey = new int[]{212, 53, 147, 199, 21, 253, 211, 28, 97, 20, 26, 189, 4, 169, 159, 214, 130, 44, 133, 88, 133, 76, 205, 227, 154, 86, 132, 231, 165, 109, 162, 125};
        int[] baseSecretKey = new int[]{152, 49, 157, 79, 248, 169, 80, 140, 75, 176, 207, 11, 90, 120, 215, 96, 160, 178, 8, 44, 2, 119, 94, 110, 130, 55, 8, 22, 254, 223, 255, 72, 146, 90, 34, 93, 151, 170, 0, 104, 45, 106, 89, 185, 91, 24, 120, 12, 16, 215, 3, 35, 54, 232, 143, 52, 66, 180, 35, 97, 244, 166, 96, 17};

        byte[] publicKey = new byte[basePublicKey.length];
        byte[] secretKey = new byte[baseSecretKey.length];

        for (int i = 0; i < basePublicKey.length; i++) {
            publicKey[i] = UnsignedBytes.checkedCast(basePublicKey[i]);
        }

        for (int i = 0; i < baseSecretKey.length; i++) {
            secretKey[i] = UnsignedBytes.checkedCast(baseSecretKey[i]);
        }
        //if (_suri.equals("//Alice")) {
        //
        //}
        //TODO 2019-05-25 03:15


        Types.PairInfo pairInfo = new Types.PairInfo();
        pairInfo.setPublicKey(publicKey);
        pairInfo.setSecretKey(secretKey);

        System.out.println(Utils.u8aToHex(publicKey));
        System.out.println(Utils.u8aToHex(secretKey));

        //org.polkadot.common.keyring.Types.KeyringPair pair = Index.createPair(type, pairInfo, meta, null);
        //return pair;
    }
}
