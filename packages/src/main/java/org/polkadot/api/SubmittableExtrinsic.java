package org.polkadot.api;

import com.onehilltech.promises.Promise;
import org.polkadot.api.Types.QueryableModuleStorage;
import org.polkadot.api.Types.QueryableStorageFunction;
import org.polkadot.common.keyring.Types.KeyringPair;
import org.polkadot.direct.IRpcFunction;
import org.polkadot.rpc.core.IRpc;
import org.polkadot.types.Codec;
import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.TypeRegistry;
import org.polkadot.types.codec.U8a;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.metadata.v0.Modules;
import org.polkadot.types.primitive.Method;
import org.polkadot.types.rpc.ExtrinsicStatus;
import org.polkadot.types.rpc.SignedBlock;
import org.polkadot.types.type.EventRecord;
import org.polkadot.types.type.Extrinsic;
import org.polkadot.types.type.ExtrinsicSignature;
import org.polkadot.types.type.Hash;
import org.polkadot.utils.MapUtils;

import java.util.List;

public interface SubmittableExtrinsic extends Types.IExtrinsic {

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
    Types.IExtrinsic sign(KeyringPair account, Types.SignatureOptions options);

    Promise signAndSend(Object account, Types.SignatureOptions options);

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
         * the contained events
         */
        public List<EventRecord> getEvents() {
            return this.getField("events");
        }

        /**
         * the status
         */
        public ExtrinsicStatus getStatus() {
            return this.getField("status");
        }

