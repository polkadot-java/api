package org.polkadot.common.keyring.pair;

import org.apache.commons.lang3.StringUtils;
import org.polkadot.common.keyring.Types;
import org.polkadot.common.keyring.address.AddressCodec;
import org.polkadot.common.keyring.pair.Types.PairInfo;
import org.polkadot.utils.crypto.Nacl;
import org.polkadot.utils.crypto.Schnorrkel;
import org.polkadot.utils.crypto.Types.Keypair;

public interface Index {

    static boolean isSr25519(String type) {
        return type.equals(org.polkadot.utils.crypto.Types.KeypairType_SR);
    }

    static Keypair fromSeed(String type, byte[] seed) {
        return isSr25519(type)
                ? Schnorrkel.schnorrkelKeypairFromSeed(seed)
                : Nacl.naclKeypairFromSeed(seed);
    }

    static byte[] sign(String type, byte[] message, final Keypair pair) {
        return isSr25519(type)
                ? Schnorrkel.schnorrkelSign(message, pair)
                : Nacl.naclSign(message, pair);
    }

    static boolean verify(String type, byte[] message, byte[] signature, byte[] publicKey) {
        return isSr25519(type)
                ? Schnorrkel.schnorrkelVerify(message, signature, publicKey)
                : Nacl.naclVerify(message, signature, publicKey);
    }

    /**
     * @name pair
     * @summary Creates a keyring pair object
     * @description Creates a keyring pair object with provided account public key, metadata, and encoded arguments.
     * The keyring pair stores the account state including the encoded address and associated metadata.
     * <p>
     * It has properties whose values are functions that may be called to perform account actions:
     * <p>
     * - `address` function retrieves the address associated with the account.
     * - `decodedPkcs8` function is called with the account passphrase and account encoded public key.
     * It decodes the encoded public key using the passphrase provided to obtain the decoded account public key
     * and associated secret key that are then available in memory, and changes the account address stored in the
     * state of the pair to correspond to the address of the decoded public key.
     * - `encodePkcs8` function when provided with the correct passphrase associated with the account pair
     * and when the secret key is in memory (when the account pair is not locked) it returns an encoded
     * public key of the account.
     * - `getMeta` returns the metadata that is stored in the state of the pair, either when it was originally
     * created or set via `setMeta`.
     * - `publicKey` returns the public key stored in memory for the pair.
     * - `sign` may be used to return a signature by signing a provided message with the secret
     * key (if it is in memory) using Nacl.
     * - `toJson` calls another `toJson` function and provides the state of the pair,
     * it generates arguments to be passed to the other `toJson` function including an encoded public key of the account
     * that it generates using the secret key from memory (if it has been made available in memory)
     * and the optionally provided passphrase argument. It passes a third boolean argument to `toJson`
     * indicating whether the public key has been encoded or not (if a passphrase argument was provided then it is encoded).
     * The `toJson` function that it calls returns a JSON object with properties including the `address`
     * and `meta` that are assigned with the values stored in the corresponding state variables of the account pair,
     * an `encoded` property that is assigned with the encoded public key in hex format, and an `encoding`
     * property that indicates whether the public key value of the `encoded` property is encoded or not.
     */
    //export default function createPair (type: KeypairType, { publicKey, secretKey }: PairInfo, meta: KeyringPair$Meta = {}, encoded: Uint8Array | null = null): KeyringPair {
    static Types.KeyringPair createPair(String type, PairInfo pairInfo, Types.KeyringPairMeta meta, byte[] encoded) {
        return new KeyringPairDefault(type, pairInfo, meta, encoded);
    }

    class KeyringPairDefault implements Types.KeyringPair {

        String type;
        PairInfo pairInfo;
        Types.KeyringPairMeta meta;
        byte[] encoded;

        public KeyringPairDefault(String type, PairInfo pairInfo, Types.KeyringPairMeta meta, byte[] encoded) {
            this.type = type;
            this.pairInfo = pairInfo;
            this.meta = meta;
            this.encoded = encoded;
        }

        @Override
        public String getType() {
            return this.type;
        }

        @Override
        public String address() {
            return AddressCodec.encodeAddress(pairInfo.getPublicKey());
        }

        @Override
        public void decodePkcs8(String passphrase, byte[] _encoded) {

            PairCodec.DecodeResult decoded = PairCodec.decode(passphrase, _encoded != null ? _encoded : this.encoded);
            if (decoded.getSecretKey().length == 64) {
                this.pairInfo.publicKey = decoded.getPublicKey();
                this.pairInfo.secretKey = decoded.getSecretKey();
            } else {
                Keypair pair = fromSeed(type, decoded.secretKey);

                this.pairInfo.publicKey = pair.getPublicKey();
                this.pairInfo.secretKey = pair.getSecretKey();
            }
        }

        @Override
        public byte[] encodePkcs8(String passphrase) {
            return PairCodec.encode(this.pairInfo, passphrase);
        }

        @Override
        public Types.KeyringPairMeta getMeta() {
            return this.meta;
        }

        @Override
        public boolean isLocked() {
            return (this.pairInfo.secretKey == null || this.pairInfo.secretKey.length == 0);
        }

        @Override
        public void lock() {
            this.pairInfo.secretKey = new byte[0];
        }

        @Override
        public byte[] publicKey() {
            return this.pairInfo.publicKey;
        }

        @Override
        public void setMeta(Types.KeyringPairMeta meta) {
            this.meta.putAll(meta);
        }

        @Override
        public byte[] sign(byte[] message) {
            return Index.sign(this.type, message, new Keypair(this.pairInfo.publicKey, this.pairInfo.secretKey));
        }

        @Override
        public Types.KeyringPairJson toJson(String passphrase) {
            return PairCodec.toJson(this.type,
                    new PairCodec.PairStateJson(this.meta, this.pairInfo.publicKey),
                    PairCodec.encode(this.pairInfo, passphrase),
                    StringUtils.isNotEmpty(passphrase));
        }

        @Override
        public boolean verify(byte[] message, byte[] signature) {
            return Index.verify(this.type, message, signature, publicKey());
        }
    }
}
