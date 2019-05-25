package org.polkadot.utils.crypto;

public interface ISR25591
{
    public static final int SR25519_CHAINCODE_SIZE = 32;

    public static final int SR25519_KEYPAIR_SIZE = 96;

    public static final int SR25519_PUBLIC_SIZE = 32;

    public static final int SR25519_SECRET_SIZE = 64;

    public static final int SR25519_SEED_SIZE = 32;

    public static final int SR25519_SIGNATURE_SIZE = 64;

    void sr25519_derive_keypair_hard(
            byte[] keypair_out,
            byte[] pair_ptr,
            byte[] cc_ptr
    );
    void sr25519_derive_keypair_soft(
            byte[] keypair_out,
            byte[] pair_ptr,
            byte[] cc_ptr
    );
    void sr25519_derive_public_soft(
            byte[] keypair_out,
            byte[] pair_ptr,
            byte[] cc_ptr
    );
    void sr25519_keypair_from_seed(
            byte[] keypair_out,
            byte[] seed_ptr
    );
    void sr25519_sign(
            byte[] signature_out,
            byte[] public_ptr,
            byte[] secret_ptr,
            byte[] message_ptr,
            int message_length
    );
    boolean sr25519_verify(
            byte[] signature_ptr,
            byte[] message_ptr,
            int message_length,
            byte[] public_ptr
    );
}
