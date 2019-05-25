package test.org.polkadot;

import org.junit.Test;
import org.polkadot.common.keyring.Keyring;
import org.polkadot.utils.Utils;

public class TestKeyRing {
    public static void main(String[] args) {

    }


    static Keyring keypair;


    @Test
    static void ed25519() {
        //const publicKeyOne = new Uint8Array([47, 140, 97, 41, 216, 22, 207, 81, 195, 116, 188, 127, 8, 195, 230, 62, 209, 86, 207, 120, 174, 251, 74, 101, 80, 217, 123, 135, 153, 121, 119, 238]);
        //   const publicKeyTwo = new Uint8Array([215, 90, 152, 1, 130, 177, 10, 183, 213, 75, 254, 211, 201, 100, 7, 58, 14, 225, 114, 243, 218, 166, 35, 37, 175, 2, 26, 104, 247, 7, 81, 26]);
        //   const seedOne = stringToU8a('12345678901234567890123456789012');
        //   const seedTwo = hexToU8a('0x9d61b19deffd5a60ba844af492ec2cc44449c5697b326919703bac031cae7f60');
        //       let keypair: Keyring;
        int[] publicKeyOne = new int[]{47, 140, 97, 41, 216, 22, 207, 81, 195, 116, 188, 127, 8, 195, 230, 62, 209, 86, 207, 120, 174, 251, 74, 101, 80, 217, 123, 135, 153, 121, 119, 238};
        int[] publicKeyTwo = new int[]{215, 90, 152, 1, 130, 177, 10, 183, 213, 75, 254, 211, 201, 100, 7, 58, 14, 225, 114, 243, 218, 166, 35, 37, 175, 2, 26, 104, 247, 7, 81, 26};

        byte[] seedOne = Utils.stringToU8a("12345678901234567890123456789012");
        byte[] seedTwo = Utils.hexToU8a("0x9d61b19deffd5a60ba844af492ec2cc44449c5697b326919703bac031cae7f60");



    }


    static void sr25519() {

    }
}
