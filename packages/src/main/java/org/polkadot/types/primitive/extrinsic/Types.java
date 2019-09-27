package org.polkadot.types.primitive.extrinsic;

public interface Types {


    class ExtrinsicOptions {
        public boolean isSigned;

        public ExtrinsicOptions(boolean isSigned) {
            this.isSigned = isSigned;
        }
    }

    class ExtrinsicSignatureOptions {
        public boolean isSigned;

        public ExtrinsicSignatureOptions(boolean isSigned) {
            this.isSigned = isSigned;
        }
    }

    interface ExtrinsicExtraValue {
        byte[] getEra();

        long getNonce();

        long getTip();
    }

}
