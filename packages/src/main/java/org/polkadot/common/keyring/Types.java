package org.polkadot.common.keyring;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public interface Types {

    class KeyringOptions {
        int addressPrefix;
        String type;

        public KeyringOptions(String type) {
            this.type = type;
        }

        public int getAddressPrefix() {
            return addressPrefix;
        }

        public String getType() {
            return type;
        }
    }


    Set Prefix = Sets.newHashSet(0, 1, 3, 42, 43, 68, 69);

    //export type KeypairType = 'ed25519' | 'sr25519';

    class KeyringPairMeta extends LinkedHashMap<String, Object> {

    }

    //  export type KeyringPair$Meta = {
    //[index: string]: any
    //  };
    //export type KeyringPair$JsonVersion = '0' | '1' | '2';
    //enum KeyringPairJsonVersion {
    //    v0, v1, v2
    //}
    List<String> KeyringPairJsonVersion = Lists.newArrayList("0", "1", "2");

    class KeyringPairJsonEncoding {
        String[] content;
        String type;
        String version;

        public String[] getContent() {
            return content;
        }

        public void setContent(String[] content) {
            this.content = content;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
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

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getEncoded() {
            return encoded;
        }

        public void setEncoded(String encoded) {
            this.encoded = encoded;
        }

        public KeyringPairJsonEncoding getEncoding() {
            return encoding;
        }

        public void setEncoding(KeyringPairJsonEncoding encoding) {
            this.encoding = encoding;
        }

        public KeyringPairMeta getMeta() {
            return meta;
        }

        public void setMeta(KeyringPairMeta meta) {
            this.meta = meta;
        }
    }

    //export type KeyringPair$Json = {
    //    address: string,
    //            encoded: string,
    //            encoding: KeyringPair$JsonEncoding,
    //            meta: KeyringPair$Meta
    //};

    interface KeyringPair {
        String getType();

        String address();

        void decodePkcs8(String passphrase, byte[] encoded);

        byte[] encodePkcs8(String passphrase);

        KeyringPairMeta getMeta();

        boolean isLocked();

        void lock();

        byte[] publicKey();

        void setMeta(KeyringPairMeta meta);

        byte[] sign(byte[] message);

        KeyringPairJson toJson(String passphrase);

        boolean verify(byte[] message, byte[] signature);
    }

    interface KeyringPairs {
        KeyringPair add(KeyringPair pair);

        List<KeyringPair> all();

        //  get: (address: string | Uint8Array) => KeyringPair;
        KeyringPair get(String address);

        void remove(String address);
    }

    interface KeyringInstance {
        List<KeyringPair> getPairs();

        List<byte[]> getPublicKeys();

        String getType();

        byte[] decodeAddress(Object encoded, boolean ignoreChecksum);

        String encodeAddress(byte[] key);

        //export type Prefix = 0 | 1 | 3 | 42 | 43 | 68 | 69;
        void setAddressPrefix(int prefix);

        KeyringPair addPair(KeyringPair pair);

        KeyringPair addFromAddress(String address, KeyringPairMeta meta, byte[] encoded, String type, boolean ignoreChecksum);

        KeyringPair addFromJson(KeyringPairJson pair, boolean ignoreChecksum);

        KeyringPair addFromMnemonic(String mnemonic, KeyringPairMeta meta, String type);

        KeyringPair addFromSeed(byte[] seed, KeyringPairMeta meta, String type);

        KeyringPair addFromUri(String suri, KeyringPairMeta meta, String type);

        KeyringPair createFromUri(String suri, KeyringPairMeta meta, String type);

        KeyringPair getPair(String address);

        void removePair(String address);

        KeyringPairJson toJson(String address, String passphrase);
    }

}
