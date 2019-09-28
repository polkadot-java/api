package org.polkadot.types.primitive.extrinsic;

public interface Constants {

    int BIT_SIGNED = 0b10000000;

    int BIT_UNSIGNED = 0;

    byte[] EMPTY_U8A = new byte[0];

    int LATEST_VERSION = 3;

    // TODO We really want to swap this to V3, however all the test data is setup
// for V1, so this will take some time to convert... "some" time :)
    int DEFAULT_VERSION = 1;

    byte[] IMMORTAL_ERA = new byte[]{0};

    int UNMASK_VERSION = 0b01111111;

}
