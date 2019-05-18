package org.polkadot.rpc.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.onehilltech.promises.Promise;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.polkadot.direct.IRpcFunction;
import org.polkadot.direct.IRpcModule;
import org.polkadot.rpc.core.IRpc.RpcInterfaceSection;
import org.polkadot.rpc.json.JsonRpc;
import org.polkadot.rpc.json.types.JsonRpcMethod;
import org.polkadot.rpc.json.types.JsonRpcSection;
import org.polkadot.rpc.provider.IProvider;
import org.polkadot.rpc.provider.ws.WsProvider;
import org.polkadot.types.Codec;
import org.polkadot.types.Types;
import org.polkadot.types.codec.CreateType;
import org.polkadot.types.codec.Option;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.metadata.v0.Modules;
import org.polkadot.types.primitive.StorageKey;
import org.polkadot.types.rpc.StorageChangeSet;
import org.polkadot.types.type.KeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RpcCore implements IRpcModule {
    private static final Logger logger = LoggerFactory.getLogger(RpcCore.class);


    IProvider provider;
    RpcInterfaceSection author;
    RpcInterfaceSection chain;
    RpcInterfaceSection state;
    RpcInterfaceSection system;


    @Override
    public RpcInterfaceSection author() {
        return author;
    }

    @Override
    public RpcInterfaceSection chain() {
        return chain;
    }

    @Override
    public RpcInterfaceSection state() {
        return state;
    }

    @Override
    public RpcInterfaceSection system() {
        return system;
    }


    public RpcCore() {
        this(new WsProvider());
    }

    public RpcCore(IProvider provider) {
        this.provider = provider;

        this.author = this.createRpcSection(JsonRpc.author);
        this.chain = this.createRpcSection(JsonRpc.chain);
        this.state = this.createRpcSection(JsonRpc.state);
        this.system = this.createRpcSection(JsonRpc.system);
    }

    Map<String, RpcInterfaceSection> sectionMap = new HashMap<>();


    public RpcInterfaceSection createRpcSection(JsonRpcSection jsonRpcSection) {
        RpcInterfaceSection ret = new RpcInterfaceSection();
        Map<String, JsonRpcMethod> rpcMethods = jsonRpcSection.rpcMethods;

        for (JsonRpcMethod jsonRpcMethod : rpcMethods.values()) {
            if (jsonRpcMethod.isSubscription()) {
                ret.addFunction(jsonRpcMethod.getMethod(), createMethodSubscribe(jsonRpcMethod));
            } else {
                ret.addFunction(jsonRpcMethod.getMethod(), createMethodSend(jsonRpcMethod));
            }
        }
        return ret;
    }

    static String signature(JsonRpcMethod jsonRpcMethod) {

        String input = String.join(", ", jsonRpcMethod.getParams()
                .stream()
                .map(param -> param.getName() + ":" + param.getType())
                .collect(Collectors.toList())
                .toArray(new String[]{}));

        return jsonRpcMethod.getMethod() + "(" + input + ")" + jsonRpcMethod.getType();
    }

    public IRpcFunction createMethodSend(JsonRpcMethod jsonRpcMethod) {

        final String rpcName = String.format("%s_%s", jsonRpcMethod.getSection(), jsonRpcMethod.getMethod());

        IRpcFunction call = new IRpcFunction() {
            @Override
            public Promise invoke(Object... values) {
                try {
                    List<Codec> params = RpcCore.this.formatInputs(jsonRpcMethod, Lists.newArrayList(values));
                    List<Object> paramsJson = params.stream().map(Codec::toJson).collect(Collectors.toList());
                    return RpcCore.this.provider.send(rpcName, paramsJson, null)
                            .then((result) -> {
                                Object output = RpcCore.this.formatOutput(jsonRpcMethod, params, result);
                                return Promise.value(output);
                            })._catch((err) -> {
                                err.printStackTrace();
                                return null;
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                    String msg = String.format("%s:: %s", RpcCore.signature(jsonRpcMethod), e.getMessage());
                    logger.error(msg);
                    throw new RuntimeException(msg, e);
                }
            }
        };

        return call;
    }

    private List<Codec> formatInputs(JsonRpcMethod jsonRpcMethod, List<Object> inputs) {
        final long reqArgCount = jsonRpcMethod.getParams().stream().filter(p -> !p.isOptional()).count();
        String optText = reqArgCount == jsonRpcMethod.getParams().size()
                ? ""
                : "(" + (jsonRpcMethod.getParams().size() - reqArgCount) + " optional)";

        assert inputs.size() >= reqArgCount && inputs.size() <= jsonRpcMethod.getParams().size()
                : "Expected " + jsonRpcMethod.getParams().size() + " parameters" + optText + ", " + inputs + " found instead";

        List<Codec> ret = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
            Codec type = CreateType.createType(jsonRpcMethod.getParams().get(i).getType(), inputs.get(i));
            ret.add(type);
        }
        return ret;
    }

    private Object formatOutput(JsonRpcMethod jsonRpcMethod, List<Codec> params, Object result) {
        if (result instanceof String) {
            String json = ((String) result).trim();

            if (json.startsWith("{")) {
                try {
                    JSONObject jsonObject = JSON.parseObject((String) result);
                    result = jsonObject;
                } catch (Exception e) {
                }
            } else if (json.startsWith("[")) {
                try {
                    JSONArray jsonArray = JSON.parseArray((String) result);
                    result = jsonArray;
                } catch (Exception e) {
                }
            }
        }

        Codec base = CreateType.createType(jsonRpcMethod.getType(), result);

        if (jsonRpcMethod.getType().equals("StorageData")) {
            // single return value (via state.getStorage), decode the value based on the
            // outputType that we have specified. Fallback to Data on nothing
            StorageKey key = ((StorageKey) params.get(0));
            String type = key.getOutputType();
            if (StringUtils.isEmpty(type)) {
                type = "Data";
            }

            Types.ConstructorCodec clazz = CreateType.createClass(type);
            //      const meta = key.meta || { default: undefined, modifier: { isOptional: true } };
            Modules.StorageFunctionMetadata meta = key.getMeta();

            if (key.getMeta() != null
                    && key.getMeta().getType().isMap()
                    && key.getMeta().getType().asMap().isLinked()) {

                // linked map
                return clazz.newInstance(base);
            } else {
                if (meta == null || meta.getModifier().isOptional()) {
                    return new Option<>(clazz, result == null ? null : clazz.newInstance(base));
                } else {
                    return clazz.newInstance(base);
                }
            }

        } else if (jsonRpcMethod.getType().equals("StorageChangeSet")) {
            // multiple return values (via state.storage subscription), decode the values
            // one at a time, all based on the query types. Three values can be returned -
            //   - Base - There is a valid value, non-empty
            //   - null - The storage key is empty (but in the resultset)
            //   - undefined - The storage value is not in the resultset
            List<Codec> ret = Lists.newArrayList();
            Vector<StorageKey> keys = (Vector<StorageKey>) params.get(0);
            for (StorageKey key : keys) {
                // Fallback to Data (i.e. just the encoding) if we don't have a specific type

                String type = key.getOutputType();
                if (StringUtils.isEmpty(type)) {
                    type = "Data";
                }
                Types.ConstructorCodec clazz = CreateType.createClass(type);

                // see if we have a result value for this specific key
                String hexKey = key.toHex();
                KeyValue.KeyValueOption option = ((StorageChangeSet) base).getChanges()
                        .stream()
                        .filter(item -> item.getKey().toHex().equals(hexKey))
                        .findFirst().orElse(null);

                Modules.StorageFunctionMetadata meta = key.getMeta();
                //const meta = meta || { default: undefined, modifier: { isOptional: true } };

                if (option == null) {
                    // if we don't have a value, do not fill in the entry, it will be up to the
                    // caller to sort this out, either ignoring or having a cache for older values
                    //result.push(undefined);TODO
                } else {
                    if (key.getMeta() != null
                            && key.getMeta().getType().isMap()
                            && key.getMeta().getType().asMap().isLinked()) {

                        // linked map
                        ret.add(clazz.newInstance(option.getValue().unwrapOr(null)));
                    } else {
                        if (meta == null || meta.getModifier().isOptional()) {

                            // create option either with the existing value, or empty when
                            // there is no value returned
                            ret.add(new Option(clazz, option.getValue().isNone() ? null : clazz.newInstance(option.getValue().unwrap())));
                        } else {
                            // for `null` we fallback to the default value, or create an empty type,
                            // otherwise we return the actual value as retrieved
                            ret.add(clazz.newInstance(option.getValue().unwrapOr(meta.getDefault())));
                        }
                    }

                }
            }

            return ret;
        }

        return base;
    }

    public IRpcFunction createMethodSubscribe(JsonRpcMethod jsonRpcMethod) {
        String updateType = jsonRpcMethod.getPubsub()[0];
        String subMethod = jsonRpcMethod.getPubsub()[1];
        String unsubMethod = jsonRpcMethod.getPubsub()[2];

        String subName = jsonRpcMethod.getSection() + "_" + subMethod;
        String unsubName = jsonRpcMethod.getSection() + "_" + unsubMethod;
        String subType = jsonRpcMethod.getSection() + "_" + updateType;

        IRpcFunction ret = new IRpcFunction() {

            @Override
            public Promise invoke(Object... _values) {
                try {
                    ArrayList<Object> values = Lists.newArrayList(_values);

                    SubscribeCallback cb = null;
                    if (CollectionUtils.isNotEmpty(values)) {
                        Object o = values.get(values.size() - 1);
                        if (o instanceof SubscribeCallback) {
                            Object remove = values.remove(values.size() - 1);
                            cb = (SubscribeCallback) remove;
                        }
                    }
                    //Object remove = values.remove(values.size() - 1);
                    //SubscribeCallback cb = (SubscribeCallback) remove;

                    List<Codec> params = RpcCore.this.formatInputs(jsonRpcMethod, values);
                    List<Object> paramsJson = params.stream().map(Codec::toJson).collect(Collectors.toList());

                    if (cb != null) {
                        SubscribeCallback finalCb = cb;
                        IProvider.CallbackHandler update = (error, result) -> {

                            if (error != null) {
                                logger.error("{}::{}", RpcCore.signature(jsonRpcMethod), error);
                                return;
                            }

                            finalCb.callback(RpcCore.this.formatOutput(jsonRpcMethod, params, result));
                        };

                        Promise<String> subscribe = RpcCore.this.provider.subscribe(subType, subName, paramsJson, update);
                        return subscribe.then(
                                (String subscriptionId) ->
                                {
                                    logger.debug(" subscriptionId = {}", subscriptionId);
                                    return Promise.value(
                                            (Unsubscribe<Promise>) () -> RpcCore.this.provider.unsubscribe(subType, unsubName, Integer.parseInt(subscriptionId))
                                    );
                                }
                        )._catch((err) -> {
                            logger.error(" promise error ", err);
                            return Promise.value(err);
                        });
                    } else {
                        //SubscribeCallback finalCb = cb;
                        //IProvider.CallbackHandler update = (error, result) -> {
                        //
                        //    if (error != null) {
                        //        logger.error("{}::{}", RpcCore.signature(jsonRpcMethod), error);
                        //        return;
                        //    }
                        //
                        //    finalCb.callback(RpcCore.this.formatOutput(jsonRpcMethod, params, result));
                        //};

                        Promise<String> subscribe = RpcCore.this.provider.subscribe(subType, subName, paramsJson, null);
                        return subscribe.then(
                                (String subscriptionId) ->
                                {
                                    logger.debug(" subscriptionId = {}", subscriptionId);
                                    return Promise.value(
                                            (Unsubscribe<Promise>) () -> RpcCore.this.provider.unsubscribe(subType, unsubName, Integer.parseInt(subscriptionId))
                                    );
                                }
                        )._catch((err) -> {
                            logger.error(" promise error ", err);
                            return Promise.value(err);
                        });
                    }

                } catch (Exception e) {
                    logger.error(" " + RpcCore.signature(jsonRpcMethod), e);
                    throw e;
                }
            }

            //@Override
            //Promise unsubscribe(int subscriptionId) {
            //    return RpcCore.this.provider.unsubscribe(subType, unsubName, subscriptionId);
            //}
        };

        return ret;

    }

    public IProvider getProvider() {
        return provider;
    }

    public RpcInterfaceSection getAuthor() {
        return author;
    }

    public RpcInterfaceSection getChain() {
        return chain;
    }

    public RpcInterfaceSection getState() {
        return state;
    }

    public RpcInterfaceSection getSystem() {
        return system;
    }


}
