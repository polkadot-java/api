package org.polkadot.types.metadata;


import com.google.common.collect.Lists;
import org.polkadot.utils.Utils;

import static org.polkadot.types.metadata.MagicNumber.MAGIC_NUMBER;

/**
 * @name Metadata
 * @description The versioned runtime metadata as a decoded structure
 */
public class Metadata extends MetadataVersioned {
    //  constructor (value?: Uint8Array | string) {
    public Metadata(Object value) {
        super(decodeMetadata(value));
    }

    // first we try and parse using the versioned structure, if this does fail,
    // we adjust with the magic number and a manual version and re-try. As soon as
    // we remove support for V0, we will just do a new here
    private static MetadataVersioned decodeMetadata(Object _value) {
        byte[] value = Utils.isHex(_value)
                ? Utils.hexToU8a((String) _value)
                : (byte[]) _value;

        try {
            return new MetadataVersioned(value);
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals(MagicNumber.MAGIC_ERROR + "")) {
                throw e;
            }
        }


        return new MetadataVersioned(
                Utils.u8aConcat(Lists.newArrayList(
                        MAGIC_NUMBER.toU8a(), // manually add the magic number
                        new byte[]{0}, // add the version for the original
                        value // the actual data as retrieved
                ))
        );
    }
}
