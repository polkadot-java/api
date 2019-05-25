package org.polkadot.utils.crypto;

public interface Types {

    class Keypair {
        byte[] publicKey;
        byte[] secretKey;

        public Keypair(byte[] publicKey, byte[] secretKey) {
            this.publicKey = publicKey;
            this.secretKey = secretKey;
        }

        public byte[] getPublicKey() {
            return publicKey;
        }

        public byte[] getSecretKey() {
            return secretKey;
        }
    }

    class Seedpair {
        byte[] publicKey;
        byte[] seed;

        public byte[] getPublicKey() {
            return publicKey;
        }

        public byte[] getSeed() {
            return seed;
        }
    }

    //enum KeypairType {
    //    ed25519, sr25519
    //}

    String KeypairType_ED = "ed25519";
    String KeypairType_SR = "sr25519";
}
