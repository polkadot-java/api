package org.polkadot.types.primitive;


import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.polkadot.types.codec.U8a;
import org.polkadot.utils.Utils;

import java.math.BigInteger;

/**
 * A Bytes wrapper for Vec<u8>. The significant difference between this and a normal Uint8Array
 * is that this version allows for length-encoding. (i.e. it is a variable-item codec, the same
 * as what is found in {@link org.polkadot.types.primitive.Text} and {@link org.polkadot.types.codec.Vector})
 */
public class Bytes extends U8a {
    //constructor (value?: AnyU8a) {
    public Bytes(Object value) {
        super(decodeBytes(value));
    }

    // Uint8Array | Array<number> | string;
    private static byte[] decodeBytes(Object value) {

        // FIXME Cyclic dependency, however needed for the StoreageData check below. In a perfect
        // world, we should probably be checking Bytes - however as a first step, check against
        // StorageData to cater for the _specific_ problematic case
        //    const StorageData = require('./StorageData').default;

        if (value instanceof StorageData) {
            // Here we cater for the actual StorageData that _could_ have a length prefix. In the
            // case of `:code` it is not added, for others it is
            //      const u8a = value as Uint8Array;
            StorageData u8a = (StorageData) value;
            byte[] bytes = u8a.toU8a();
            Pair<Integer, BigInteger> pair = Utils.compactFromU8a(bytes);

            return bytes.length == pair.getValue().add(new BigInteger(pair.getKey().toString())).intValue()
                    ? u8a.subarray(pair.getKey(), u8a.length()).toU8a()
                    : u8a.toU8a();


            //} else if (value.getClass().isArray() || value instanceof String) {
        } else if (value instanceof String) {
            byte[] u8a = Utils.u8aToU8a(value);
            return Bytes.decodeBytes(Utils.compactAddLength(u8a));
        } else if (value instanceof U8a) {
            // This is required. In the case of a U8a we already have gotten rid of the length,
            // i.e. new Bytes(new Bytes(...)) will work as expected TODO
            return ((U8a) value).toU8a();
        } else if (Utils.isU8a(value)) {
            // handle all other Uint8Array inputs, these do have a length prefix
            //const [offset, length] = Compact.decodeU8a(value);
            //return value.subarray(offset, offset + length.toNumber());

            Pair<Integer, BigInteger> pair = Utils.compactFromU8a(value);
            int offset = pair.getKey();
            int length = pair.getValue().intValue();
            return ArrayUtils.subarray((byte[]) value, offset, offset + length);
        }
        return (byte[]) value;
    }


    /**
     * The length of the value when encoded as a Uint8Array
     */
    @Override
    public int getEncodedLength() {
        return this.length() + Utils.compactToU8a(this.length()).length;
    }


    /**
     * @param isBare true when the value has none of the type-specific prefixes (internal)
     *               Encodes the value as a Uint8Array as per the parity-codec specifications
     */
    @Override
    public byte[] toU8a(boolean isBare) {
        return isBare
                ? super.toU8a(isBare)
                : Utils.compactAddLength(this.raw);
    }

    /**
     * @description Returns the base runtime type name for this instance
     */
    @Override
    public String toRawType() {
        return "Bytes";
    }

}
