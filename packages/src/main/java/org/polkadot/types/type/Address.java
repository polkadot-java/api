package org.polkadot.types.type;

import com.google.common.collect.Lists;
import com.google.common.primitives.UnsignedBytes;
import org.apache.commons.lang3.ArrayUtils;
import org.polkadot.common.keyring.address.AddressUtils;
import org.polkadot.types.Codec;
import org.polkadot.types.codec.Base;
import org.polkadot.utils.Utils;


/**
 * @name Address
 * @description A wrapper around an AccountId and/or AccountIndex that is encoded with a prefix.
 * Since we are dealing with underlying publicKeys (or shorter encoded addresses),
 * we extend from Base with an AccountId/AccountIndex wrapper. Basically the Address
 * is encoded as `[ <prefix-byte>, ...publicKey/...bytes ]` as per spec
 */
//export default class Address extends Base<AccountId | AccountIndex> implements Codec {
public class Address extends Base<Codec> implements Codec {
    //export const ACCOUNT_ID_PREFIX = new Uint8Array([0xff]);
    //public static final int[] ACCOUNT_ID_PREFIX = new int[]{0xff};
    public static final byte[] ACCOUNT_ID_PREFIX = new byte[]{UnsignedBytes.parseUnsignedByte("0xff")};

    //type AnyAddress = BN | Address | AccountId | AccountIndex | Array<number> | Uint8Array | number | string;
    //  constructor (value: AnyAddress = new Uint8Array()) {
    public Address(Object value) {
        super(decodeAddress(value));
    }

    //  static decodeAddress (value: AnyAddress): AccountId | AccountIndex {
    static Codec decodeAddress(Object value) {
        if (value instanceof AccountId || value instanceof AccountIndex) {
            return (Codec) value;
        } else if (value instanceof Number) {
            return new AccountIndex(value);
        } else if (value instanceof Address) {
            return ((Address) value).raw;
        } else if (value instanceof byte[]) {
            byte[] bytes = (byte[]) value;
            // This allows us to instantiate an address with a raw publicKey. Do this first before
            // we checking the first byte, otherwise we may split an already-existent valid address
            if (bytes.length == 32) {
                return new AccountId(value);
            } else if (UnsignedBytes.toInt(bytes[0]) == 0xff) {
                return new AccountId(ArrayUtils.subarray(bytes, 1, bytes.length));
            }

            int[] results = AccountIndex.readLength(bytes);
            int offset = results[0];
            int length = results[1];
            return new AccountIndex(Utils.u8aToBn(ArrayUtils.subarray(bytes, offset, offset + length), true, false));
        } else if (value.getClass().isArray()) {
            return Address.decodeAddress(Utils.u8aToU8a(value));
        } else if (Utils.isHex(value)) {
            return decodeAddress(Utils.hexToU8a((String) value));
        }

        byte[] decoded = AddressUtils.decodeAddress((byte[]) value);
        return decoded.length == 32
                ? new AccountId(decoded)
                : new AccountIndex(Utils.u8aToBn(decoded, true, false));
    }


    /**
     * @description The length of the raw value, either AccountIndex or AccountId
     */
    public int getRawLength() {
        return this.raw instanceof AccountIndex
                ? AccountIndex.calcLength((AccountIndex) this.raw)
                : this.raw.getEncodedLength();
    }

    @Override
    public int getEncodedLength() {
        int rawLength = this.getRawLength();

        return rawLength + (
                // for 1 byte AccountIndexes, we are not adding a specific prefix
                rawLength > 1
                        ? 1
                        : 0
        );
    }

    /**
     * @description Checks if the value is an empty value
     */
    @Override
    public boolean isEmpty() {
        return this.raw.isEmpty();
    }

    @Override
    public boolean eq(Object other) {
        return this.raw.eq(other);
    }

    @Override
    public String toHex() {
        return Utils.u8aToHex(this.toU8a());
    }

    @Override
    public Object toJson() {
        return this.raw.toJson();
    }


    @Override
    public String toString() {
        return this.raw.toString();
    }

    @Override
    public byte[] toU8a(boolean isBare) {
        byte[] encoded = this.raw.toU8a();
        encoded = ArrayUtils.subarray(encoded, 0, this.getRawLength());
        return isBare
                ? encoded
                : Utils.u8aConcat(Lists.newArrayList(
                this.raw instanceof AccountIndex
                        ? AccountIndex.writeLength(encoded)
                        : ACCOUNT_ID_PREFIX,
                encoded
        ));
    }
}
