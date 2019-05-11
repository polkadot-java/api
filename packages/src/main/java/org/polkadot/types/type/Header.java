package org.polkadot.types.type;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Compact;
import org.polkadot.types.codec.Struct;

/**
 * @name Header
 * @description A [[Block]] header
 */
public class Header extends Struct {


    static class HeaderValue {
        Digest digest;
        byte[] extrinsicsRoot;
        int number;
        byte[] parentHash;
        byte[] stateRoot;
    }

    //HeaderValue =
    //{
    //    digest ?:Digest | {logs:DigestItem[] },
    //    extrinsicsRoot ?:AnyU8a,
    //        number ?:AnyNumber,
    //        parentHash ?:AnyU8a,
    //        stateRoot ?:AnyU8a
    //}


    //  constructor (value?: HeaderValue | Uint8Array | null) {
    public Header(Object value) {
        super(new Types.ConstructorDef()
                        .add("parentHash", Hash.class)
                        .add("number", Compact.with(TypesUtils.getConstructorCodec(BlockNumber.class)))
                        .add("stateRoot", Hash.class)
                        .add("extrinsicsRoot", Hash.class)
                        .add("digest", Digest.class)
                , value == null ? new Object() : value);

    }


    /**
     * @description The wrapped [[BlockNumber]]
     */
    public BlockNumber getBlockNumber() {
        return (BlockNumber) ((Compact) this.getField("number")).toBn();
    }


    /**
     * @description The wrapped [[Digest]]
     */
    public Digest getDigest() {
        return this.getField("digest");
    }

    /**
     * @description The wrapped extrisics root as a [[Hash]]
     */
    public Hash getExtrinsicsRoot() {
        return this.getField("extrinsicsRoot");
    }

    /**
     * @description Convenience method, encodes the header and calculates the [[Hash]]
     */
    public Hash getHash() {
        //import { blake2AsU8a } from '@polkadot/util-crypto';
//TODO 2019-05-10 03:56
        throw new UnsupportedOperationException();
        //return new Hash(
        //blake2AsU8a(this.toU8a(), 256)
        //);
    }

    /**
     * @description Alias for `blockNumber` (this is displayed in JSON)
     */
    public BlockNumber getNumber() {
        return this.getBlockNumber();
    }

    /**
     * @description The wrapped parent as a [[Hash]]
     */
    public Hash getParentHash() {
        return this.getField("parentHash");
    }

    /**
     * @description The wrapped state root as a [[Hash]]
     */

    public Hash getStateRoot() {
        return this.getField("stateRoot");
    }
}