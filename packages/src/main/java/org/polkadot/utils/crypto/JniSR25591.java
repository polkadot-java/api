package org.polkadot.utils.crypto;

public class JniSR25591 implements ISR25591 {
    public void sr25519_derive_keypair_hard(
            byte[] keypair_out,
            byte[] pair_ptr,
            byte[] cc_ptr
    )
    {
        new SR25519().sr25519_derive_keypair_hard(keypair_out, pair_ptr, cc_ptr);
    }

    public void sr25519_derive_keypair_soft(
            byte[] keypair_out,
            byte[] pair_ptr,
            byte[] cc_ptr
    )
    {
        new SR25519().sr25519_derive_keypair_soft(keypair_out, pair_ptr, cc_ptr);
    }

    public void sr25519_derive_public_soft(
            byte[] keypair_out,
            byte[] pair_ptr,
            byte[] cc_ptr
    )
    {
        new SR25519().sr25519_derive_public_soft(keypair_out, pair_ptr, cc_ptr);
    }

    public void sr25519_keypair_from_seed(
            byte[] keypair_out,
            byte[] seed_ptr
    )
    {
        new SR25519().sr25519_keypair_from_seed(keypair_out, seed_ptr);
    }

    public void sr25519_sign(
            byte[] signature_out,
            byte[] public_ptr,
            byte[] secret_ptr,
            byte[] message_ptr,
            int message_length
    )
    {
        new SR25519().sr25519_sign(signature_out, public_ptr, secret_ptr, message_ptr, message_length);
    }

    public boolean sr25519_verify(
            byte[] signature_ptr,
            byte[] message_ptr,
            int message_length,
            byte[] public_ptr
    )
    {
        return new SR25519().sr25519_verify(signature_ptr, message_ptr, message_length, public_ptr);
    }
}


// This class is copied from sr25519/SR25519.java
class SR25519
{
    public static final int SR25519_CHAINCODE_SIZE = 32;

    public static final int SR25519_KEYPAIR_SIZE = 96;

    public static final int SR25519_PUBLIC_SIZE = 32;

    public static final int SR25519_SECRET_SIZE = 64;

    public static final int SR25519_SEED_SIZE = 32;

    public static final int SR25519_SIGNATURE_SIZE = 64;


    static {
        System.loadLibrary("jni");
    }

    public native void test1(byte[] input, byte[] output);

    public native void sr25519_derive_keypair_hard(
            byte[] keypair_out,
            byte[] pair_ptr,
            byte[] cc_ptr
    );
    public native void sr25519_derive_keypair_soft(
            byte[] keypair_out,
            byte[] pair_ptr,
            byte[] cc_ptr
    );
    public native void sr25519_derive_public_soft(
            byte[] keypair_out,
            byte[] pair_ptr,
            byte[] cc_ptr
    );
    public native void sr25519_keypair_from_seed(
            byte[] keypair_out,
            byte[] seed_ptr
    );
    public native void sr25519_sign(
            byte[] signature_out,
            byte[] public_ptr,
            byte[] secret_ptr,
            byte[] message_ptr,
            int message_length
    );
    public native boolean sr25519_verify(
            byte[] signature_ptr,
            byte[] message_ptr,
            int message_length,
            byte[] public_ptr
    );
}
