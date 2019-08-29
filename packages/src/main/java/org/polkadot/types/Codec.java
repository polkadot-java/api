package org.polkadot.types;

public interface Codec {
    int getEncodedLength();

    boolean isEmpty();

    //TODO 2019-05-07 19:13 override equals
    boolean eq(Object other);

    String toHex();

    Object toJson();

    /**
     * @description Returns the base runtime type name for this instance
     */
    default String toRawType(boolean isBare) {
        return toRawType();
    }

    /**
     * @description Returns the base runtime type name for this instance
     */
    String toRawType();


    //String toString();

    default byte[] toU8a() {
        return toU8a(false);
    }

    byte[] toU8a(boolean isBare);

    //TODO 2019-05-07 18:27 start check
    //public static Types.ConstructorCodec<? extends  Codec> builder();
    static Types.ConstructorCodec getConstructorCodec() {
        //return TypesUtils.getConstructorCodec(Thread.currentThread().getStackTrace()[1].getClassName());
        return null;
    }

}
