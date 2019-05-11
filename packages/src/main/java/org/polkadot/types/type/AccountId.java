package org.polkadot.types.type;

import org.polkadot.types.codec.U8a;
import org.polkadot.types.codec.U8aFixed;
import org.polkadot.utils.Utils;

/**
 * @name AccountId
 * @description A wrapper around an AccountId/PublicKey representation. Since we are dealing with
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
        //return encodeAddress(value);
        return new String(value.toU8a());
    }

    //private static decodeAccountId (value: AnyU8a | AnyString): Uint8Array {
    private static byte[] decodeAccountId(Object value) {
        if (Utils.isU8a(value) || value.getClass().isArray()) {
            return Utils.u8aToU8a(value);
        } else if (Utils.isHex(value)) {
            return Utils.hexToU8a(value.toString());
        } else if (value instanceof String) {
            //TODO 2019-05-10 03:30 decodeAddress, encodeAddress } from '@polkadot/keyring';
            //return decodeAddress((value as String).toString());
        }
        return (byte[]) value;
    }


    @Override
    public boolean eq(Object other) {
        return super.eq(AccountId.decodeAccountId(other));
    }

    @Override
    public Object toJson() {
        return this.toString();
    }

    @Override
    public String toString() {
        return AccountId.encode(this);
    }

    /**
     * @name AccountIdOf
     * @description The Substrate AccountIdOf representation as a [[AccountId]].
     */
    public static class AccountIdOf extends AccountId {
        public AccountIdOf(Object value) {
            super(value);
        }
    }
}
