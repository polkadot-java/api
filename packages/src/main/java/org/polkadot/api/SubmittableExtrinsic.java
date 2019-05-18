package org.polkadot.api;

import com.onehilltech.promises.Promise;
import org.polkadot.common.keyring.Types.KeyringPair;
import org.polkadot.direct.IRpcFunction;
import org.polkadot.rpc.core.IRpc;
import org.polkadot.types.Codec;
import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.TypeRegistry;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.metadata.v0.Modules;
import org.polkadot.types.primitive.Method;
import org.polkadot.types.rpc.ExtrinsicStatus;
import org.polkadot.types.type.EventRecord;
import org.polkadot.types.type.Hash;

import java.util.List;

interface SubmittableExtrinsic extends Types.IExtrinsic {

    //interface SubmittableExtrinsic<CodecResult, SubscriptionResult> extends IExtrinsic {
    //    send ()SumbitableResultResult<CodecResult, SubscriptionResult>;
    //
    //    send (statusCb(resultSubmittableResult) => any)SumbitableResultSubscription<CodecResult, SubscriptionResult>;
    //
    //    sign (accountKeyringPair, _optionsPartial<SignatureOptions>)this;
    //
    //    signAndSend (accountKeyringPair | string | AccountId | Address, options?Partial<Partial<SignatureOptions>>)SumbitableResultResult<CodecResult, SubscriptionResult>;
    //
    //    signAndSend (accountKeyringPair | string | AccountId | Address, statusCbStatusCb)SumbitableResultSubscription<CodecResult, SubscriptionResult>;
    //}
    //
    Promise<Hash> send();

    Promise<Runnable> send(StatusCb callback);

    //sign (account: KeyringPair, _options: Partial<SignatureOptions>): this;
    @Override
    SubmittableExtrinsic sign(KeyringPair account, Types.SignatureOptions options);

    Promise<Hash> signAndSend(Object account, Types.SignatureOptions options);

    Promise<Runnable> signAndSend(Object account, StatusCb callback);
    ///**
    // * type SumbitableResultResult<CodecResult, SubscriptionResult> =
    // *   CodecResult extends Observable<any>
    // *     ? Observable<SubmittableResult>
    // *     : Promise<Hash>;
    // *
    // * type SumbitableResultSubscription<CodecResult, SubscriptionResult> =
    // *   SubscriptionResult extends Observable<any>
    // *     ? Observable<SubmittableResult>
    // *     : Promise<() => void>;
    // */


    interface StatusCb {
        Object callback(SubmittableResult result);
    }

    class SubmittableResult extends Struct {

        public SubmittableResult(Object value) {
            super(new Types.ConstructorDef()
                            .add("events", Vector.with(TypesUtils.getConstructorCodec(EventRecord.class)))
                            .add("status", ExtrinsicStatus.class)
                    , value);
        }


        /**
         * @description the contained events
         */
        List<EventRecord> getEvents() {
            return this.getField("events");
        }

        /**
         * @description the status
         */
        public ExtrinsicStatus getStatus() {
            return this.getField("status");
        }

        /**
         * @description Finds an EventRecord for the specified method & section
         */
        public EventRecord findRecord(String section, String method) {
            return this.getEvents()
                    .stream()
                    .filter(
                            eventRecord ->
                                    eventRecord.getEvent().getSection().equals(section)
                                            && eventRecord.getEvent().getMethod().equals(method))
                    .findFirst()
                    .orElse(null);
        }


    }


    abstract class SubmittableExtrinsicImpl implements SubmittableExtrinsic {
        public SubmittableExtrinsicImpl(Types.IExtrinsic _extrinsic) {
            this._extrinsic = _extrinsic;
        }

        Types.IExtrinsic _extrinsic;

        @Override
        public List<Codec> getArgs() {
            return _extrinsic.getArgs();
        }

        @Override
        public Types.ConstructorDef getArgsDef() {
            return _extrinsic.getArgsDef();
        }

        @Override
        public byte[] getCallIndex() {
            return _extrinsic.getCallIndex();
        }

        @Override
        public byte[] getData() {
            return _extrinsic.getData();
        }

        @Override
        public boolean hasOrigin() {
            return _extrinsic.hasOrigin();
        }

        @Override
        public Modules.FunctionMetadata getMeta() {
            return _extrinsic.getMeta();
        }

        @Override
        public Types.IHash getHash() {
            return _extrinsic.getHash();
        }

        @Override
        public boolean isSigned() {
            return _extrinsic.isSigned();
        }

        @Override
        public Method getMethod() {
            return _extrinsic.getMethod();
        }

        @Override
        public Types.IExtrinsicSignature getSignature() {
            return _extrinsic.getSignature();
        }

        @Override
        public Types.IExtrinsic addSignature(Object signer, byte[] signature, Object nonce, byte[] era) {
            return _extrinsic.addSignature(signer, signature, nonce, era);
        }

        @Override
        public int getEncodedLength() {
            return _extrinsic.getEncodedLength();
        }

        @Override
        public boolean isEmpty() {
            return _extrinsic.isEmpty();
        }

        @Override
        public boolean eq(Object other) {
            return _extrinsic.eq(other);
        }

        @Override
        public String toHex() {
            return _extrinsic.toHex();
        }

        @Override
        public Object toJson() {
            return _extrinsic.toJson();
        }

        @Override
        public byte[] toU8a() {
            return _extrinsic.toU8a();
        }

        @Override
        public byte[] toU8a(boolean isBare) {
            return _extrinsic.toU8a(isBare);
        }
    }

    static SubmittableExtrinsic createSubmittableExtrinsic(ApiBase apiBase, Method extrinsic, StatusCb trackingCb) {
        Types.ConstructorCodec type = TypeRegistry.getDefaultRegistry().getOrThrow("Extrinsic", "erro");
        Types.IExtrinsic _extrinsic = (Types.IExtrinsic) type.newInstance(extrinsic);


        SubmittableExtrinsic submittableExtrinsic = new SubmittableExtrinsicImpl(_extrinsic) {


            @Override
            public Promise<Hash> send() {
                IRpc.RpcInterfaceSection author = apiBase.rpc().author();
                IRpcFunction submitExtrinsic = author.function("submitExtrinsic");
                Promise invoke = submitExtrinsic.invoke(_extrinsic);
                return null;
            }

            @Override
            public Promise<Runnable> send(StatusCb callback) {
                return null;
            }

            @Override
            public SubmittableExtrinsic sign(KeyringPair account, Types.SignatureOptions options) {
                return null;
            }

            @Override
            public Promise<Hash> signAndSend(Object account, Types.SignatureOptions options) {
                return null;
            }

            @Override
            public Promise<Runnable> signAndSend(Object account, StatusCb callback) {
                return null;
            }
        };
        return submittableExtrinsic;
    }
}
