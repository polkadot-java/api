package org.polkadot.api;

import com.google.common.collect.Lists;
import com.onehilltech.promises.Promise;
import org.polkadot.api.Types.*;
import org.polkadot.common.keyring.Types.KeyringPair;
import org.polkadot.direct.IRpcFunction;
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
import org.polkadot.utils.MapUtils;

import java.util.List;

public interface SubmittableExtrinsic<ApplyResult> extends Types.IExtrinsic {

    ApplyResult send();

    ApplyResult send(StatusCb callback);

    @Override
    Types.IExtrinsic sign(KeyringPair account, Types.SignatureOptions options);

    ApplyResult signAndSend(Object account, Types.SignatureOptions options);

    ApplyResult signAndSendCb(Object account, StatusCb callback);


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
    static void updateSigner(ApiInterfacePromise apiInterfacePromise, int updateId, Object status) {
        if (updateId != -1
                && apiInterfacePromise.getSigner() != null) {
            apiInterfacePromise.getSigner().update(updateId, status);
        }
    }

    static Promise sendObservable(ApiInterfacePromise apiInterfacePromise, int updateId, Types.IExtrinsic _extrinsic) {

        //IRpc.RpcInterfaceSection author = apiBase.rpc().author();
        //IRpcFunction submitExtrinsic = apiBase.rpc().author().function("submitExtrinsic");
        Promise submitExtrinsic = apiInterfacePromise.rpc().author().function("submitExtrinsic").invoke(_extrinsic);
        Promise invoke = submitExtrinsic;

        return invoke.then((hash) -> {
            updateSigner(apiInterfacePromise, updateId, hash);
            return Promise.value(hash);
        });
    }

    static Promise subscribeObservable(ApiInterfacePromise apiInterfacePromise, int updateId, Types.IExtrinsic _extrinsic, StatusCb trackingCb) {

        DecoratedRpcMethod<Promise> submitAndWatchExtrinsic = apiInterfacePromise.rpc().author().function("submitAndWatchExtrinsic");

        IRpcFunction.SubscribeCallback cb = null;

        //todo
        Promise invoke;// = submitAndWatchExtrinsic.invoke(_extrinsic);
        if (trackingCb != null) {
            invoke = submitAndWatchExtrinsic.invoke(_extrinsic, (IRpcFunction.SubscribeCallback) (status) ->
                    statusObservable(apiInterfacePromise, _extrinsic, (ExtrinsicStatus) status, trackingCb)
                            .then((result) -> {
                                updateSigner(apiInterfacePromise, updateId, result);
                                return Promise.value(result);
                            }));
            return invoke;
        } else {
            invoke = submitAndWatchExtrinsic.invoke(_extrinsic);
            //Promise invoke = submitAndWatchExtrinsic.invoke(_extrinsic);

            return invoke
                    .then((status) ->
                            {
                                Promise promise = statusObservable(apiInterfacePromise, _extrinsic, (ExtrinsicStatus) status, trackingCb);
                                return promise;
                            }
                    )
                    .then((status) -> {
                        updateSigner(apiInterfacePromise, updateId, status);
                        return Promise.value(status);
                    });
        }
    }

