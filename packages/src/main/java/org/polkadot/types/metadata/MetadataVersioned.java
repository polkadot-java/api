package org.polkadot.types.metadata;

import org.polkadot.types.Types.ConstructorDef;
import org.polkadot.types.codec.EnumType;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vec;
import org.polkadot.types.metadata.v0.MetadataV0;
import org.polkadot.types.metadata.v1.MetadataV1;
import org.polkadot.types.metadata.v1.ToV0;
import org.polkadot.types.metadata.v2.MetadataV2;
import org.polkadot.types.metadata.v2.ToV1;
import org.polkadot.types.metadata.v3.MetadataV3;
import org.polkadot.types.metadata.v3.ToV2;

import java.util.List;

/**
 * The versioned runtime metadata as a decoded structure
 */
public class MetadataVersioned extends Struct implements Types.MetadataInterface {


    //class MetadataEnum extends EnumType<Null | MetadataV1 | MetadataV2> {
    public static class MetadataEnum extends EnumType<Types.MetadataInterface> {

        public MetadataEnum(Object value) {
            super(new ConstructorDef()
                            .add("MetadataV0", MetadataV0.class)
                            .add("MetadataV1", MetadataV1.class)
                            .add("MetadataV2", MetadataV2.class)
                            .add("MetadataV3", MetadataV3.class)
                    , value, -1, null
            );
        }


        /**
         * Returns the wrapped values as a V0 object
         */
        public MetadataV0 asV0() {
            return ((MetadataV0) this.value());
        }

        /**
         * Returns the wrapped values as a V1 object
         */
        public MetadataV1 asV1() {
            return ((MetadataV1) this.value());
        }

        /**
         * Returns the wrapped values as a V2 object
         */
        public MetadataV2 asV2() {
            return ((MetadataV2) this.value());
        }


        /**
         * Returns the wrapped values as a V3 object
         */
        public MetadataV3 asV3() {
            return ((MetadataV3) this.value());
        }


        /**
         * The version this metadata represents
         */
        public int getVersion() {
            return this.index();
        }
    }


    private MetadataV0 convertedV0;
    private MetadataV1 convertedV1;
    private MetadataV2 convertedV2;

    public MetadataVersioned(Object value) {
        super(new ConstructorDef()
                        .add("magicNumber", MagicNumber.class)
                        .add("metadata", MetadataEnum.class)
                , value);


    }

    /**
     * the metadata version this structure represents
     */
    public int getVersion() {
        return ((MetadataEnum) this.getField("metadata")).getVersion();
    }

    /**
     * the metadata wrapped
     */
    private MetadataEnum getMetadata() {
        return this.getField("metadata");
    }

    /**
     * Returns the wrapped values as a V0 object
     */
    public MetadataV0 asV0() {
        if (this.getVersion() == 0) {
            return this.getMetadata().asV0();
        }

        if (this.convertedV0 == null) {
            this.convertedV0 = ToV0.toV0(this.asV1());
        }

        return this.convertedV0;
    }


    /**
     * Returns the wrapped values as a V1 object
     */
    public MetadataV1 asV1() {

        if (this.getVersion() == 1) {
            return this.getMetadata().asV1();
        }

        int version = this.getVersion();
        assert (this.getVersion() == 2 || this.getVersion() == 3) : "Cannot convert metadata from v" + this.getVersion() + " to v1";

        if (this.convertedV1 == null) {
            if (version == 3) {
                this.convertedV1 = ToV1.toV1(this.asV2());
            }
            if (version == 2) {
                this.convertedV1 = ToV1.toV1(this.getMetadata().asV2());
            }
        }

        return this.convertedV1;
    }

    /**
     * Returns the wrapped values as a V2 object
     */
    public MetadataV2 asV2() {

        if (this.getVersion() == 2) {
            return this.getMetadata().asV2();
        }

        assert this.getVersion() == 3 : "Cannot convert metadata from v" + this.getVersion() + " to v1";

        if (this.convertedV2 == null) {
            this.convertedV2 = ToV2.toV2(this.getMetadata().asV3());
        }

        return this.convertedV2;
    }


    /**
     * Returns the wrapped values as a V3 object
     */
    public MetadataV3 asV3() {
        assert this.getVersion() == 3 : "Cannot convert metadata from v" + this.getVersion() + " to v3";
        return this.getMetadata().asV3();
    }


    public List<String> getUniqTypes(boolean throwError) {
        //return ((Types.MetadataInterface) this.getMetadata().value()).getUniqTypes(throwError);
        //TODO 2019-09-28 04:09
        throw new UnsupportedOperationException();
    }

    @Override
    public Vec getVecModules() {
        //TODO 2019-09-28 03:59
        return null;
    }
}
