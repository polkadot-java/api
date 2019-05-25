package org.polkadot.common.keyring.pair;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.polkadot.common.keyring.Types.KeyringPairJson;
import org.polkadot.common.keyring.Types.KeyringPairJsonEncoding;
import org.polkadot.common.keyring.Types.KeyringPairMeta;
import org.polkadot.common.keyring.address.AddressCodec;
import org.polkadot.utils.Utils;
import org.polkadot.utils.crypto.Nacl;

import static org.polkadot.common.keyring.pair.Defaults.*;

public class PairCodec {

    public static final int SEED_OFFSET = PKCS8_HEADER.length;


    public static class DecodeResult extends Types.PairInfo {
        byte[] secretKey;


        @Override
        public byte[] getSecretKey() {
            return secretKey;
        }

        @Override
        public void setSecretKey(byte[] secretKey) {
            this.secretKey = secretKey;
        }
    }

    public static DecodeResult decode(String passphrase, byte[] encrypted) {
        assert encrypted != null : "No encrypted data available to decode";

        byte[] encoded = passphrase != null
                ? Nacl.naclDecrypt(
                ArrayUtils.subarray(encrypted, NONCE_LENGTH, encrypted.length),
                ArrayUtils.subarray(encrypted, 0, NONCE_LENGTH),
                Utils.u8aFixLength(Utils.stringToU8a(passphrase), 256, true))
                : encrypted;

        assert encoded != null : "Unable to unencrypt using the supplied passphrase";

        byte[] header = ArrayUtils.subarray(encoded, 0, PKCS8_HEADER.length);

        assert Utils.u8aStrEq(header, PKCS8_HEADER) : "Invalid Pkcs8 header found in body";

        byte[] secretKey = ArrayUtils.subarray(encoded, SEED_OFFSET, SEED_OFFSET + SEC_LENGTH);
        int divOffset = SEED_OFFSET + SEC_LENGTH;
        byte[] divider = ArrayUtils.subarray(encoded, divOffset, divOffset + PKCS8_DIVIDER.length);


        // old-style, we have the seed here
        if (!Utils.u8aStrEq(divider, PKCS8_DIVIDER)) {
            divOffset = SEED_OFFSET + SEED_LENGTH;
            secretKey = ArrayUtils.subarray(encoded, SEED_OFFSET, divOffset);
            divider = ArrayUtils.subarray(encoded, divOffset, divOffset + PKCS8_DIVIDER.length);
        }

        assert Utils.u8aStrEq(divider, PKCS8_DIVIDER) : "Invalid Pkcs8 divider found in body";

        int pubOffset = divOffset + PKCS8_DIVIDER.length;

        byte[] publicKey = ArrayUtils.subarray(encoded, pubOffset, pubOffset + PUB_LENGTH);

        DecodeResult ret = new DecodeResult();
        ret.publicKey = publicKey;
        ret.secretKey = secretKey;
        return ret;
    }


    public static byte[] encode(Types.PairInfo pairInfo, String passphrase) {
        assert pairInfo.getSecretKey() != null : "Expected a valid secretKey to be passed to encode";

        byte[] encoded = Utils.u8aConcat(Lists.newArrayList(
                PKCS8_HEADER,
                pairInfo.getSecretKey(),
                PKCS8_DIVIDER,
                pairInfo.getPublicKey()
        ));

        if (passphrase == null) {
            return encoded;
        }

        Nacl.Encrypted encrypted = Nacl.naclEncrypt(encoded, Utils.u8aFixLength(Utils.stringToU8a(passphrase), 256, true));

        return Utils.u8aConcat(Lists.newArrayList(encrypted.getNonce(), encrypted.getEncrypted()));
    }

    public static class PairStateJson {
        KeyringPairMeta meta;
        byte[] publicKey;

        public PairStateJson(KeyringPairMeta meta, byte[] publicKey) {
            this.meta = meta;
            this.publicKey = publicKey;
        }
    }

    //     export default function toJson (type: KeypairType, { publicKey, meta }: PairStateJson, encoded: Uint8Array, isEncrypted: boolean): KeyringPair$Json {
    public static KeyringPairJson toJson(String type, PairStateJson pairStateJson, byte[] encoded, boolean isEncryptede) {
        KeyringPairJson result = new KeyringPairJson();
        result.setAddress(AddressCodec.encodeAddress(pairStateJson.publicKey));
        result.setEncoded(Utils.u8aToHex(encoded));
        KeyringPairJsonEncoding encoding = new KeyringPairJsonEncoding();
        encoding.setContent(new String[]{"pkcs8", type.toString()});
        encoding.setType(isEncryptede ? "xsalsa20-poly1305" : "none");
        encoding.setVersion("2");
        result.setEncoding(encoding);
        //     meta
        result.setMeta(pairStateJson.meta);
        return result;
    }
    /**
     *
     type PairStateJson =  & {
     publicKey: Uint8Array
     };

     export default function toJson (type: KeypairType, { publicKey, meta }: PairStateJson, encoded: Uint8Array, isEncrypted: boolean): KeyringPair$Json {
     return {
     address: encodeAddress(publicKey),
     encoded: u8aToHex(encoded),
     encoding: {
     content: ["pkcs8", type],
     type: isEncrypted
     ? "xsalsa20-poly1305"
     : "none",
     version: "2"
     },
     meta
     };
     }

     */
}
