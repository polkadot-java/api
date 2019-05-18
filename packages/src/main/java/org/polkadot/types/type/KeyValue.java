package org.polkadot.types.type;


import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Option;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Tuple;
import org.polkadot.types.primitive.StorageData;
import org.polkadot.types.primitive.StorageKey;

/**
 * @name KeyValue
 * @description KeyValue structure. Since most of the keys and resultant values in Subtrate is
 * hashed and/or encoded, this does not wrap [[Text]], but rather a [[Bytes]]
 * for the keys and values. (Not to be confused with the KeyValue in [[Metadata]], that
 * is actually for Maps, whereas this is a representation of actaul storage values)
 */
public class KeyValue extends Struct {
    //    type KeyValueValue = {
    //            key?: AnyU8a,
    //    value?: AnyU8a
    //};
    //  constructor (value?: KeyValueValue | Uint8Array) {
    public KeyValue(Object value) {
        super(new Types.ConstructorDef()
                        .add("key", StorageKey.class)
                        .add("value", StorageData.class)
                , value);
    }


    //export type KeyValueOptionValue = [AnyU8a, AnyU8a?];

    /**
     * @name KeyValueOption
     * @description A key/value change. This is similar to the [[KeyValue]] structure,
     * however in this case the value could be optional. Here it extends
     * from a [[Tuple]], indicating the use inside areas such as [[StorageChangeSet]]
     */
    public static class KeyValueOption extends Tuple {
        //constructor (value?: KeyValueOptionValue | Uint8Array) {
        public KeyValueOption(Object value) {
            super(new Types.ConstructorDef()
                            .add("StorageKey", StorageKey.class)
                            .add("Option<StorageData>", Option.with(TypesUtils.getConstructorCodec(StorageData.class)))
                    , value);
        }

        /**
         * @description The [[StorageKey]]
         */
        public StorageKey getKey() {
            return this.getFiled(0);
        }


        /**
         * @description The [[Option]] [[StorageData]]
         */
        public Option<StorageData> getValue() {
            return this.getFiled(1);
        }
    }
}
