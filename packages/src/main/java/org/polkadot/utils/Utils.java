package org.polkadot.utils;

import com.google.common.collect.Lists;
import com.google.common.primitives.UnsignedBytes;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.CaseUtils;
import org.polkadot.types.codec.CodecUtils;
import org.polkadot.types.codec.Compact;
import org.polkadot.types.codec.Option;
import org.polkadot.types.codec.U8a;
import org.polkadot.types.type.ExtrinsicSignature;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {

    /**
     * @name isHex
     * @summary Tests for a hex string.
     * @description Checks to see if the input value is a `0x` prefixed hex string. Optionally (`bitLength` !== -1) checks to see if the bitLength is correct.
     * @example <BR>
     * <p>
     * ```javascript
     * import { isHex } from '@polkadot/util';
     * <p>
     * isHex('0x1234'); // => true
     * isHex('0x1234', 8); // => false
     * ```
     */
    static final String HEX_REGEX = "^0x[a-fA-F0-9]+$";

    //export default function isHex (value: any, bitLength: number = -1, ignoreLength: boolean = false): value is string | String {
    public static boolean isHex(Object value) {
        return isHex(value, -1, false);
    }

    public static boolean isHex(Object value, int bitLength, boolean ignoreLength) {
        if (value == null) {
            return false;
        }
        //CharSequence value = _value.toString();
        boolean isValidHex = value.equals("0x") || (value instanceof String && Pattern.matches(HEX_REGEX, (CharSequence) value));

        if (isValidHex && bitLength != -1) {
            String strValue = (String) value;
            return strValue.length() == (2 + (int) Math.ceil(bitLength / 4));
        }

        return isValidHex && (ignoreLength || (((String) value).length() % 2 == 0));
    }


    /**
     * @name hexToU8a
     * @summary Creates a Buffer object from a hex string.
     * @description `null` inputs returns an empty `Uint8Array` result. Hex input values return the actual bytes value converted to a Uint8Array. Anything that is not a hex string (including the `0x` prefix) throws an error.
     * @example <BR>
     * <p>
     * ```javascript
     * import { hexToU8a } from '@polkadot/util';
     * <p>
     * hexToU8a('0x80001f'); // Uint8Array([0x80, 0x00, 0x1f])
     * hexToU8a('0x80001f', 32); // Uint8Array([0x00, 0x80, 0x00, 0x1f])
     * ```
     */
    //export default function hexToU8a (_value?: string | null, bitLength: number = -1): Uint8Array {
    public static byte[] hexToU8a(String value, int bitLength) {
        if (value == null) {
            return new byte[0];
        }

        assert isHex(value) : "Expected hex value to convert, found " + value;

        value = hexStripPrefix(value);
        int valLength = value.length() / 2;
        int bufLength = (int) Math.ceil((
                bitLength == -1
                        ? valLength
                        : bitLength / 8f));

        byte[] result = new byte[bufLength];
        int offSet = Math.max(0, bufLength - valLength);

        for (int index = 0; index < bufLength; index++) {
            String byteStr = value.substring(index * 2, index * 2 + 2);
            result[index + offSet] = UnsignedBytes.parseUnsignedByte(byteStr, 16);
        }
        return result;
    }

    public static byte[] hexToU8a(String value) {
        return hexToU8a(value, -1);
    }


    /**
     * @name hexStripPrefix
     * @summary Strips any leading `0x` prefix.
     * @description Tests for the existence of a `0x` prefix, and returns the value without the prefix. Un-prefixed values are returned as-is.
     * @example <BR>
     * <p>
     * ```javascript
     * import { hexStripPrefix } from '@polkadot/util';
     * <p>
     * console.log('stripped', hexStripPrefix('0x1234')); // => 1234
     * ```
     */
    //export default function hexStripPrefix (value?: string | null): string {
    static String UNPREFIX_HEX_REGEX = "^[a-fA-F0-9]+$";

    public static String hexStripPrefix(String value) {
        if (value == null) {
            return "";
        }

        if (hexHasPrefix(value)) {
            return value.substring(2);
        }

        if (Pattern.matches(UNPREFIX_HEX_REGEX, value)) {
            return value;
        }

        throw new RuntimeException("Invalid hex " + value + " passed to hexStripPrefix");
    }

    /**
     * @name hexHasPrefix
     * @summary Tests for the existence of a `0x` prefix.
     * @description Checks for a valid hex input value and if the start matched `0x`
     * @example <BR>
     * <p>
     * ```javascript
     * import { hexHasPrefix } from '@polkadot/util';
     * <p>
     * console.log('has prefix', hexHasPrefix('0x1234')); // => true
     * ```
     */
    //export default function hexHasPrefix (value?: string | null): boolean {
    //    return !!(value && isHex(value, -1, true) && value.substr(0, 2) === '0x');
    //}
    public static boolean hexHasPrefix(String value) {
        if (value != null
                && isHex(value, -1, true)
                && value.substring(0, 2).equals("0x")) {
            return true;
        }
        return false;
    }


    /**
     * @name isU8a
     * @summary Tests for a `Uint8Array` object instance.
     * @description Checks to see if the input object is an instance of `Uint8Array`.
     * @example <BR>
     * <p>
     * ```javascript
     * import { isUint8Array } from '@polkadot/util';
     * <p>
     * console.log('isU8a', isU8a([])); // => false
     * ```
     */
    //export default function isU8a (value?: any): value is Uint8Array {
    public static boolean isU8a(Object value) {
        return value instanceof byte[]
                || value instanceof U8a;
    }


    /**
     * @param _value              The value to convert
     * @param _options            Options to pass while converting
     * @param _options.isLe       Convert using Little Endian
     * @param _options.isNegative Convert using two's complement
     * @name hexToBn
     * @summary Creates a BN.js bignumber object from a hex string.
     * @description `null` inputs returns a `BN(0)` result. Hex input values return the actual value converted to a BN. Anything that is not a hex string (including the `0x` prefix) throws an error.
     * @example <BR>
     * <p>
     * ```javascript
     * import { hexToBn } from '@polkadot/util';
     * <p>
     * hexToBn('0x123480001f'); // => BN(0x123480001f)
     * ```
     */
    //export default function hexToBn (value?: string | number | null, options: ToBnOptions | boolean = { isLe: false, isNegative: false }): BN {
    public static BigInteger hexToBn(Object value, boolean isLe, boolean isNegative) {
        if (value == null) {
            return BigInteger.ZERO;
        }

        String rawValue = hexStripPrefix((String) value);

        if (isLe) {
            //"12345678" --- "78563412"
            StringBuilder reverse = new StringBuilder(rawValue).reverse();
            for (int i = 0; i < reverse.length(); i += 2) {
                char c1 = reverse.charAt(i);
                char c2 = reverse.charAt(i + 1);

                reverse.setCharAt(i + 1, c1);
                reverse.setCharAt(i, c2);
            }
            rawValue = reverse.toString();
        }

        BigInteger bigInteger = BigInteger.ZERO;
        if (rawValue.length() > 0){
            bigInteger = new BigInteger(rawValue, 16);
        }
        //BigInteger bigInteger = new BigInteger(rawValue, 16);

        if (isNegative) {
            //TODO 2019-05-08 23:04
            throw new UnsupportedOperationException();
        }
        return bigInteger;

        // FIXME: Use BN's 3rd argument `isLe` once this issue is fixed
        // https://github.com/indutny/bn.js/issues/208
        //const bn = new BN((_options.isLe ? reverse(_value) : _value) || '00', 16);

        // fromTwos takes as parameter the number of bits, which is the hex length
        // multiplied by 4.
        //return _options.isNegative ? bn.fromTwos(_value.length * 4) : bn;
    }

    /**
     * @param value              The value to convert
     * @param options            Options to pass while converting
     * @param options.isLe       Convert using Little Endian
     * @param options.isNegative Convert using two's complement
     * @name u8aToBn
     * @summary Creates a BN from a Uint8Array object.
     * @description `UInt8Array` input values return the actual BN. `null` or `undefined` values returns an `0x0` value.
     * @example <BR>
     * <p>
     * ```javascript
     * import { u8aToBn } from '@polkadot/util';
     * <p>
     * u8aToHex(new Uint8Array([0x68, 0x65, 0x6c, 0x6c, 0xf])); // 0x68656c0f
     * ```
     */
    //export default function u8aToBn (value: Uint8Array, options: ToBnOptions | boolean = { isLe: true, isNegative: false }):
    public static BigInteger u8aToBn(byte[] value, boolean isLe, boolean isNegative) {
        return hexToBn(
                u8aToHex(value),
                isLe, isNegative
        );
    }


    /**
     * @name bnToBn
     * @summary Creates a BN value from a BN.js bignumber or number input.
     * @description `null` inputs returns a `0x0` result, BN values returns the value, numnbers returns a BN representation.
     * @example <BR>
     * <p>
     * ```javascript
     * import BN from 'bn.js';
     * import { bnToBn } from '@polkadot/util';
     * <p>
     * bnToBn(0x1234); // => BN(0x1234)
     * bnToBn(new BN(0x1234)); // => BN(0x1234)
     * ```
     */
    //export default function bnToBn (value?: BN | number | null): BN {
    public static BigInteger bnToBn(Object value) {
        if (value == null) {
            return BigInteger.ZERO;
        }

        if (value instanceof BigInteger) {
            return (BigInteger) value;
        } else if (value instanceof Number) {
            return new BigInteger(value.toString());
        } else if (value instanceof String) {
            return new BigInteger((String) value, 16);
        }

        throw new RuntimeException(" bnToBn " + value);
    }

    final static String ZERO_STR = "0x00";


    /**
     * @name bnToHex
     * @summary Creates a hex value from a BN.js bignumber object.
     * @description `null` inputs returns a `0x` result, BN values return the actual value as a `0x` prefixed hex value. Anything that is not a BN object throws an error. With `bitLength` set, it fixes the number to the specified length.
     * @example <BR>
     * <p>
     * ```javascript
     * import BN from 'bn.js';
     * import { bnToHex } from '@polkadot/util';
     * <p>
     * bnToHex(new BN(0x123456)); // => '0x123456'
     * ```
     */
    //export default function bnToHex (value?: BN | number | null, options: number | Options = { bitLength: -1, isLe: false, isNegative: false }): string {
    public static String bnToHex(BigInteger value, int bitLength) {
        return bnToHex(value, false, false, bitLength);
    }

    public static String bnToHex(BigInteger value, boolean isLe, boolean isNegtive, int bitLength) {
        /*
        *
  if (!value) {
    return ZERO_STR;
  }

  const _options = {
    isLe: false,
    isNegative: false,
    // Backwards-compatibility
    ...(isNumber(options) ? { bitLength: options } : options)
  };

  return u8aToHex(bnToU8a(value, _options));
        * */
        if (value == null) {
            return ZERO_STR;
        }

        return u8aToHex(bnToU8a(value, isLe, isNegtive, bitLength));
    }


    /**
     * @name bnToU8a
     * @summary Creates a Uint8Array object from a BN.
     * @description `null`/`undefined`/`NaN` inputs returns an empty `Uint8Array` result. `BN` input values return the actual bytes value converted to a `Uint8Array`. Optionally convert using little-endian format if `isLE` is set.
     * @example <BR>
     * <p>
     * ```javascript
     * import { bnToU8a } from '@polkadot/util';
     * <p>
     * bnToU8a(new BN(0x1234)); // => [0x12, 0x34]
     * ```
     */
    //export default function bnToU8a (value: BN | number | null, options?: Options): Uint8Array;
    //export default function bnToU8a (value: BN | number | null, bitLength?: number, isLe?: boolean): Uint8Array;
    //export default function bnToU8a (value: BN | number | null, arg1: number | Options = { bitLength: -1, isLe: true, isNegative: false },arg2?: boolean): Uint8Array {
    public static byte[] bnToU8a(BigInteger value, boolean isLe, int bitLength) {
        return bnToU8a(value, isLe, false, bitLength);
    }

    public static byte[] bnToU8a(BigInteger value, boolean isLe, boolean isNegative, int bitLength) {
        BigInteger valueBn = bnToBn(value);
        int byteLength;
        if (bitLength == -1) {
            byteLength = (int) Math.ceil(valueBn.bitLength() / 8f);
        } else {
            byteLength = (int) Math.ceil(bitLength / 8f);
        }

        if (value == null) {
            if (bitLength == -1) {
                return new byte[0];
            } else {
                return new byte[byteLength];
            }
        }

        byte[] output = new byte[byteLength];

        if (isNegative) {
            //TODO  valueBn.negate()
            //const bn = _options.isNegative ? valueBn.toTwos(byteLength * 8) : valueBn;
        }

        if (isLe) {
            byte[] bytes = toByteArrayLittleEndianUnsigned(valueBn);
            //arraycopy(Object src,  int  srcPos,
            //Object dest, int destPos,
            //int length);
            System.arraycopy(bytes, 0, output, 0, bytes.length);
        } else {
            //big-endian
            byte[] bytes = valueBn.toByteArray();
            System.arraycopy(bytes, 0, output, output.length - bytes.length, bytes.length);
        }
        //if (output.length != bytes.length) {
        //    throw new RuntimeException();
        //}

        return output;

    }

    public static void main(String[] argv) {
        System.out.println(System.currentTimeMillis());
        BigInteger bi = BigInteger.valueOf(1557849267933L);
        System.out.println(java.util.Arrays.toString((bi.toByteArray())));

        System.out.println(java.util.Arrays.toString(toByteArrayLittleEndianUnsigned(bi)));

        System.out.println(Arrays.toString(bnToU8a(bi, false, true, 64)));
        System.out.println(Arrays.toString(bnToU8a(bi, true, true, 64)));
    }


    public static byte[] toByteArrayLittleEndianUnsigned(BigInteger bi) {
        byte[] extractedBytes = toByteArrayUnsigned(bi);
        ArrayUtils.reverse(extractedBytes);
        //byte[] reversed = ByteUtils.reverseArray(extractedBytes);
        return extractedBytes;
    }

    public static byte[] toByteArrayUnsigned(BigInteger bi) {
        byte[] extractedBytes = bi.toByteArray();
        int skipped = 0;
        boolean skip = true;
        for (byte b : extractedBytes) {
            boolean signByte = b == (byte) 0x00;
            if (skip && signByte) {
                skipped++;
                continue;
            } else if (skip) {
                skip = false;
            }
        }
        extractedBytes = Arrays.copyOfRange(extractedBytes, skipped,
                extractedBytes.length);
        return extractedBytes;
    }


    /**
     * @name compactFromU8a
     * @description Retrievs the offset and encoded length from a compact-prefixed value
     * @example <BR>
     * <p>
     * ```javascript
     * import { compactFromU8a } from '@polkadot/util';
     * <p>
     * const [offset, length] = compactFromU8a(new Uint8Array([254, 255, 3, 0]), 32));
     * <p>
     * console.log('value offset=', offset, 'length=', length); // 4, 0xffff
     * ```
     */
    //export default function compactFromU8a (_input: Uint8Array | string, bitLength: BitLength = DEFAULT_BITLENGTH): [number, BN] {
    public static Pair<Integer, BigInteger> compactFromU8a(Object _input, int bitLength) {
          /*
        *   const input = u8aToU8a(_input);
  const flag = input[0] & 0b11;

  if (flag === 0b00) {
    return [1, new BN(input[0]).shrn(2)];
  } else if (flag === 0b01) {
    return [2, u8aToBn(input.slice(0, 2), true).shrn(2)];
  } else if (flag === 0b10) {
    return [4, u8aToBn(input.slice(0, 4), true).shrn(2)];
  }

  const length = new BN(input[0])
    .shrn(2) // clear flag
    .addn(4) // add 4 for base length
    .toNumber();
  const offset = 1 + length;

  return [offset, u8aToBn(input.subarray(1, offset), true)];
        * */
        byte[] input = u8aToU8a(_input);
        int flag;
        if (input.length == 0) {
            return Pair.of(1, new BigInteger("0").shiftRight(2));
        } else {
            flag = UnsignedBytes.toInt(input[0]) & 0b11;
        }

        if (flag == 0b00) {
            //shift right
            return Pair.of(1, new BigInteger(UnsignedBytes.toInt(input[0]) + "").shiftRight(2));
        } else if (flag == 0b01) {
            byte[] subarray = ArrayUtils.subarray(input, 0, 2);
            return Pair.of(2, u8aToBn(subarray, true, false).shiftRight(2));
        } else if (flag == 0b10) {
            byte[] subarray = ArrayUtils.subarray(input, 0, 4);
            return Pair.of(4, u8aToBn(subarray, true, false).shiftRight(2));
        }


        int length = BigInteger.valueOf(UnsignedBytes.toInt(input[0]))
                .shiftRight(2)
                .add(BigInteger.valueOf(4))
                .intValue();

        int offset = length + 1;
        return Pair.of(offset, u8aToBn(ArrayUtils.subarray(input, 1, offset), true, false));
    }

    public static Pair<Integer, BigInteger> compactFromU8a(Object input) {
        return compactFromU8a(input, 32);
    }

    /**
     * @name u8aToString
     * @summary Creates a utf-8 string from a Uint8Array object.
     * @description `UInt8Array` input values return the actual decoded utf-8 string. `null` or `undefined` values returns an empty string.
     * @example <BR>
     * <p>
     * ```javascript
     * import { u8aToString } from '@polkadot/util';
     * <p>
     * u8aToString(new Uint8Array([0x68, 0x65, 0x6c, 0x6c, 0x6f])); // hello
     * ```
     */
    //export default function u8aToString (value?: Uint8Array | null): string {
    public static String u8aToString(byte[] value) {
        if (value == null || value.length == 0) {
            return "";
        }


        StringBuilder sb = new StringBuilder();
        for (byte b : value) {
            char ch = (char) UnsignedBytes.toInt(b);
            sb.append(ch);
        }

        //TODO 2019-05-14 02:49 uint8

//  return decoder.decode(value);
        return new String(value);
        //return sb.toString();
    }


    static final String ALPHABET = "0123456789abcdef";

    /**
     * @name u8aToHex
     * @summary Creates a hex string from a Uint8Array object.
     * @description `UInt8Array` input values return the actual hex string. `null` or `undefined` values returns an `0x` string.
     * @example <BR>
     * <p>
     * ```javascript
     * import { u8aToHex } from '@polkadot/util';
     * <p>
     * u8aToHex(new Uint8Array([0x68, 0x65, 0x6c, 0x6c, 0xf])); // 0x68656c0f
     * ```
     */
    //export default function u8aToHex (value?: Uint8Array | null, bitLength: number = -1, isPrefixed: boolean = true): string {
    public static String u8aToHex(byte[] value, int bitLength, boolean isPrefixed) {
        String prefix = isPrefixed ? "0x" : "";

        if (ArrayUtils.isEmpty(value)) {
            return prefix;
        }

        int byteLength = (int) Math.ceil(bitLength / 8f);

        if (byteLength > 0 && value.length > byteLength) {
            int halfLength = (int) Math.ceil(byteLength / 2f);

            String left = u8aToHex(ArrayUtils.subarray(value, 0, halfLength), -1, isPrefixed);
            String right = u8aToHex(ArrayUtils.subarray(value, value.length - halfLength, value.length), -1, false);

            return left + "..." + right;
        }
        // based on comments in https://stackoverflow.com/questions/40031688/javascript-arraybuffer-to-hex and
        // implementation in http://jsben.ch/Vjx2V - optimisation here suggests that a forEach loop is faster
        // than reduce as well (clocking at in 90% of the reduce speed with tweaking in the playpen above)
        //return value.reduce((result, value) => {
        //    return result + ALPHABET[value >> 4] + ALPHABET[value & 15];
        //}, prefix);
        StringBuilder stringBuilder = new StringBuilder(prefix);

        for (byte b : value) {
            int ub = UnsignedBytes.toInt(b);
            stringBuilder.append(ALPHABET.charAt(ub >> 4)).append(ALPHABET.charAt(ub & 15));
        }
        return stringBuilder.toString();
    }

    public static String u8aToHex(byte[] value) {
        return u8aToHex(value, -1, true);
    }


    /**
     * @name stringToU8a
     * @summary Creates a Uint8Array object from a utf-8 string.
     * @description String input values return the actual encoded `UInt8Array`. `null` or `undefined` values returns an empty encoded array.
     * @example <BR>
     * <p>
     * ```javascript
     * import { stringToU8a } from '@polkadot/util';
     * <p>
     * stringToU8a('hello'); // [0x68, 0x65, 0x6c, 0x6c, 0x6f]
     * ```
     */
    //export default function stringToU8a (value?: string): Uint8Array {
    public static byte[] stringToU8a(String value) {
        if (StringUtils.isEmpty(value)) {
            return new byte[0];
        }

        //TODO 2019-05-09 00:48 test
        return value.getBytes();
    }


    /**
     * @name compactAddLength
     * @description Adds a length prefix to the input value
     * @example <BR>
     * <p>
     * ```javascript
     * import { compactAddLength } from '@polkadot/util';
     * <p>
     * console.log(compactAddLength(new Uint8Array([0xde, 0xad, 0xbe, 0xef]))); // Uint8Array([4 << 2, 0xde, 0xad, 0xbe, 0xef])
     * ```
     */
    //export default function compactAddLength (input: Uint8Array): Uint8Array {
    public static byte[] compactAddLength(byte[] input) {
        return u8aConcat(Lists.newArrayList(
                compactToU8a(input.length),
                input)
        );
    }


    final static BigInteger MAX_U8 = BigInteger.valueOf(2).pow(8 - 2).subtract(BigInteger.ONE);
    final static BigInteger MAX_U16 = BigInteger.valueOf(2).pow(16 - 2).subtract(BigInteger.ONE);
    final static BigInteger MAX_U32 = BigInteger.valueOf(2).pow(32 - 2).subtract(BigInteger.ONE);
//const MAX_U8 = new BN(2).pow(new BN(8 - 2)).subn(1);
//const MAX_U16 = new BN(2).pow(new BN(16 - 2)).subn(1);
//const MAX_U32 = new BN(2).pow(new BN(32 - 2)).subn(1);

    /**
     * @name compactToU8a
     * @description Encodes a number into a compact representation
     * @example <BR>
     * <p>
     * ```javascript
     * import { compactToU8a } from '@polkadot/util';
     * <p>
     * console.log(compactToU8a(511, 32)); // Uint8Array([0b11111101, 0b00000111])
     * ```
     */
    //export default function compactToU8a (_value: BN | number): Uint8Array {
    public static byte[] compactToU8a(Object _value) {
        BigInteger value = bnToBn(_value);

        if (value.compareTo(MAX_U8) <= 0) {
            return new byte[]{UnsignedBytes.parseUnsignedByte((value.intValue() << 2) + "")};
        } else if (value.compareTo(MAX_U16) <= 0) {
            return bnToU8a(value.shiftLeft(2).add(BigInteger.valueOf(0b01)), true, false, 16);
        } else if (value.compareTo(MAX_U32) <= 0) {
            return bnToU8a(value.shiftLeft(2).add(BigInteger.valueOf(0b10)), true, false, 32);
        }

        byte[] u8a = bnToU8a(value, true, false, -1);
        int length = u8a.length;

        while (u8a[length - 1] == 0) {
            length--;
        }

        assert length >= 4 : "Previous tests match anyting less than 2^30; qed";

        return u8aConcat(Lists.newArrayList(
                // substract 4 as minimum (also catered for in decoding)
                new byte[]{UnsignedBytes.parseUnsignedByte((((length - 4) << 2) + 0b11) + "")},
                ArrayUtils.subarray(u8a, 0, length)
        ));
    }

    /**
     * @name u8aConcat
     * @summary Creates a concatenated Uint8Array from the inputs.
     * @description Concatenates the input arrays into a single `UInt8Array`.
     * @example <BR>
     * <p>
     * ```javascript
     * import { u8aConcat } from '@polkadot/util';
     * <p>
     * u8aConcat(
     * new Uint8Array([1, 2, 3]),
     * new Uint8Array([4, 5, 6])
     * ); // [1, 2, 3, 4, 5, 6]
     * ```
     */
    //export default function u8aConcat (..._list: Array<Uint8Array | string>): Uint8Array {
    public static byte[] u8aConcat(List<byte[]> _list) {
        List<byte[]> list = _list.stream().map(e -> u8aToU8a(e)).collect(Collectors.toList());

        int length = list.stream().mapToInt(e -> e.length).sum();
        byte[] result = new byte[length];
        int offset = 0;

        for (byte[] bytes : list) {
            System.arraycopy(bytes, 0, result, offset, bytes.length);
            offset += bytes.length;
        }
        return result;
    }


    /**
     * @name u8aToU8a
     * @summary Creates a Uint8Array value from a Uint8Array, Buffer, string or hex input.
     * @description `null` ior `undefined` nputs returns a `[]` result, Uint8Array values returns the value, hex strings returns a Uint8Array representation.
     * @example <BR>
     * <p>
     * ```javascript
     * import { u8aToU8a } from '@polkadot/util';
     * <p>
     * u8aToU8a(new Uint8Array([0x12, 0x34]); // => Uint8Array([0x12, 0x34])
     * u8aToU8a(0x1234); // => Uint8Array([0x12, 0x34])
     * ```
     */
    //export default function u8aToU8a (value?: Array<number> | Buffer | Uint8Array | string | null): Uint8Array {
    public static byte[] u8aToU8a(Object value) {
        if (value == null) {
            return new byte[0];
        }

        //if (isBuffer(value)) {
        //    return bufferToU8a(value);
        //}

        if (value instanceof String) {
            String strValue = (String) value;
            return isHex(strValue)
                    ? hexToU8a(strValue)
                    : stringToU8a(strValue);
        }

        if (value instanceof byte[]) {
            return (byte[]) value;
        }

        if (value instanceof U8a) {
            return ((U8a) value).raw;
        }

        if (value.getClass().isArray()) {
            List<Object> objects = CodecUtils.arrayLikeToList(value);
            byte[] result = new byte[objects.size()];
            for (int i = 0; i < objects.size(); i++) {
                Number number = (Number) objects.get(i);
                result[i] = UnsignedBytes.parseUnsignedByte(number.toString());
            }
            return result;
        }

        return (byte[]) value;
    }


    /**
     * @name stringLowerFirst
     * @summary Lowercase the first letter of a string
     * @description Lowercase the first letter of a string
     * @example <BR>
     * <p>
     * ```javascript
     * import { stringLowerFirst } from '@polkadot/util';
     * <p>
     * stringLowerFirst('ABC'); // => 'aBC'
     * ```
     */
    //export default function stringLowerFirst (value?: string | null): string {
    public static String stringLowerFirst(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }

        return value.substring(0, 1).toLowerCase() + value.substring(1);
    }

    public static boolean isContainer(Object object) {
        if (object instanceof ExtrinsicSignature) {
            return false;
        }


        if (object instanceof Collection
                || object instanceof Map) {
            return true;
        }

        if (object instanceof Option
                || object instanceof Compact) {
            return true;
        }
        if (object.getClass().isArray()) {
            Class<?> componentType = object.getClass().getComponentType();
            if (componentType.isPrimitive()) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    //public static String substr(String str, int startIndex, int length) {
    //    return str.substring()
    //}

    public static BigInteger toBn(Object value) {
        if (value instanceof Number) {
            return BigInteger.valueOf(((Number) value).longValue());
        }

        return new BigInteger(value.toString());
    }

    public static String stringCamelCase(String input) {
        return CaseUtils.toCamelCase(input, false, '-', '_', ' ');
    }


    /**
     * @name u8aFixLength
     * @summary Shifts a Uint8Array to a specific bitLength
     * @description Returns a uint8Array with the specified number of bits contained in the return value. (If bitLength is -1, length checking is not done). Values with more bits are trimmed to the specified length.
     * @example <BR>
     * <p>
     * ```javascript
     * import { u8aFixLength } from '@polkadot/util';
     * <p>
     * u8aFixLength('0x12') // => 0x12
     * u8aFixLength('0x12', 16) // => 0x0012
     * u8aFixLength('0x1234', 8) // => 0x12
     * ```
     */
    //  export default function u8aFixLength (value: Uint8Array, bitLength: number = -1, atStart: boolean = false): Uint8Array {
    public static byte[] u8aFixLength(byte[] value, int bitLength, boolean atStart) {
        int byteLength = (int) Math.ceil(bitLength / 8f);

        if (bitLength == -1 || value.length == byteLength) {
            return value;
        }

        if (value.length > byteLength) {
            return ArrayUtils.subarray(value, 0, byteLength);
        }

        byte[] result = new byte[byteLength];

        if (atStart) {
            System.arraycopy(value, 0, result, 0, value.length);
        } else {
            System.arraycopy(value, 0, result, byteLength - value.length, value.length);
        }
        return result;
    }

    public static boolean u8aStrEq(byte[] u8a1, byte[] u8a2) {
        return u8a1 != null
                && u8a2 != null
                && Arrays.toString(u8a1).equals(Arrays.toString(u8a2));
    }


    /**
     * @name randomAsU8a
     * @summary Creates a Uint8Array filled with random bytes.
     * @description Returns a `Uint8Array` with the specified (optional) length filled with random bytes.
     * @example <BR>
     * <p>
     * ```javascript
     * import { randomAsU8a } from '@polkadot/util-crypto';
     * <p>
     * randomAsU8a(); // => Uint8Array([...])
     * ```
     */
    //export default function randomAsU8a (length: number = 32): Uint8Array {
    //    return nacl.randomBytes(length);
    //}
    public static byte[] randomAsU8a() {
        return randomAsU8a(32);
    }


    public static byte[] randomAsU8a(int length) {
//TODO 2019-05-20 08:58 nacl.randomBytes(length);
        throw new UnsupportedOperationException();
    }


    /**
     * @name compactStripLength
     * @description Removes the length prefix, returning both the total length (including the value + compact encoding) and the decoded value with the correct length
     * @example <BR>
     * <p>
     * ```javascript
     * import { compactStripLength } from '@polkadot/util';
     * <p>
     * console.log(compactStripLength(new Uint8Array([2 << 2, 0xde, 0xad]))); // [2, Uint8Array[0xde, 0xad]]
     * ```
     */
    //  export default function compactStripLength (input: Uint8Array, bitLength: BitLength = DEFAULT_BITLENGTH): [number, Uint8Array] {
    public static Pair<Integer, byte[]> compactStripLength(byte[] input, int bitLength) {
        Pair<Integer, BigInteger> pair = compactFromU8a(input, bitLength);
        Integer offset = pair.getLeft();
        int length = pair.getRight().intValue();
        int total = offset + length;
        return Pair.of(total, ArrayUtils.subarray(input, offset, total));
    }

    public static Pair<Integer, byte[]> compactStripLength(byte[] input) {
        return compactStripLength(input, 32);
    }
}