    static Promise statusObservable(ApiInterfacePromise apiInterfacePromise, Types.IExtrinsic _extrinsic, ExtrinsicStatus status, StatusCb trackingCb) {
        if (!status.isFinalized()) {
            SubmittableResult result = new SubmittableResult(MapUtils.ofMap("status", status));

            if (trackingCb != null) {
                trackingCb.callback(result);
            }

            return Promise.value(result);
        }

        ExtrinsicStatus.Finalized blockHash = status.asFinalized();

        System.out.println("====== handle status.isFinalized() " + blockHash);

        DecoratedRpcMethod<Promise> getBlock = apiInterfacePromise.rpc().chain().function("getBlock");
        QueryableStorageFunction<Promise> events = apiInterfacePromise.query().section("system").function("events");

        System.out.println("======try get events" );

        return Promise.all(
                getBlock.invoke(blockHash),
                events.at(blockHash, null)
        ).then((results) -> {
            SignedBlock signedBlock = (SignedBlock) results.get(0);
            Vector<EventRecord> allEvents = (Vector<EventRecord>) results.get(1);


            System.out.println("====== get events" + allEvents);

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

    static <ApplyResult> SubmittableExtrinsic<ApplyResult> createSubmittableExtrinsic(ApiBase.ApiType apiType, ApiInterfacePromise apiPromise, Method extrinsic, StatusCb trackingCb,
                                                                                      OnCallDefinition<ApplyResult> onCallDefinition) {
        Types.ConstructorCodec type = TypeRegistry.getDefaultRegistry().getOrThrow("Extrinsic", "erro");
        Types.IExtrinsic _extrinsic = (Types.IExtrinsic) type.newInstance(extrinsic);

        boolean noStatusCb = apiType == ApiBase.ApiType.RX;

        SubmittableExtrinsic submittableExtrinsic = new SubmittableExtrinsicImpl(_extrinsic) {

            private Types.SignatureOptions expandOptions(Types.SignatureOptions options) {
                Types.SignatureOptions signatureOptions = new Types.SignatureOptions();
                signatureOptions.setBlockHash(apiPromise.getGenesisHash());
                signatureOptions.setVersion(apiPromise.getRuntimeVersion());

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
            public ApplyResult send() {
                boolean isSubscription = noStatusCb;
                return onCallDefinition.apply(
                        new OnCallFunction() {
                            @Override
                            public Promise apply(Object... params) {
                                if (isSubscription) {
                                    StatusCb statusCb = null;
                                    //    StatusCb statusCb = new StatusCb() {
                                    //    @Override
                                    //    public Object callback(SubmittableResult result) {
                                    //        System.out.println("  in empty  StatusCb " + result);
                                    //        return null;
                                    //    }
                                    //};
                                    return subscribeObservable(apiPromise, -1, _extrinsic, statusCb);
                                } else {
                                    return sendObservable(apiPromise, -1, _extrinsic);
                                }


                                //return isSubscription
                                //        ? subscribeObservable(apiPromise, -1, _extrinsic, null)
                                //        : sendObservable(apiPromise, -1, _extrinsic);
                            }
                        },
                        Lists.newArrayList(),
                        //noStatusCb,
                        false,
                        null
                );
            }

            @Override
            public ApplyResult send(StatusCb callback) {

                return onCallDefinition.apply(
                        new OnCallFunction() {
                            @Override
                            public Promise apply(Object... params) {

                                StatusCb statusCb = callback;
                                if (params != null && params.length > 0) {
                                    IRpcFunction.SubscribeCallback subscribeCallback = (IRpcFunction.SubscribeCallback) params[0];
                                    statusCb = new StatusCb() {
                                        @Override
                                        public Object callback(SubmittableResult result) {
                                            subscribeCallback.callback(result);
                                            return null;
                                        }
                                    };
                                }

                                return subscribeObservable(apiPromise, -1, _extrinsic, statusCb);
                            }
                        },
                        Lists.newArrayList(),
                        true,
                        callback == null
                                ? null
                                : new IRpcFunction.SubscribeCallback() {
                            @Override
                            public void callback(Object o) {
                                callback.callback((SubmittableResult) o);
                            }
                        }
                );

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
            public ApplyResult signAndSend(Object account, Types.SignatureOptions _options) {
                Types.SignatureOptions options;
                if (_options == null) {
                    options = new Types.SignatureOptions();
                } else {
                    options = _options;
                }

                boolean isKeyringPair = account instanceof KeyringPair;
                String address = isKeyringPair ? ((KeyringPair) account).address() : account.toString();
                //AtomicInteger updateId = new AtomicInteger();

                QueryableModuleStorage<Promise> system = apiPromise.query().section("system");
                QueryableStorageFunction<Promise> accountNonce = system.function("accountNonce");
                Promise call = accountNonce.call(address);

                SubmittableExtrinsic self = this;

                return onCallDefinition.apply(
                        new OnCallFunction() {
                            @Override
                            public Promise apply(Object... params) {
                                return call.then((nonce) -> {
                                    if (isKeyringPair) {
                                        options.setNonce(nonce);
                                        self.sign((KeyringPair) account, options);
                                        return Promise.value(-1);
                                    } else {
                                        assert apiPromise.getSigner() != null : "no signer exists";

                                        options.setBlockHash(apiPromise.getGenesisHash());
                                        options.setVersion(apiPromise.getRuntimeVersion());
                                        options.setNonce(nonce);
                                        Promise<Integer> sign = apiPromise.getSigner().sign(_extrinsic, address, options);
                                        return sign;
                                    }

                                }).then((updateId) -> {
                                    return sendObservable(apiPromise, (Integer) updateId, _extrinsic);
                                });

                            }
                        },
                        Lists.newArrayList(),
                        false,
                        null);
            }

            @Override
            public ApplyResult signAndSendCb(Object account, StatusCb callback) {
                Types.SignatureOptions options;
                options = new Types.SignatureOptions();

                boolean isKeyringPair = account instanceof KeyringPair;
                String address = isKeyringPair ? ((KeyringPair) account).address() : account.toString();
                //AtomicInteger updateId = new AtomicInteger();

                QueryableModuleStorage<Promise> system = apiPromise.query().section("system");
                QueryableStorageFunction<Promise> accountNonce = system.function("accountNonce");
                Promise call = accountNonce.call(address);
                SubmittableExtrinsic self = this;


                return onCallDefinition.apply(
                        new OnCallFunction() {
                            @Override
                            public Promise apply(Object... params) {

                                StatusCb statusCb = callback;
                                if (params != null && params.length > 0) {
                                    IRpcFunction.SubscribeCallback subscribeCallback = (IRpcFunction.SubscribeCallback) params[0];
                                    statusCb = new StatusCb() {
                                        @Override
                                        public Object callback(SubmittableResult result) {
                                            subscribeCallback.callback(result);
                                            return null;
                                        }
                                    };
                                }

                                StatusCb finalStatusCb = statusCb;
                                return call.then((nonce) -> {
                                    if (isKeyringPair) {
                                        options.setNonce(nonce);
                                        self.sign((KeyringPair) account, options);
                                        return Promise.value(-1);
                                    } else {
                                        assert apiPromise.getSigner() != null : "no signer exists";

                                        Types.SignatureOptions expandOptions = expandOptions(options);
                                        Promise<Integer> sign = apiPromise.getSigner().sign(_extrinsic, address, expandOptions);
                                        return sign;
                                    }

                                }).then((updateId) -> {
                                    return subscribeObservable(apiPromise, (Integer) updateId, _extrinsic, finalStatusCb);
                                });
                            }
                        },
                        Lists.newArrayList(),
                        true,
                        callback == null
                                ? null
                                : new IRpcFunction.SubscribeCallback() {
                            @Override
                            public void callback(Object o) {
                                callback.callback((SubmittableResult) o);
                            }
                        }
                );

            }
        };
        return submittableExtrinsic;
    }
}
