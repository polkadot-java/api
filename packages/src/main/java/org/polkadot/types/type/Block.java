package org.polkadot.types.type;

import org.polkadot.types.Types;
import org.polkadot.types.codec.Struct;
import org.polkadot.utils.CryptoUtils;

import java.util.List;

/**
 * @name Block
 * @description A block encoded with header and extrinsics
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
     * @description Encodes a content [[Hash]] for the block
     */
    public Hash getcontentHash() {
        return new Hash(CryptoUtils.blake2AsU8a(this.toU8a(), 256));
    }

    /**
     * @description The [[Extrinsics]] contained in the block
     */
    public Extrinsics getExtrinsics() {
        return this.getField("extrinsics");
    }


    /**
     * @description Block/header [[Hash]]
     */
    public Hash getHash() {
        return this.getHeader().getHash();
    }

    /**
     * @description The [[Header]] of the block
     */
    public Header getHeader() {
        return this.getField("header");
    }

}
