package org.polkadot.utils.crypto;

import org.polkadot.utils.Utils;
import net.i2p.crypto.eddsa.*;
import net.i2p.crypto.eddsa.spec.*;

import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class Nacl {

    /**
     * @name naclDecrypt
     * @summary Decrypts a message using the supplied secretKey and nonce
     * @description Returns an decrypted message, using the `secret` and `nonce`.
     * @example <BR>
     * <p>
     * ```javascript
     * import { naclDecrypt } from '@polkadot/util-crypto';
     * <p>
     * naclDecrypt([...], [...], [...]); // => [...]
     * ```
     */
    //export default function naclDecrypt (encrypted: Uint8Array, nonce: Uint8Array, secret: Uint8Array): Uint8Array | null {
    //    return nacl.secretbox.open(encrypted, nonce, secret) || null;
    //}
    public static byte[] naclDecrypt(byte[] encrypted, byte[] nonce, byte[] secret) {
        return TweetNaCl.secretbox_open(encrypted, nonce, secret);
    }


    /**
     * @name naclEncrypt
     * @summary Encrypts a message using the supplied secretKey and nonce
     * @description Returns an encrypted message, using the `secretKey` and `nonce`. If the `nonce` was not supplied, a random value is generated.
     * @example <BR>
     * <p>
     * ```javascript
     * import { naclEncrypt } from '@polkadot/util-crypto';
     * <p>
     * naclEncrypt([...], [...]); // => [...]
     * ```
     */
//type Encrypted = {
//        encrypted: Uint8Array,
//    nonce: Uint8Array
//};
//export default function naclEncrypt (message: Uint8Array, secret: Uint8Array, nonce: Uint8Array = randomAsU8a(24)): Encrypted {
    public static Encrypted naclEncrypt(byte[] message, byte[] secret) {
        return naclEncrypt(message, secret, Utils.randomAsU8a(24));
    }

    public static Encrypted naclEncrypt(byte[] message, byte[] secret, byte[] nonce) {
        Encrypted result = new Encrypted();
        result.encrypted = TweetNaCl.secretbox(message, nonce, secret);
        result.nonce = nonce;
        return result;
    }

    public static class Encrypted {
        public byte[] getEncrypted() {
            return encrypted;
        }

        public byte[] getNonce() {
            return nonce;
        }

        byte[] encrypted;
        byte[] nonce;
    }


    /**
     * @name naclKeypairFromSeed
     * @summary Creates a new public/secret keypair from a seed.
     * @description Returns a object containing a `publicKey` & `secretKey` generated from the supplied seed.
     * @example <BR>
     * <p>
     * ```javascript
     * import { naclKeypairFromSeed } from '@polkadot/util-crypto';
     * <p>
     * naclKeypairFromSeed(...); // => { secretKey: [...], publicKey: [...] }
     * ```
     */
    //export default function naclKeypairFromSeed (seed: Uint8Array): Keypair {
    //    if (isReady()) {
    //
    //const full = ed25519KeypairFromSeed(seed);
    //
    //        return {
    //                publicKey: full.slice(32),
    //                secretKey: full.slice(0, 64)
    //};
    //    }
    //
    //    return nacl.sign.keyPair.fromSeed(seed);
    //}
    public static Types.Keypair naclKeypairFromSeed(byte[] seed) {
        EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
        EdDSAPrivateKeySpec privKey = new EdDSAPrivateKeySpec(seed, spec);
        byte[] priv = privKey.getH();
        byte[] pub = privKey.getA().toByteArray();
        return new Types.Keypair(pub, priv);
    }


    /**
     * @name naclSign
     * @summary Signs a message using the supplied secretKey
     * @description Returns message signature of `message`, using the `secretKey`.
     * @example <BR>
     * <p>
     * ```javascript
     * import { naclSign } from '@polkadot/util-crypto';
     * <p>
     * naclSign([...], [...]); // => [...]
     * ```
     */
    //export default function naclSign (message: Uint8Array, { publicKey, secretKey }: Partial<Keypair>): Uint8Array {
    //    assert(secretKey, 'Expected valid secretKey');
    //
    //    return isReady()
    //            ? ed25519Sign(publicKey as Uint8Array, (secretKey as Uint8Array).subarray(0, 32), message)
    //: nacl.sign.detached(message, secretKey as Uint8Array);
    //}
    public static byte[] naclSign(byte[] message, final Types.Keypair keypair) {
        try {
            EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
            Signature sgr = new EdDSAEngine(MessageDigest.getInstance(spec.getHashAlgorithm()));

            EdDSAPrivateKeySpec edPrivateKey = new EdDSAPrivateKeySpec(spec, keypair.secretKey);
            PrivateKey privateKey = new EdDSAPrivateKey(edPrivateKey);
            sgr.initSign(privateKey);
            sgr.update(message);
            return sgr.sign();
        }
        catch (Exception e) {
            return null;
        }
    }


    /**
     * @name naclSign
     * @summary Verifies the signature on the supplied message.
     * @description Verifies the `signature` on `message` with the supplied `plublicKey`. Returns `true` on sucess, `false` otherwise.
     * @example <BR>
     * <p>
     * ```javascript
     * import { naclVerify } from '@polkadot/util-crypto';
     * <p>
     * naclVerify([...], [...], [...]); // => true/false
     * ```
     */
    //export default function naclVerify (message: Uint8Array, signature: Uint8Array, publicKey: Uint8Array): boolean {
    //    return isReady()
    //            ? ed25519Verify(signature, message, publicKey)
    //            : nacl.sign.detached.verify(message, signature, publicKey);
    //}
    public static boolean naclVerify(byte[] message, byte[] signature, byte[] publicKey) {
        try {
            EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
            Signature sgr = new EdDSAEngine(MessageDigest.getInstance(spec.getHashAlgorithm()));

            EdDSAPublicKeySpec edPublicKey = new EdDSAPublicKeySpec(publicKey, spec);
            PublicKey pubKey = new EdDSAPublicKey(edPublicKey);
            sgr.initVerify(pubKey);
            sgr.update(message);
            return sgr.verify(signature);
        }
        catch (Exception e) {
            return false;
        }
    }

}
