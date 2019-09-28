package org.polkadot.types.primitive.generic;

import org.polkadot.types.Types;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.primitive.extrinsic.Extrinsics;
import org.polkadot.types.type.Hash;
import org.polkadot.types.type.Header;
import org.polkadot.utils.UtilsCrypto;

import java.util.List;

/**
 * A block encoded with header and extrinsics
 */
public class Block extends Struct {
    public static class BlockValue {
        List<byte[]> extrinsics;
        Header.HeaderValue header;

        public Header.HeaderValue getHeader() {
            return header;
        }

        public void setHeader(Header.HeaderValue header) {
            this.header = header;
        }

        public List<byte[]> getExtrinsics() {
            return extrinsics;
        }

        public void setExtrinsics(List<byte[]> extrinsics) {
            this.extrinsics = extrinsics;
        }
    }

    //  constructor (value?: BlockValue | Uint8Array) {
    public Block(Object value) {
        super(new Types.ConstructorDef()
                        .add("head", Header.class)
                        .add("extrinsics", Extrinsics.class)
                , value);
    }


    /**
     * Encodes a content {@link org.polkadot.types.type.Hash} for the block
     */
    public Hash getcontentHash() {
        return new Hash(UtilsCrypto.blake2AsU8a(this.toU8a(), 256));
    }

    /**
     * The Extrinsics contained in the block
     */
    public Extrinsics getExtrinsics() {
        return this.getField("extrinsics");
    }


    /**
     * Block/header {@link org.polkadot.types.type.Hash}
     */
    public Hash getHash() {
        return this.getHeader().getHash();
    }

    /**
     * The {@link org.polkadot.types.type.Header} of the block
     */
    public Header getHeader() {
        return this.getField("header");
    }

}
