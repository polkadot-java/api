package org.polkadot.utils.crypto;


import org.polkadot.utils.MapUtils;
import org.polkadot.utils.Utils;

import java.util.Arrays;
import java.util.Map;

/**
 * Copy Bitcoin source code, remove Base58 unrelated code
 */
public class Base58 {
    // Bsae58 code
    public static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
    private static final char ENCODED_ZERO = ALPHABET[0];
    private static final int[] INDEXES = new int[128];

    static {
        Arrays.fill(INDEXES, -1);
        for (int i = 0; i < ALPHABET.length; i++) {
            INDEXES[ALPHABET[i]] = i;
        }
    }

    // Base58 encode
    public static String encode(byte[] input) {
        if (input.length == 0) {
            return "";
        }
        // count leading zeros
        int zeros = 0;
        while (zeros < input.length && input[zeros] == 0) {
            ++zeros;
        }
        // duplicate to modify
        input = Arrays.copyOf(input, input.length);
        // max code length
        char[] encoded = new char[input.length * 2];
        int outputStart = encoded.length;
        // Base58 codec begin
        for (int inputStart = zeros; inputStart < input.length; ) {
            encoded[--outputStart] = ALPHABET[divmod(input, inputStart, 256, 58)];
            if (input[inputStart] == 0) {
                ++inputStart;
            }
        }
        while (outputStart < encoded.length && encoded[outputStart] == ENCODED_ZERO) {
            ++outputStart;
        }
        while (--zeros >= 0) {
            encoded[--outputStart] = ENCODED_ZERO;
        }
        return new String(encoded, outputStart, encoded.length - outputStart);
    }

    public static byte[] decode(String input) {
        if (input.length() == 0) {
            return new byte[0];
        }
        byte[] input58 = new byte[input.length()];
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            int digit = c < 128 ? INDEXES[c] : -1;
            if (digit < 0) {
                String msg = "Invalid characters,c=" + c;
                throw new RuntimeException(msg);
            }
            input58[i] = (byte) digit;
        }
        int zeros = 0;
        while (zeros < input58.length && input58[zeros] == 0) {
            ++zeros;
        }
        byte[] decoded = new byte[input.length()];
        int outputStart = decoded.length;
        for (int inputStart = zeros; inputStart < input58.length; ) {
            decoded[--outputStart] = divmod(input58, inputStart, 58, 256);
            if (input58[inputStart] == 0) {
                ++inputStart;
            }
        }
        while (outputStart < decoded.length && decoded[outputStart] == 0) {
            ++outputStart;
        }
        return Arrays.copyOfRange(decoded, outputStart - zeros, decoded.length);
    }

    private static byte divmod(byte[] number, int firstDigit, int base, int divisor) {
        int remainder = 0;
        for (int i = firstDigit; i < number.length; i++) {
            int digit = (int) number[i] & 0xFF;
            int temp = remainder * base + digit;
            number[i] = (byte) (temp / divisor);
            remainder = temp % divisor;
        }
        return (byte) remainder;
    }


    /**
     * const address = '16UjcYNBG9GTK4uq2f7yYEbuifqCzoLMGS'
     * const bytes = bs58.decode(address)
     * console.log(out.toString('hex'))
     * // => 003c176e659bea0f29a3e9bf7880c112b1b31b4dc826268187
     *
     * @param args
     */

    //////
    //static String Alice = "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY";
    //static String alice_result = "2AD43593C715FDD31C61141ABD04A99FD6822C8558854CCDE39A5684E7A56DA27D1D21";
//////
//    static String Alice = "16UjcYNBG9GTK4uq2f7yYEbuifqCzoLMGS";
//    static String alice_result = "003c176e659bea0f29a3e9bf7880c112b1b31b4dc826268187";


    static Map<String, String> testCases = MapUtils.ofMap(
            "16UjcYNBG9GTK4uq2f7yYEbuifqCzoLMGS", "003c176e659bea0f29a3e9bf7880c112b1b31b4dc826268187",
            "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY", "2AD43593C715FDD31C61141ABD04A99FD6822C8558854CCDE39A5684E7A56DA27D1D21"
    );


    public static void main(String[] args) {

        for (Map.Entry<String, String> entry : testCases.entrySet()) {
            System.out.println(entry.getKey());

            byte[] decode = Base58.decode(entry.getKey());
            String result = Utils.u8aToHex(decode, -1, false);

            System.out.println(result.toUpperCase());

            System.out.println(entry.getValue());

            if (!result.equalsIgnoreCase(entry.getValue())) {
                //TODO 2019-05-15 01:15
                throw new UnsupportedOperationException();
            }

            String encode = Base58.encode(decode);
            System.out.println(encode);

            //System.out.println();
        }

        System.out.println("=========");

        for (Map.Entry<String, String> entry : testCases.entrySet()) {
            System.out.println(entry.getKey());

            byte[] decode = org.bitcoinj.core.Base58.decode(entry.getKey());
            String result = Utils.u8aToHex(decode, -1, false);

            System.out.println(result.toUpperCase());

            System.out.println(entry.getValue());

            if (!result.equalsIgnoreCase(entry.getValue())) {
                //TODO 2019-05-15 01:15
                throw new UnsupportedOperationException();
            }

            String encode = org.bitcoinj.core.Base58.encode(decode);
            System.out.println(encode);

            //System.out.println();
        }

        //for (byte b : ndata) {
        //    System.out.println(b);
        //}
    }
}