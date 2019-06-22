package org.polkadot.types.metadata.v1;

import com.google.common.collect.Lists;
import org.polkadot.types.Types.ConstructorDef;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Option;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.metadata.MetadataUtils;
import org.polkadot.types.metadata.Types;
import org.polkadot.types.metadata.v1.Storage.MetadataStorageV1;
import org.polkadot.types.primitive.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MetadataV1 extends Struct implements Types.MetadataInterface {


    /**
     * The definition of a module in the system
     */
    public static class MetadataModule extends Struct {
        public MetadataModule(Object value) {
            super(new ConstructorDef()
                            .add("name", Text.class)
                            .add("prefix", Text.class)
                            .add("storage", Option.with(Vector.with(TypesUtils.getConstructorCodec(Storage.MetadataStorageV1.class))))
                            .add("calls", Option.with(Vector.with(TypesUtils.getConstructorCodec(Calls.MetadataCall.class))))
                            .add("events", Option.with(Vector.with(TypesUtils.getConstructorCodec(Events.MetadataEvent.class))))
                    , value);
        }


        /**
         * the module calls
         */
        public Option<Vector<Calls.MetadataCall>> getCalls() {
            return this.getField("calls");
        }

        /**
         * the module events
         */
        public Option<Vector<Events.MetadataEvent>> getEvents() {
            return this.getField("events");
        }

        /**
         * the module name
         */
        public Text getName() {
            return this.getField("name");
        }

        /**
         * the module prefix
         */
        public Text getPrefix() {
            return this.getField("prefix");
        }

        /**
         * the associated module storage
         */
        public Option<Vector<MetadataStorageV1>> getStorage() {
            return this.getField("storage");
        }
    }


    public MetadataV1(Object value) {
        super(new ConstructorDef()
                        .add("modules", Vector.with(TypesUtils.getConstructorCodec(MetadataModule.class)))
                , value);
    }

    /**
     * The associated modules for this structure
     */
    Vector<MetadataModule> getModules() {
        return this.getField("modules");
    }

    private List getCallNames() {
        return this.getModules().stream().map(
                (mod) -> {
                    return mod.getCalls().isNone()
                            ? Lists.newArrayList()
                            : mod.getCalls().unwrap().stream().map(
                            (fn) -> {
                                return fn.getArgs().stream().map(
                                        (arg) -> {
                                            return arg.getType().toString();
                                        }
                                ).collect(Collectors.toList());
                            }
                    ).collect(Collectors.toList());
                }
        ).collect(Collectors.toList());
    }

    private List getEventNames() {
        return this.getModules().stream().map(
                (mod) -> {
                    return mod.getEvents().isNone()
                            ? Lists.newArrayList()
                            : mod.getEvents().unwrap().stream().map(
                            (event) -> {
                                return event.getArgs().stream().map(
                                        (arg) -> arg.toString()
                                ).collect(Collectors.toList());
                            }
                    ).collect(Collectors.toList());
                }
        ).collect(Collectors.toList());
    }


    private List getStorageNames() {
        return this.getModules().stream().map(
                (mod) -> {
                    return mod.getStorage().isNone()
                            ? Lists.newArrayList()
                            : mod.getStorage().unwrap().stream().map(
                            (fn) -> {
                                return fn.getType().isMap()
                                        ? Lists.newArrayList(fn.getType().asMap().getKey().toString(), fn.getType().asMap().getValue().toString())
                                        : Lists.newArrayList(fn.getType().asType().toString());
                            }
                    ).collect(Collectors.toList());
                }
        ).collect(Collectors.toList());
    }


    @Override
    public List<String> getUniqTypes(boolean throwError) {

        List<Object> types = MetadataUtils.flattenUniq(Lists.newArrayList(this.getCallNames(), this.getEventNames(), this.getStorageNames()));
        List<String> ret = new ArrayList<>();
        types.forEach(type -> ret.add((String) type));

        MetadataUtils.validateTypes(ret, throwError);

        return null;
    }
}
