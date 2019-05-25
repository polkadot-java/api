package org.polkadot.common.keyring.pair;

public interface Types {
    class PairInfo {
        protected byte[] publicKey;
        protected byte[] secretKey;
        protected byte[] seed;

        public byte[] getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(byte[] publicKey) {
            this.publicKey = publicKey;
        }

        public byte[] getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(byte[] secretKey) {
            this.secretKey = secretKey;
        }

        public byte[] getSeed() {
            return seed;
        }

        public void setSeed(byte[] seed) {
            this.seed = seed;
        }
        //
        //public PairInfo(byte[] publicKey, byte[] secretKey, byte[] seed) {
        //    this.publicKey = publicKey;
        //    this.secretKey = secretKey;
        //    this.seed = seed;
        //}
    }
}
