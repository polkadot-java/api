package org.polkadot.types.type;

import org.apache.commons.lang3.ArrayUtils;
import org.polkadot.types.codec.U8a;
import org.polkadot.utils.Utils;

/**
 * @name ExtrinsicEra
 * @description The era for an extrinsic, indicating either a mortal or immortal extrinsic
 */
public class ExtrinsicEra extends U8a {
    //constructor (value?: AnyU8a) {
    public ExtrinsicEra(Object value) {
        super(decodeExtrinsicEra(value));
    }

    static byte[] decodeExtrinsicEra(Object value) {
        if (value != null) {
            byte[] u8a = Utils.u8aToU8a(value);

            // If we have a zero byte, it is immortal (1 byte in length), otherwise we have
            // the era details following as another byte
            return ArrayUtils.subarray(u8a, 0, (u8a[0] == 0) ? 1 : 2);
        }

        return new byte[0];
    }
}
