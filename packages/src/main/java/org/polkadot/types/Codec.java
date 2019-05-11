package org.polkadot.types;

public interface Codec {
    int getEncodedLength();

    boolean isEmpty();

    //TODO 2019-05-07 19:13 override equals
    boolean eq(Object other);

    String toHex();

    Object toJson();

    //String toString();

    default byte[] toU8a() {
        return toU8a(false);
    }

    byte[] toU8a(boolean isBare);

    //TODO 2019-05-07 18:27 start check
    //public static Types.ConstructorCodec<? extends  Codec> builder();
    default Types.ConstructorCodec getBuilder() {
        return TypesUtils.getConstructorCodec(this.getClass());
    }

}
