package org.polkadot.utils.crypto;

import java.util.Arrays;

public class Schnorrkel {

    private static ISR25591 createSR25591()
    {
        // use this if there is no JNI
        return new EmptySR25591();
        // use this line to integrate JNI
        //return new JniSR25591();
    }

    private static byte[] extractPublicKey(byte[] keyPair)
    {
        return Arrays.copyOfRange(keyPair, ISR25591.SR25519_SECRET_SIZE, keyPair.length);
    }

    private static byte[] extractSecretKey(byte[] keyPair)
    {
        return Arrays.copyOfRange(keyPair, 0, ISR25591.SR25519_SECRET_SIZE);
    }

    private static Types.Keypair toKeyPair(byte[] keyPair)
    {
        return new Types.Keypair(extractPublicKey(keyPair), extractSecretKey(keyPair));
    }

    /**
     * @name schnorrkelKeypairFromSeed
     * @description Returns a object containing a `publicKey` & `secretKey` generated from the supplied seed.
     */
    //export default function schnorrkelKeypairFromSeed (seed: Uint8Array): Keypair {
    //    return keypairFromU8a(
    //            sr25519KeypairFromSeed(seed)
    //    );
    //}
    public static Types.Keypair schnorrkelKeypairFromSeed(byte[] seed) {
        byte[] kp = new byte[ISR25591.SR25519_KEYPAIR_SIZE];
        createSR25591().sr25519_keypair_from_seed(kp, seed);
        return toKeyPair(kp);
    }


    /**
     * @name schnorrkelSign
     * @description Returns message signature of `message`, using the supplied pair
     */
    //export default function schnorrkelSign (message: Uint8Array, { publicKey, secretKey }: Partial<Keypair>): Uint8Array {
    //    assert(publicKey && publicKey.length === 32, 'Expected valid publicKey, 32-bytes');
    //    assert(secretKey && secretKey.length === 64, 'Expected valid secretKey, 64-bytes');
    //
    //    return sr25519Sign(publicKey as Uint8Array, secretKey as Uint8Array, message);
    //}
    public static byte[] schnorrkelSign(byte[] message, final Types.Keypair keypair) {
        byte[] sig = new byte[ISR25591.SR25519_SIGNATURE_SIZE];
        createSR25591().sr25519_sign(sig, keypair.getPublicKey(), keypair.getSecretKey(), message, message.length);
        return sig;
    }

    /**
     * @name schnorrkelVerify
     * @description Verifies the signature of `message`, using the supplied pair
     */
    //export default function schnorrkelVerify (message: Uint8Array, signature: Uint8Array, publicKey: Uint8Array): boolean {
    //    return sr25519Verify(signature, message, publicKey);
    //}
    public static boolean schnorrkelVerify(byte[] message, byte[] signature, byte[] publicKey) {
        return createSR25591().sr25519_verify(signature, message, message.length, publicKey);
    }
}
