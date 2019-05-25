package org.polkadot.types.primitive;

import com.google.common.primitives.UnsignedBytes;
import org.polkadot.direct.IFunction;
import org.polkadot.types.Codec;
import org.polkadot.types.Types;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.codec.*;
import org.polkadot.types.metadata.v0.Modules;
import org.polkadot.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @name Method
 * Extrinsic function descriptor, as defined in
 * {@link https://github.com/paritytech/wiki/blob/master/Extrinsic.md#the-extrinsic-format-for-node}.
 */
public class Method extends Struct implements Types.IMethod {

    private static final Logger logger = LoggerFactory.getLogger(Method.class);


    //const injected: { [index: string]: MethodFunction } = {};
    static final Map<String, MethodFunction> INJECTED = new HashMap();

    static final MethodFunction FN_UNKNOWN = new MethodFunction() {
        @Override
        public Method apply(Object... args) {
            return null;
        }

        @Override
        public Object toJson() {
            return null;
        }
    };


    /**
     * @name MethodIndex
     * A wrapper around the `[sectionIndex, methodIndex]` value that uniquely identifies a method
     */
    public static class MethodIndex extends U8aFixed {
        public MethodIndex(Object value) {
            super(value, 16);
        }
    }

    public static class DecodeMethodInput {

        public DecodeMethodInput(Object args, MethodIndex callIndex) {
            this.args = args;
            this.callIndex = callIndex;
        }

        //args: any;
        //callIndex: MethodIndex | Uint8Array;
        public Object args;
        public MethodIndex callIndex;

        public Object getArgs() {
            return args;
        }

        public MethodIndex getCallIndex() {
            return callIndex;
        }
    }

    public static class DecodedMethod extends DecodeMethodInput {


        public DecodedMethod(Object args, MethodIndex callIndex, Types.ConstructorDef argsDef, Modules.FunctionMetadata meta) {
            super(args, callIndex);
            this.argsDef = argsDef;
            this.meta = meta;
        }

        public Types.ConstructorDef argsDef;
        public Modules.FunctionMetadata meta;

        public Types.ConstructorDef getArgsDef() {
            return argsDef;
        }

        public Modules.FunctionMetadata getMeta() {
            return meta;
        }
    }

    //  interface MethodFunction {
    //(...args: any[]): Method;
    //      callIndex: Uint8Array;
    //      meta: FunctionMetadata;
    //      method: string;
    //      section: string;
    //      toJSON: () => any;
    //  }
    public abstract static class MethodFunction implements IFunction {

        public abstract Method apply(Object... args);

        byte[] callIndex;
        Modules.FunctionMetadata meta;
        String method;
        String section;

        public abstract Object toJson();

        public byte[] getCallIndex() {
            return callIndex;
        }

        public void setCallIndex(byte[] callIndex) {
            this.callIndex = callIndex;
        }

        public Modules.FunctionMetadata getMeta() {
            return meta;
        }

        public void setMeta(Modules.FunctionMetadata meta) {
            this.meta = meta;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getSection() {
            return section;
        }

        public void setSection(String section) {
            this.section = section;
        }
    }

    //  export interface Methods {
    //[key: string]: MethodFunction;
    //  }
    public static class Methods extends LinkedHashMap<String, MethodFunction> {

    }

    //
    //  export interface ModulesWithMethods {
    //[key: string]: Methods; // Will hold modules returned by state_getMetadata
    //  }

    public static class ModulesWithMethods extends LinkedHashMap<String, Methods> {

    }

    protected Modules.FunctionMetadata meta;

    public Method(Object value, Modules.FunctionMetadata meta) {
        super(new Types.ConstructorDef()
                        .add("callIndex", MethodIndex.class)
                        .add("args", Struct.with(decodeMethod(value, meta).argsDef))
                , decodeMethod(value, meta));
        this.meta = decodeMethod(value, meta).meta;
    }

    /**
     * Decode input to pass into constructor.
     *
     * @param value - Value to decode, one of:
     *              - hex
     *              - Uint8Array
     *              - {@see DecodeMethodInput}
     * @param _meta - Metadata to use, so that `injectMethods` lookup is not
     *              necessary.
     */
    //private static decodeMethod (value: DecodedMethod | Uint8Array | string, _meta?: FunctionMetadata): DecodedMethod {
    private static DecodedMethod decodeMethod(Object value, Modules.FunctionMetadata meta) {
        if (Utils.isHex(value)) {
            return decodeMethod(Utils.hexToU8a((String) value), meta);
        } else if (Utils.isU8a(value)) {
            U8a u8a = (U8a) value;
            // The first 2 bytes are the callIndex
            U8a callIndex = u8a.subarray(0, 2);

            // Find metadata with callIndex
            Modules.FunctionMetadata fMeta = meta;
            if (fMeta == null) {
                fMeta = findFunction(callIndex).meta;
            }

            return new DecodedMethod(
                    callIndex,
                    new MethodIndex(callIndex),
                    getArgsDef(meta),
                    meta
            );
            //} else if (isObject(value) && value.callIndex && value.args) {
            //} else if (value instanceof Struct) {
        } else if (value instanceof Map) {
            Map struct = (Map) value;
            // destructure value, we only pass args/methodsIndex out
            Object args = struct.get("args");
            MethodIndex callIndex = new MethodIndex(struct.get("callIndex"));

            // Get the correct lookupIndex
            U8a lookupIndex = callIndex;
            if (callIndex instanceof MethodIndex) {
                lookupIndex = new U8a(callIndex.toU8a(false));
            }

            // Find metadata with callIndex
            Modules.FunctionMetadata fMeta = meta;
            if (fMeta == null) {
                fMeta = findFunction(callIndex).meta;
            }

            return new DecodedMethod(
                    args,
                    callIndex,
                    getArgsDef(meta),
                    meta
            );
        }

        logger.error("Method: cannot decode value {} of type {}", value, value.getClass());

        return new DecodedMethod(
                new U8a(new byte[0]),
                new MethodIndex(new byte[]{UnsignedBytes.MAX_VALUE, UnsignedBytes.MAX_VALUE}),
                new Types.ConstructorDef(),
                new Modules.FunctionMetadata(null)
        );
    }


    // We could only inject the meta (see injectMethods below) and then do a
    // meta-only lookup via
    //
    //   metadata.modules[callIndex[0]].module.call.functions[callIndex[1]]
    //
    // As a convenience helper though, we return the full constructor function,
    // which includes the meta, name, section & actual interface for calling
    static MethodFunction findFunction(U8a callIndex) {
        //assert(Object.keys(injected).length > 0, 'Calling Method.findFunction before extrinsics have been injected.');
        return INJECTED.getOrDefault(callIndex.toString(), FN_UNKNOWN);
    }

    /**
     * Get a mapping of `argument name -> argument type` for the function, from
     * its metadata.
     *
     * @param meta - The function metadata used to get the definition.
     */
    private static Types.ConstructorDef getArgsDef(Modules.FunctionMetadata meta) {
        Types.ConstructorDef constructorDef = new Types.ConstructorDef();
        filterOrigin(meta).stream().forEach((argumentMetadata) -> {
            Types.ConstructorCodec type = CreateType.getTypeClass(CreateType.getTypeDef(argumentMetadata.getType().toString(), null));
            constructorDef.add(argumentMetadata.getName().toString(), type);
        });
        return constructorDef;
    }

    // If the extrinsic function has an argument of type `Origin`, we ignore it
    public static List<Modules.FunctionArgumentMetadata> filterOrigin(Modules.FunctionMetadata meta) {
        // FIXME should be `arg.type !== Origin`, but doesn't work...
        if (meta != null) {
            Vector<Modules.FunctionArgumentMetadata> arguments = meta.getArguments();
            List<Modules.FunctionArgumentMetadata> ret = arguments.stream()
                    .filter((Modules.FunctionArgumentMetadata argument) -> !argument.getType().toString().equals("Origin"))
                    .collect(Collectors.toList());
            return ret;
        }
        return Collections.emptyList();
    }


    // This is called/injected by the API on init, allowing a snapshot of
    // the available system extrinsics to be used in lookups
    public static void injectMethods(Map<String, Map<String, MethodFunction>> moduleMethods) {
        moduleMethods.forEach((k, v) -> {
            v.forEach((ik, iv) -> INJECTED.put(iv.callIndex.toString(), iv));
        });
    }


    /**
     * The arguments for the function call
     */
    @Override
    public List<Codec> getArgs() {
        // FIXME This should return a Struct instead of an Array
        Struct args = this.getField("args");
        return args.values().stream().collect(Collectors.toList());
    }

    @Override
    public Types.ConstructorDef getArgsDef() {
        return getArgsDef(this.meta);
    }

    /**
     * The encoded `[sectionIndex, methodIndex]` identifier
     */
    @Override
    public byte[] getCallIndex() {
        MethodIndex callIndex = this.getField("callIndex");
        return callIndex.toU8a(false);
    }

    /**
     * The encoded data
     */
    @Override
    public byte[] getData() {
        Struct args = this.getField("args");
        return args.toU8a(false);
    }


    /**
     * `true` if the `Origin` type is on the method (extrinsic method)
     */
    @Override
    public boolean hasOrigin() {
        Vector<Modules.FunctionArgumentMetadata> arguments = this.meta.getField("arguments");
        Modules.FunctionArgumentMetadata firstArg = arguments.size() > 0 ? arguments.get(0) : null;

        return firstArg != null && firstArg.getType().toString().equals("Origin");
    }

    /**
     * The [[FunctionMetadata]]
     */
    @Override
    public Modules.FunctionMetadata getMeta() {
        return this.meta;
    }

}
