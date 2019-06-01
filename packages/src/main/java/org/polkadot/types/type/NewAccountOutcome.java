package org.polkadot.types.type;

import com.google.common.collect.Lists;
import org.polkadot.types.codec.Enum;

/**
 * @name NewAccountOutcome
 * @description Enum to track the outcome for creation of an [[AccountId]]
 */
public class NewAccountOutcome extends Enum {
    //constructor (index?: U8a | Uint8Array | number) {
    public NewAccountOutcome(int index) {
        super(Lists.newArrayList(
                "NoHint",
                "GoodHint",
                "BadHint"), index);
    }
}