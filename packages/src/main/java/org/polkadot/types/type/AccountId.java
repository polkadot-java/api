package org.polkadot.types.type;

import org.polkadot.common.keyring.address.AddressCodec;
import org.polkadot.types.codec.U8a;
import org.polkadot.types.codec.U8aFixed;
import org.polkadot.utils.Utils;

/**
 * A wrapper around an AccountId/PublicKey representation. Since we are dealing with
 * underlying PublicKeys (32 bytes in length), we extend from U8aFixed which is
 * just a Uint8Array wrapper with a fixed length.
 */
public class AccountId extends U8aFixed {
    //constructor (value: AnyU8a = new Uint8Array()) {
    public AccountId(Object value) {
        super(decodeAccountId(value), 256);
    }


    static String encode(U8a value) {
        //TODO 2019-05-10 03:30 decodeAddress, encodeAddress } from '@polkadot/keyring';
        return AddressCodec.encodeAddress(value.toU8a());
        //return new String(value.toU8a());
    }

    //private static decodeAccountId (value: AnyU8a | AnyString): Uint8Array {
    private static byte[] decodeAccountId(Object value) {
        if (Utils.isU8a(value) || value.getClass().isArray()) {
            return Utils.u8aToU8a(value);
        } else if (Utils.isHex(value)) {
            return Utils.hexToU8a(value.toString());
        } else if (value instanceof String) {
            //TODO 2019-05-10 03:30 decodeAddress, encodeAddress } from '@polkadot/keyring';
            return AddressCodec.decodeAddress((String) value);
        }
        return (byte[]) value;
    }


    /**
     * Compares the value of the input to see if there is a match
     */
    @Override
    public boolean eq(Object other) {
        return super.eq(AccountId.decodeAccountId(other));
    }

    /**
     * Converts the Object to JSON, typically used for RPC transfers
     */
    @Override
    public Object toJson() {
        return this.toString();
    }

    /**
     * Returns the string representation of the value
     */
    @Override
    public String toString() {
        return AccountId.encode(this);
    }

    /**
     * The Substrate AccountIdOf representation as a AccountId.
     */
    public static class AccountIdOf extends AccountId {
        public AccountIdOf(Object value) {
            super(value);
        }
    }

    /**
     * @description Returns the base runtime type name for this instance
     */
    @Override
    public String toRawType() {
        return "AccountId";
    }
}
