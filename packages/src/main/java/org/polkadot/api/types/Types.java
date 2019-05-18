package org.polkadot.api.types;

import org.polkadot.api.rx.Types.RxResult;
import org.polkadot.types.Types.CodecArg;
import org.polkadot.types.Types.CodecCallback;

import java.util.List;

public interface Types {
    //export type OnCallDefinition<CodecResult, SubscriptionResult> =
    // (method: OnCallFunction<RxResult, RxResult>,
    // params?: Array<CodecArg>,
    // callback?: CodecCallback,
    // needsCallback?: boolean) => CodecResult | SubscriptionResult;

    //export type OnCallFunction<CodecResult, SubscriptionResult> = (...params: Array<CodecArg>) => CodecResult | SubscriptionResult;

    interface BaseResult {

    }

    interface OnCallDefinition<CodecResult, SubscriptionResult> {
        BaseResult apply(OnCallFunction<RxResult, RxResult> method, List<CodecArg> params, CodecCallback callback, boolean needsCallback);
    }

    interface OnCallFunction<CodecResult, SubscriptionResult> {
        BaseResult apply(List<CodecArg> params);
    }


}
