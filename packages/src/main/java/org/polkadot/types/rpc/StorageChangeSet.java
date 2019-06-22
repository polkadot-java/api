package org.polkadot.types.rpc;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.type.Hash;
import org.polkadot.types.type.KeyValue;


/**
 * A set of storage changes. It contains the Block hash and
 * a list of the actual changes that took place as an array of
 * KeyValueOption
 */
public class StorageChangeSet extends Struct {
    //type StorageChangeSetValue = {
    //        block?: AnyU8a,
    //changes?: Array<KeyValueOptionValue>
    //};
    //constructor (value?: StorageChangeSetValue | Uint8Array) {
    public StorageChangeSet(Object value) {
        super(new Types.ConstructorDef()
                        .add("block", Hash.class)
                        .add("changes", Vector.with(TypesUtils.getConstructorCodec(KeyValue.KeyValueOption.class)))
                , value);
    }

    public Vector<KeyValue.KeyValueOption> getChanges() {
        return this.getField("changes");
    }

    public Hash getBlock(){
        return this.getField("block");
    }
}
