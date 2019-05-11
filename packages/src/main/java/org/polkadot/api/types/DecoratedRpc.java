package org.polkadot.api.types;

import org.polkadot.types.Types;

import java.util.HashMap;
import java.util.Map;

public class DecoratedRpc<CodecResult, SubscriptionResult> {

    private DecoratedRpcSection<CodecResult, SubscriptionResult> author;
    private DecoratedRpcSection<CodecResult, SubscriptionResult> chain;
    private DecoratedRpcSection<CodecResult, SubscriptionResult> state;
    private DecoratedRpcSection<CodecResult, SubscriptionResult> system;

    public abstract class DecoratedRpcSection<CodecResult, SubscriptionResult> {
        Map<String, DecoratedRpcMethod> methodMap = new HashMap<>();
        //[index: string]: DecoratedRpc$Method<CodecResult, SubscriptionResult>;

        public DecoratedRpcMethod getMethod(String methodName) {
            return methodMap.get(methodName);
        }
    }

    // checked against max. params in jsonrpc, 1 for subs, 3 without
    public abstract static class DecoratedRpcMethod<CodecResult, SubscriptionResult> {
        public abstract SubscriptionResult invoke1(Types.CodecCallback callback);

        public abstract SubscriptionResult invoke2(Types.CodecArg arg1, Types.CodecCallback callback);

        public abstract CodecResult invoke3(Types.CodecArg arg1, Types.CodecArg arg2, Types.CodecArg arg3);
    }


    public DecoratedRpcSection<CodecResult, SubscriptionResult> getAuthor() {
        return author;
    }

    public DecoratedRpcSection<CodecResult, SubscriptionResult> getChain() {
        return chain;
    }

    public DecoratedRpcSection<CodecResult, SubscriptionResult> getState() {
        return state;
    }

    public DecoratedRpcSection<CodecResult, SubscriptionResult> getSystem() {
        return system;
    }
}
