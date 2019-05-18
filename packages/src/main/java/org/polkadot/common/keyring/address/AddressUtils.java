package org.polkadot.common.keyring.address;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.bitcoinj.core.Base58;
import org.polkadot.utils.CryptoUtils;
import org.polkadot.utils.Utils;

public class AddressUtils {

    //export default function decode (encoded: string | Uint8Array, ignoreChecksum?: boolean, prefix: Prefix = defaults.prefix): Uint8Array {
    public static byte[] decodeAddress(byte[] encoded) {
        return decodeAddress(encoded, false, Defaults.prefix);
    }

    public static byte[] decodeAddress(String encoded) {
        return decodeAddress(encoded, false, Defaults.prefix);
    }

    public static byte[] decodeAddress(Object encoded, boolean ignoreChecksum, int prefix) {
        if (Utils.isU8a(encoded) || Utils.isHex(encoded)) {
            return Utils.u8aToU8a(encoded);
        }

        byte[] decoded = Base58.decode((String) encoded);
        String errorPre = "Decoding " + encoded + ":";

        // assert(defaults.allowedPrefix.includes(decoded[0] as Prefix), error('Invalid decoded address prefix'));
        assert Defaults.allowedEncodedLengths.contains(decoded.length)
                : errorPre + "Invalid decoded address length " + decoded.length;

        // TODO Unless it is an "use everywhere" prefix, throw an error
        // if (decoded[0] !== prefix) {
        //   console.log(`WARN: Expected ${prefix}, found ${decoded[0]}`);
        // }

        boolean isPublicKey = decoded.length == 35;

        // non-publicKeys has 1 byte checksums, else default to 2
        int endPos = decoded.length - (isPublicKey ? 2 : 1);

        // calculate the hash and do the checksum byte checks
        byte[] hash = sshash(ArrayUtils.subarray(decoded, 0, endPos));
        boolean checks = isPublicKey
                ? decoded[decoded.length - 2] == hash[0] && decoded[decoded.length - 1] == hash[1]
                : decoded[decoded.length - 1] == hash[0];

        assert ignoreChecksum || checks : errorPre + "Invalid decoded address checksum";

        return ArrayUtils.subarray(decoded, 1, endPos);
    }


    final static byte[] SS58_PREFIX = Utils.stringToU8a("SS58PRE");

    public static byte[] sshash(byte[] key) {
        return CryptoUtils.blake2AsU8a(Utils.u8aConcat(Lists.newArrayList(SS58_PREFIX, key)), 512);
    }


    //export default function encode (_key: Uint8Array | string, prefix: Prefix = defaults.prefix): string {
    public static String encodeAddress(byte[] key) {
        return encodeAddress(key, Defaults.prefix);
    }

    public static String encodeAddress(byte[] _key, byte prefix) {

        byte[] key = Utils.u8aToU8a(_key);

        assert Defaults.allowedDecodedLengths.contains(key.length)
                : "Expected a valid key to convert, with length " + Defaults.allowedDecodedLengths + " : " + key.length;

        boolean isPublicKey = key.length == 32;

        byte[] input = Utils.u8aConcat(Lists.newArrayList(new byte[]{prefix}, key));
        byte[] hash = sshash(input);

        byte[] bytes = Utils.u8aConcat(Lists.newArrayList(input, ArrayUtils.subarray(hash, 0, isPublicKey ? 2 : 1)));

        String result = Base58.encode(bytes);
        //System.out.println(result);
        return result;
    }
}
