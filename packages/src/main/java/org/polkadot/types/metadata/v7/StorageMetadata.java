package org.polkadot.types.metadata.v7;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vec;
import org.polkadot.types.metadata.v5.Storage;
import org.polkadot.types.primitive.Text;

public class StorageMetadata extends Struct {


    public StorageMetadata(Object value) {
        super(new Types.ConstructorDef()
                        .add("prefix", Text.class)
                        .add("items", Vec.with(TypesUtils.getConstructorCodec(Storage.StorageFunctionMetadataV5.class)))
                , value);
    }


    /**
     * @description the storage entries
     */
    public Vec<Storage.StorageFunctionMetadataV5> getItems() {
        return this.getField("items");
    }

    /**
     * @description the prefix for this module
     */
    public Text getPrefix() {
        return this.getField("prefix");
    }

}
