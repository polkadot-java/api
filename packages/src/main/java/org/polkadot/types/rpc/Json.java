package org.polkadot.types.rpc;

import com.alibaba.fastjson.JSONObject;
import org.polkadot.types.Codec;
import org.polkadot.types.codec.CodecUtils;

import java.util.Map;


/**
 * Wraps the a JSON structure retrieve via RPC. It extends the standard JS Map with. While it
 * implements a Codec, it is limited in that it can only be used with input objects via RPC,
 * i.e. no hex decoding. Unlike a struct, this waps a JSON object with unknown keys
 * @noInheritDoc
 */
public class Json extends JSONObject implements Codec {

    //  constructor (value?: { [index: string]: any } | null) {
    public Json(Object value) {
        super(decodeJson(value));
    }


    static Map<String, Object> decodeJson(Object value) {
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        //TODO 2019-05-13 09:43
        throw new UnsupportedOperationException(" illegal type " + value);
    }

    /**
     * Always 0, never encodes as a Uint8Array
     */
    @Override
    public int getEncodedLength() {
        return 0;
    }

    /**
     * Checks if the value is an empty value
     */
    @Override
    public boolean isEmpty() {
        //    return [...this.keys()].length === 0;
        return super.isEmpty();
    }

    /**
     * Compares the value of the input to see if there is a match
     */
    @Override
    public boolean eq(Object other) {
        return CodecUtils.compareMap(this, other);
    }

    /**
     * Unimplemented, will throw
     */
    @Override
    public String toHex() {
        throw new UnsupportedOperationException();
    }

    /**
     * Converts the Object to JSON, typically used for RPC transfers
     */
    @Override
    public Object toJson() {
        return this.toJSONString();
    }

    /**
     * Unimplemented, will throw
     */
    @Override
    public byte[] toU8a(boolean isBare) {
        throw new UnsupportedOperationException();
    }
}
