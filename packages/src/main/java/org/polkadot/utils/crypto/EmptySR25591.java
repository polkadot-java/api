package org.polkadot.utils.crypto;

public class EmptySR25591 implements ISR25591 {
    public void sr25519_derive_keypair_hard(
            byte[] keypair_out,
            byte[] pair_ptr,
            byte[] cc_ptr
    )
    {
    }

    public void sr25519_derive_keypair_soft(
            byte[] keypair_out,
            byte[] pair_ptr,
            byte[] cc_ptr
    )
    {
    }

    public void sr25519_derive_public_soft(
            byte[] keypair_out,
            byte[] pair_ptr,
            byte[] cc_ptr
    )
    {
    }

    public void sr25519_keypair_from_seed(
            byte[] keypair_out,
            byte[] seed_ptr
    )
    {
    }

    public void sr25519_sign(
            byte[] signature_out,
            byte[] public_ptr,
            byte[] secret_ptr,
            byte[] message_ptr,
            int message_length
    )
    {
    }

    public boolean sr25519_verify(
            byte[] signature_ptr,
            byte[] message_ptr,
            int message_length,
            byte[] public_ptr
    )
    {
        return false;
    }
}
