package org.polkadot.types.metadata.v0;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Tuple;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.primitive.Text;
import org.polkadot.types.primitive.Type;

public interface Events {

    class EventMetadata extends Struct {
        public EventMetadata(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("arguments", Vector.with(TypesUtils.getConstructorCodec(Type.class)))
                            .add("documentation", Vector.with(TypesUtils.getConstructorCodec(Text.class)))
                    , value);

        }


        /**
         * The arguments of {@link org.polkadot.types.type}
         */
        public Vector<Type> getArguments() {
            return this.getField("arguments");
        }

        /**
         * The {@link org.polkadot.types.primitive.Text} documentation
         */
        public Vector<Text> getDocumentation() {
            return this.getField("documentation");
        }

        /**
         * The name for the event
         */
        public Text getName() {
            return this.getField("name");
        }
    }

    class OuterEventMetadataEvent extends Tuple {
        public OuterEventMetadataEvent(Object value) {
            super(new Types.ConstructorDef()
                            .add("Text", Text.class)
                            .add("Vec<EventMetadata>", Vector.with(TypesUtils.getConstructorCodec(EventMetadata.class)))
                    , value);
        }

        /**
         * The EventMetadata
         */
        public Vector<EventMetadata> getEvents() {
            return this.getFiled(1);
        }

        /**
         * The name of the section
         */
        public Text getName() {
            return getFiled(0);
        }
    }


    class OuterEventMetadata extends Struct {
        public OuterEventMetadata(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("events", Vector.with(TypesUtils.getConstructorCodec(OuterEventMetadataEvent.class)))
                    , value);
        }
    }
}
