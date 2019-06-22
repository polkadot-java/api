package org.polkadot.types.type;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Compact;
import org.polkadot.types.codec.Struct;

import java.math.BigInteger;


/**
 * Just a Balance/BlockNumber tuple to encode when a chunk of funds will be unlocked
 */
public class UnlockChunk extends Struct {

    public UnlockChunk(Object value) {
        super(new Types.ConstructorDef()
                        .add("value", Compact.with(TypesUtils.getConstructorCodec(Balance.class)))
                        .add("era", Compact.with(TypesUtils.getConstructorCodec(BlockNumber.class)))
                , value);
    }

    /**
     * Era number at which point it'll be unlocked
     */
    public BlockNumber getEra() {
        Compact era = this.getField("era");
        BigInteger bigInteger = era.toBn();
        return new BlockNumber(bigInteger);
    }

    /**
     * Amount of funds to be unlocked
     */
    public Balance getValue() {
        Compact value = this.getField("value");
        BigInteger bigInteger = value.toBn();
        return new Balance(bigInteger);
    }

}
