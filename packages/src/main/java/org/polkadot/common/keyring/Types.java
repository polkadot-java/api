package org.polkadot.common.keyring;

import java.util.LinkedHashMap;

public interface Types {

    enum KeypairType {
        ed25519, sr25519
    }

    //export type KeypairType = 'ed25519' | 'sr25519';

    class KeyringPairMeta extends LinkedHashMap<String, Object> {

    }

    //  export type KeyringPair$Meta = {
    //[index: string]: any
    //  };
    //export type KeyringPair$JsonVersion = '0' | '1' | '2';
    enum KeyringPairJsonVersion {
        v0, v1, v2
    }

    class KeyringPairJsonEncoding {
        String[] content;
        String type;
        KeyringPairJsonVersion version;
    }

    //export type KeyringPair$JsonEncoding = {
    //    content: ['pkcs8', KeypairType],
    //    type: 'xsalsa20-poly1305' | 'none',
    //            version: KeyringPair$JsonVersion
    //};


    class KeyringPairJson {
        String address;
        String encoded;
        KeyringPairJsonEncoding encoding;
        KeyringPairMeta meta;
    }

    //export type KeyringPair$Json = {
    //    address: string,
    //            encoded: string,
    //            encoding: KeyringPair$JsonEncoding,
    //            meta: KeyringPair$Meta
    //};

    interface KeyringPair {
        KeypairType getType();

        String address();

        void decodePkcs8(String passphrase, byte[] encoded);

        byte[] encodePkcs8(String passphrase);

        KeyringPairMeta getMeta();

        boolean isLocked();

        void lock();

        byte[] getPublicKey();

        void setMeta(KeyringPairMeta meta);

        byte[] sign(byte[] message);

        KeyringPairJson toJson(String passphrase);

        boolean verify(byte[] message, byte[] signature);
    }

}