        /**
         * Finds an EventRecord for the specified method & section
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


    abstract class SubmittableExtrinsicImpl extends Extrinsic implements SubmittableExtrinsic {

        public SubmittableExtrinsicImpl(Types.IExtrinsic _extrinsic) {
            super(_extrinsic);
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
        public U8a getHash() {
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
        public ExtrinsicSignature getSignature() {
            return (ExtrinsicSignature) _extrinsic.getSignature();
        }

        @Override
        public Extrinsic addSignature(Object signer, byte[] signature, Object nonce, byte[] era) {
            return (Extrinsic) _extrinsic.addSignature(signer, signature, nonce, era);
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

    //  function updateSigner (updateId: number, status: Hash | SubmittableResult): void {
    static void updateSigner(ApiBase apiBase, int updateId, Object status) {
        if (updateId != -1
                && apiBase.signer != null) {
            apiBase.signer.update(updateId, status);
        }
    }

    static Promise sendObservable(ApiBase apiBase, int updateId, Types.IExtrinsic _extrinsic) {

        IRpc.RpcInterfaceSection author = apiBase.rpc().author();
        IRpcFunction submitExtrinsic = author.function("submitExtrinsic");
        Promise invoke = submitExtrinsic.invoke(_extrinsic);

        return invoke.then((hash) -> {
            updateSigner(apiBase, updateId, hash);
            return Promise.value(hash);
        });
    }

    static Promise subscribeObservable(ApiBase apiBase, int updateId, Types.IExtrinsic _extrinsic, StatusCb trackingCb) {

        IRpc.RpcInterfaceSection author = apiBase.rpc().author();
        IRpcFunction submitAndWatchExtrinsic = author.function("submitAndWatchExtrinsic");

        IRpcFunction.SubscribeCallback cb = null;

        //todo
        Promise invoke;// = submitAndWatchExtrinsic.invoke(_extrinsic);
        if (trackingCb != null) {
            invoke = submitAndWatchExtrinsic.invoke(_extrinsic, (IRpcFunction.SubscribeCallback) (status) ->
                    statusObservable(apiBase, _extrinsic, (ExtrinsicStatus) status, trackingCb)
                            .then((result) -> {
                                updateSigner(apiBase, updateId, result);
                                return Promise.value(result);
                            }));
            return invoke;
        } else {
            invoke = submitAndWatchExtrinsic.invoke(_extrinsic);
            //Promise invoke = submitAndWatchExtrinsic.invoke(_extrinsic);

            return invoke
                    .then((status) ->
                            {
                                Promise promise = statusObservable(apiBase, _extrinsic, (ExtrinsicStatus) status, trackingCb);
                                return promise;
                            }
                    )
                    .then((status) -> {
                        updateSigner(apiBase, updateId, status);
                        return Promise.value(status);
                    });
        }
    }

    static Promise statusObservable(ApiBase apiBase, Types.IExtrinsic _extrinsic, ExtrinsicStatus status, StatusCb trackingCb) {
        if (!status.isFinalized()) {
            SubmittableResult result = new SubmittableResult(MapUtils.ofMap("status", status));

            if (trackingCb != null) {
                trackingCb.callback(result);
            }

            return Promise.value(result);
        }

        ExtrinsicStatus.Finalized blockHash = status.asFinalized();

        IRpcFunction getBlock = apiBase.rpc().chain().function("getBlock");
        QueryableStorageFunction events = apiBase.query().section("system").function("events");

        return Promise.all(
                getBlock.invoke(blockHash),
                events.at(blockHash, null)
        ).then((results) -> {
            SignedBlock signedBlock = (SignedBlock) results.get(0);
            Vector<EventRecord> allEvents = (Vector<EventRecord>) results.get(1);

            SubmittableResult result = new SubmittableResult(MapUtils.ofMap(
                    //          events: filterEvents(_extrinsic.hash, signedBlock, allEvents),
                    "events", ApiUtils.filterEvents(_extrinsic.getHash().toU8a(), signedBlock, allEvents),
                    "status", status
            ));

            if (trackingCb != null) {
                trackingCb.callback(result);
            }


            return Promise.value(result);
        })._catch(err -> {
            err.printStackTrace();
            return null;
        });
    }

    static SubmittableExtrinsic createSubmittableExtrinsic(ApiBase apiBase, Method extrinsic, StatusCb trackingCb) {
        Types.ConstructorCodec type = TypeRegistry.getDefaultRegistry().getOrThrow("Extrinsic", "erro");
        Types.IExtrinsic _extrinsic = (Types.IExtrinsic) type.newInstance(extrinsic);


        SubmittableExtrinsic submittableExtrinsic = new SubmittableExtrinsicImpl(_extrinsic) {

            private Types.SignatureOptions expandOptions(Types.SignatureOptions options) {
                Types.SignatureOptions signatureOptions = new Types.SignatureOptions();
                signatureOptions.setBlockHash(apiBase.genesisHash);
                signatureOptions.setVersion(apiBase.runtimeVersion);

                if (options.getNonce() != null) {
                    signatureOptions.setNonce(options.getNonce());
                }
                if (options.getVersion() != null) {
                    signatureOptions.setVersion(options.getVersion());
                }
                if (options.getBlockHash() != null) {
                    signatureOptions.setBlockHash(options.getBlockHash());
                }
                if (options.getEra() != null) {
                    signatureOptions.setEra(options.getEra());
                }
                return signatureOptions;
            }

            @Override
            public Promise<Hash> send() {
                return sendObservable(apiBase, -1, _extrinsic);
            }

            @Override
            public Promise<Runnable> send(StatusCb callback) {
                return subscribeObservable(apiBase, -1, _extrinsic, callback);
                //return subscribeObservable(apiBase, -1, _extrinsic, trackingCb);
            }

            @Override
            public Extrinsic sign(KeyringPair account, Types.SignatureOptions options) {
                /*
                 // HACK here we actually override nonce if it was specified (backwards compat for
                  // the previous signature - don't let userspace break, but allow then time to upgrade)
                  const options: Partial<SignatureOptions> = isBn(_options) || isNumber(_options)
                    ? { nonce: _options as any as number }
                    : _options;
                 */
                Types.IExtrinsic sign = _extrinsic.sign(account, expandOptions(options));
                return this;
            }

            @Override
            public Promise<Hash> signAndSend(Object account, Types.SignatureOptions _options) {
                Types.SignatureOptions options;
                if (_options == null) {
                    options = new Types.SignatureOptions();
                } else {
                    options = _options;
                }

                boolean isKeyringPair = account instanceof KeyringPair;
                String address = isKeyringPair ? ((KeyringPair) account).address() : account.toString();
                //AtomicInteger updateId = new AtomicInteger();

                QueryableModuleStorage system = apiBase.query().section("system");
                QueryableStorageFunction accountNonce = system.function("accountNonce");
                Promise call = accountNonce.call(address);

                return call.then((nonce) -> {
                    if (isKeyringPair) {
                        options.setNonce(nonce);
                        this.sign((KeyringPair) account, options);
                        return Promise.value(-1);
                    } else {
                        assert apiBase.signer != null : "no signer exists";

                        options.setBlockHash(apiBase.genesisHash);
                        options.setVersion(apiBase.runtimeVersion);
                        options.setNonce(nonce);
                        Promise<Integer> sign = apiBase.signer.sign(_extrinsic, address, options);
                        return sign;
                    }

                }).then((updateId) -> {
                    return sendObservable(apiBase, (Integer) updateId, _extrinsic);
                });
            }

            @Override
            public Promise<Runnable> signAndSend(Object account, StatusCb callback) {
                Types.SignatureOptions options;
                options = new Types.SignatureOptions();

                boolean isKeyringPair = account instanceof KeyringPair;
                String address = isKeyringPair ? ((KeyringPair) account).address() : account.toString();
                //AtomicInteger updateId = new AtomicInteger();

                QueryableModuleStorage system = apiBase.query().section("system");
                QueryableStorageFunction accountNonce = system.function("accountNonce");
                Promise call = accountNonce.call(address);

                return call.then((nonce) -> {
                    if (isKeyringPair) {
                        options.setNonce(nonce);
                        this.sign((KeyringPair) account, options);
                        return Promise.value(-1);
                    } else {
                        assert apiBase.signer != null : "no signer exists";

                        Types.SignatureOptions expandOptions = expandOptions(options);
                        Promise<Integer> sign = apiBase.signer.sign(_extrinsic, address, expandOptions);
                        return sign;
                    }

                }).then((updateId) -> {
                    return subscribeObservable(apiBase, (Integer) updateId, _extrinsic, callback);
                });
            }
        };
        return submittableExtrinsic;
    }
}
