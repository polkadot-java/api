package org.polkadot.types.codec;

import com.alibaba.fastjson.JSONArray;
import org.polkadot.types.Codec;
import org.polkadot.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.polkadot.utils.Utils.compactToU8a;

/**
 * AbstractArray
 * This manages codec arrays. It is an extension to Array, providing
 * specific encoding/decoding on top of the base type.
 * @noInheritDoc
 */
public class AbstractArray<T extends Codec> extends ArrayList<T> implements Codec {

	/**
	* Checks if the value is an empty value
	*/
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

	/**
	* The length of the value
	*/
    public int length() {
        return this.size();
    }

	/**
	* The length of the value when encoded as a Uint8Array
	*/
    @Override
    public int getEncodedLength() {
        int total = 0;
        for (T t : this) {
            total += t.getEncodedLength();
        }
        total += compactToU8a(this.size()).length;
        return total;
    }

	/**
	* Compares the value of the input to see if there is a match
	*/
    @Override
    public boolean eq(Object other) {
        //TODO 2019-05-07 19:13     return compareArray(this, other);
        return CodecUtils.compareArray(this, other);
    }

	/**
	* Returns a hex string representation of the value
	*/
    @Override
    public String toHex() {
        return Utils.u8aToHex(this.toU8a());
    }

	/**
	* Converts the Object to JSON, typically used for RPC transfers
	*/
    @Override
    public Object toJson() {
        List<Object> collect = this.stream().map(e ->
                e.toJson()
        ).collect(Collectors.toList());
        return JSONArray.toJSON(collect);
    }

	/**
	* Encodes the value as a Uint8Array as per the parity-codec specifications
	* @param isBare true when the value has none of the type-specific prefixes (internal)
	*/
    @Override
    public byte[] toU8a(boolean isBare) {
        List<byte[]> encoded = this.stream().map(e -> e.toU8a(isBare)).collect(Collectors.toList());
        if (!isBare) {
            encoded.add(0, Utils.compactToU8a(this.length()));
        }
        return Utils.u8aConcat(encoded);
    }


    // Below are methods that we override. When we do a `new Vector(...).map()`,
    // we want it to return an Array. We only override the methods that return a
    // new instance.

    /**
     * Filters the array with the callback
     * @param callbackfn The filter function
     * @param thisArg The `this` object to apply the result to
     */
    //filter (callbackfn: (value: T, index: number, array: Array<T>) => any, thisArg?: any): Array<T> {
    //    return this.toArray().filter(callbackfn, thisArg);
    //}

    /**
     * @param callbackfn The mapping function
     * @param thisArg    The `this` onject to apply the result to
     * Maps the array with the callback
     */
    //map<U> (callbackfn: (value: T, index: number, array: Array<T>) => U, thisArg?: any): Array<U> {
    //      return this.toArray().map(callbackfn, thisArg);
    //  }
    public <T> T getFiled(int index) {
        return (T) get(index);
    }
}
