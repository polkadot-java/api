package org.polkadot.common.keyring.pair;

import com.google.common.primitives.UnsignedBytes;

public interface Defaults {


    int NONCE_LENGTH = 24;
    byte[] PKCS8_DIVIDER = new byte[]{UnsignedBytes.checkedCast(161), 35, 3, 33, 0};
    byte[] PKCS8_HEADER = new byte[]{48, 83, 2, 1, 1, 48, 5, 6, 3, 43, 101, 112, 4, 34, 4, 32};
    int PUB_LENGTH = 32;
    int SEC_LENGTH = 64;
    int SEED_LENGTH = 32;

    /**
     *
     const NONCE_LENGTH = 24;
     const PKCS8_DIVIDER = new Uint8Array([161, 35, 3, 33, 0]);
     const PKCS8_HEADER = new Uint8Array([48, 83, 2, 1, 1, 48, 5, 6, 3, 43, 101, 112, 4, 34, 4, 32]);
     const PUB_LENGTH = 32;
     const SEC_LENGTH = 64;
     const SEED_LENGTH = 32;

     export {
     NONCE_LENGTH,
     PKCS8_DIVIDER,
     PKCS8_HEADER,
     PUB_LENGTH,
     SEC_LENGTH,
     SEED_LENGTH
     };

     */
}
