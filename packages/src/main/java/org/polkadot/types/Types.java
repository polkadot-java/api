package org.polkadot.types;

import org.polkadot.types.codec.U8a;
import org.polkadot.types.metadata.v0.Modules;
import org.polkadot.types.primitive.Method;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Types {
    /**
     * export type CodecArg = Codec | BN | Boolean | String | Uint8Array | boolean | number | string | undefined | CodecArgArray | CodecArgObject;
     */
    class CodecArg {
    }

    interface CodecCallback<T extends Codec> {
        Object apply(T t);
    }


    //interface Constructor<T extends Codec> {
    //    T instance(List<?> value);
    //}

    interface IHash extends Codec {
    }
    //interface IHash extends U8a {}


    interface ConstructorCodec<T extends Codec> {

        //T newInstance();
        T newInstance(Object... values);

        Class<T> getTClass();
    }

    class ConstructorDef {

        List<String> names = new ArrayList<>();
        //List<Class<? extends Codec>> types = new ArrayList<>();
        List<ConstructorCodec> types = new ArrayList<>();

        List<Class> classes = new ArrayList<>();


        public ConstructorDef add(String name, ConstructorCodec<? extends Codec> type) {
            this.names.add(name);
            this.types.add(type);
            return this;
        }

        public ConstructorDef add(String name, Class<? extends Codec> clazz) {
            this.names.add(name);
            Types.ConstructorCodec builder = TypesUtils.getConstructorCodec(clazz);
            this.types.add(builder);
            return this;
        }

        public List<String> getNames() {
            return names;
        }

        public List<ConstructorCodec> getTypes() {
            return types;
        }

        public ConstructorDef() {
        }

        public ConstructorDef(List<ConstructorCodec> list) {
            for (ConstructorCodec type : list) {

                Type gType = ((ParameterizedType) type.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
                String name = gType instanceof Class ? ((Class) gType).getSimpleName() : gType.getTypeName();
                this.add(name, type);
            }
        }

        //
        //public ConstructorDef(List<CreateType.TypeDef> defs) {
        //    defs.stream().forEach(def -> {
        //        names.add(def.getName());
        //        types.add(def.getType())
        //    });
        //}
    }

    //class TypeDef {
    //    Map<String, Codec> codecMap;
    //}

    class RegistryTypes {
        //  [name: string]: Constructor | string | { [name: string]: string }
        Map<String, Class<?>> registryTypes;
    }
    /*

interface CodecArgObject {
  [index: string]: CodecArg;
}

interface CodecArgArray extends Array<CodecArg> { }


export interface Constructor<T = Codec> {
  new(...value: Array<any>): T;
}

export type ConstructorDef<T = Codec> = { [index: string]: Constructor<T> };

export type TypeDef = { [index: string]: Codec };

export type RegistryTypes = {
  [name: string]: Constructor | string | { [name: string]: string }
};

    * */

    //export interface ArgsDef {
    //[index: string]: Constructor;
    //}
    interface IMethod extends Codec {

        //export interface IMethod extends Codec {
        //    readonly args: Array<Codec>;
        //    readonly argsDef: ArgsDef;
        //    readonly callIndex: Uint8Array;
        //    readonly data: Uint8Array;
        //    readonly hasOrigin: boolean;
        //    readonly meta: FunctionMetadata;
        //}

        List<Codec> getArgs();

        ConstructorDef getArgsDef();

        byte[] getCallIndex();

        byte[] getData();

        boolean hasOrigin();

        Modules.FunctionMetadata getMeta();
    }


    //export interface RuntimeVersionInterface {
    //    readonly apis: Array<any>;
    //    readonly authoringVersion: BN;
    //    readonly implName: String;
    //    readonly implVersion: BN;
    //    readonly specName: String;
    //    readonly specVersion: BN;
    //}

    interface RuntimeVersionInterface {
        List<? extends Object> getApis();

        BigInteger getAuthoringVersion();

        String getImplName();

        BigInteger getImplVersion();

        String getSpecName();

        BigInteger getSpecVersion();

    }

    interface IExtrinsic extends IMethod {
        U8a getHash();

        boolean isSigned();

        Method getMethod();

        IExtrinsicSignature getSignature();

        //addSignature(signer:Address|Uint8Array, signature:Uint8Array, nonce:AnyNumber, era?:Uint8Array):IExtrinsic;

        IExtrinsic addSignature(Object signer, byte[] signature, Object nonce, byte[] era);

        //sign(account:KeyringPair, options:SignatureOptions):IExtrinsic;
        IExtrinsic sign(org.polkadot.common.keyring.Types.KeyringPair account, Types.SignatureOptions options);
    }

    class SignatureOptions {
        Object blockHash;
        byte[] era;
        Object nonce;
        RuntimeVersionInterface version;

        public Object getBlockHash() {
            return blockHash;
        }

        public void setBlockHash(Object blockHash) {
            this.blockHash = blockHash;
        }

        public byte[] getEra() {
            return era;
        }

        public void setEra(byte[] era) {
            this.era = era;
        }

        public Object getNonce() {
            return nonce;
        }

        public void setNonce(Object nonce) {
            this.nonce = nonce;
        }

        public RuntimeVersionInterface getVersion() {
            return version;
        }

        public void setVersion(RuntimeVersionInterface version) {
            this.version = version;
        }

    }

    interface IExtrinsicSignature extends Codec {
        boolean isSigned();
    }

}
