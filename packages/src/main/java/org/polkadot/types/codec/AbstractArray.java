package org.polkadot.types.codec;

import com.alibaba.fastjson.JSONArray;
import org.polkadot.types.Codec;
import org.polkadot.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.polkadot.utils.Utils.compactToU8a;

/**
 * @name AbstractArray
 * @description This manages codec arrays. It is an extension to Array, providing
 * specific encoding/decoding on top of the base type.
 * @noInheritDoc
 */
public class AbstractArray<T extends Codec> extends ArrayList<T> implements Codec {

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    public int length() {
        return this.size();
    }

    @Override
    public int getEncodedLength() {
        int total = 0;
        for (T t : this) {
            total += t.getEncodedLength();
        }
        total += compactToU8a(this.size()).length;
        return total;
    }

    @Override
    public boolean eq(Object other) {
        //TODO 2019-05-07 19:13     return compareArray(this, other);
        return super.equals(other);
    }

    @Override
    public String toHex() {
        return Utils.u8aToHex(this.toU8a(false));
    }

    @Override
    public Object toJson() {
        return JSONArray.toJSON(this);
    }

    @Override
    public byte[] toU8a(boolean isBare) {
        List<byte[]> encoded = this.stream().map(e -> e.toU8a(isBare)).collect(Collectors.toList());
        if (isBare) {
            encoded.add(0, Utils.compactToU8a(this.length()));
        }
        return Utils.u8aConcat(encoded);
    }


    // Below are methods that we override. When we do a `new Vector(...).map()`,
    // we want it to return an Array. We only override the methods that return a
    // new instance.

    /**
     * @description Filters the array with the callback
     * @param callbackfn The filter function
     * @param thisArg The `this` object to apply the result to
     */
    //filter (callbackfn: (value: T, index: number, array: Array<T>) => any, thisArg?: any): Array<T> {
    //    return this.toArray().filter(callbackfn, thisArg);
    //}

    /**
     * @description Maps the array with the callback
     * @param callbackfn The mapping function
     * @param thisArg The `this` onject to apply the result to
     */
    //map<U> (callbackfn: (value: T, index: number, array: Array<T>) => U, thisArg?: any): Array<U> {
    //      return this.toArray().map(callbackfn, thisArg);
    //  }


    public <T> T getFiled(int index) {
        return (T) get(index);
    }
 }
