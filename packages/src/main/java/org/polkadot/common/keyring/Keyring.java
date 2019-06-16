package org.polkadot.common.keyring;

import com.google.common.primitives.UnsignedBytes;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.polkadot.common.keyring.address.AddressCodec;
import org.polkadot.common.keyring.address.Defaults;
import org.polkadot.common.keyring.pair.Index;
import org.polkadot.common.keyring.pair.Types.PairInfo;
import org.polkadot.example.TestingPairs;
import org.polkadot.utils.Utils;
import org.polkadot.utils.crypto.Nacl;
import org.polkadot.utils.crypto.Schnorrkel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * # @polkadot/keyring
 * <p>
 * ## Overview
 *
 * @name Keyring
 * @summary Keyring management of user accounts
 * @description Allows generation of keyring pairs from a variety of input combinations, such as
 * json object containing account address or public key, account metadata, and account encoded using
 * `addFromJson`, or by providing those values as arguments separately to `addFromAddress`,
 * or by providing the mnemonic (seed phrase) and account metadata as arguments to `addFromMnemonic`.
 * Stores the keyring pairs in a keyring pair dictionary. Removal of the keyring pairs from the keyring pair
 * dictionary is achieved using `removePair`. Retrieval of all the stored pairs via `getPairs` or perform
 * lookup of a pair for a given account address or public key using `getPair`. JSON metadata associated with
 * an account may be obtained using `toJson` accompanied by the account passphrase.
 */
public class Keyring implements Types.KeyringInstance {
    private Pairs pairs;
    private String type;

    public Keyring(Types.KeyringOptions options) {
        if (options.type == null) {
            options.type = org.polkadot.utils.crypto.Types.KeypairType_ED;
        }

        //assert(options && ['ed25519', 'sr25519'].includes(options.type || 'undefined'), `Expected a keyring type of either 'ed25519' or 'sr25519', found '${options.type}`);

        this.pairs = new Pairs();
        this.type = options.type;

        Defaults.prefix = (byte) (Types.Prefix.contains(options.addressPrefix)
                ? options.addressPrefix
                : 42);
    }

    /**
     * @name getPairs
     * @summary Retrieves all account keyring pairs from the Keyring Pair Dictionary
     * @description Returns an array list of all the keyring pair values that are stored in the keyring pair dictionary.
     */
    @Override
    public List<Types.KeyringPair> getPairs() {
        return this.pairs.all();
    }

    /**
     * @name getPublicKeys
     * @summary Retrieves Public Keys of all Keyring Pairs stored in the Keyring Pair Dictionary
     * @description Returns an array list of all the public keys associated with each of the keyring pair values that are stored in the keyring pair dictionary.
     */
    @Override
    public List<byte[]> getPublicKeys() {
        return this.pairs.all().stream()
                .map(e -> e.publicKey())
                .collect(Collectors.toList());
    }

    /**
     * @description Returns the type of the keyring, either ed25519 of sr25519
     */
    @Override
    public String getType() {
        return this.type;
    }


    @Override
    public byte[] decodeAddress(Object encoded, boolean ignoreChecksum) {
        return AddressCodec.decodeAddress(encoded, ignoreChecksum, Defaults.prefix);
    }

    @Override
    public String encodeAddress(byte[] key) {
        return AddressCodec.encodeAddress(key);
    }

    @Override
    public void setAddressPrefix(int prefix) {
        assert Types.Prefix.contains(prefix) : "invalid prefix " + prefix;
        Defaults.prefix = (byte) prefix;
    }

    /**
     * @name addPair
     * @summary Stores an account, given a keyring pair, as a Key/Value (public key, pair) in Keyring Pair Dictionary
     */
    @Override
    public Types.KeyringPair addPair(Types.KeyringPair pair) {
        return this.pairs.add(pair);
    }

    /**
     * @name addFromAddress
     * @summary Stores an account, given an account address, as a Key/Value (public key, pair) in Keyring Pair Dictionary
     * @description Allows user to explicitely provide separate inputs including account address or public key, and optionally
     * the associated account metadata, and the default encoded value as arguments (that may be obtained from the json file
     * of an account backup), and then generates a keyring pair from them that it passes to
     * `addPair` to stores in a keyring pair dictionary the public key of the generated pair as a key and the pair as the associated value.
     */
    @Override
    public Types.KeyringPair addFromAddress(String address, Types.KeyringPairMeta meta, byte[] encoded, String type, boolean ignoreChecksum) {
        byte[] publicKey = this.decodeAddress(address, ignoreChecksum);
        meta = meta == null ? new Types.KeyringPairMeta() : meta;
        PairInfo pairInfo = new PairInfo();
        pairInfo.setPublicKey(publicKey);
        return this.addPair(Index.createPair(type, pairInfo, meta, encoded));
    }

    /**
     * @name addFromJson
     * @summary Stores an account, given JSON data, as a Key/Value (public key, pair) in Keyring Pair Dictionary
     * @description Allows user to provide a json object argument that contains account information (that may be obtained from the json file
     * of an account backup), and then generates a keyring pair from it that it passes to
     * `addPair` to stores in a keyring pair dictionary the public key of the generated pair as a key and the pair as the associated value.
     */
    @Override
    public Types.KeyringPair addFromJson(Types.KeyringPairJson pair, boolean ignoreChecksum) {
        String version = pair.getEncoding().getVersion();
        String[] content = pair.getEncoding().getContent();

        String type = version.equals("0") || !content.getClass().isArray()
                ? this.type
                : content[1];

        return this.addFromAddress(pair.getAddress(), pair.getMeta(), Utils.hexToU8a(pair.getEncoded()), type, ignoreChecksum);
    }


    /**
     * @name addFromMnemonic
     * @summary Stores an account, given a mnemonic, as a Key/Value (public key, pair) in Keyring Pair Dictionary
     * @description Allows user to provide a mnemonic (seed phrase that is provided when account is originally created)
     * argument and a metadata argument that contains account information (that may be obtained from the json file
     * of an account backup), and then generates a keyring pair from it that it passes to
     * `addPair` to stores in a keyring pair dictionary the public key of the generated pair as a key and the pair as the associated value.
     */
    @Override
    public Types.KeyringPair addFromMnemonic(String mnemonic, Types.KeyringPairMeta meta, String type) {
        return this.addFromUri(mnemonic, meta, type);
    }

    /**
     * @name addFromSeed
     * @summary Stores an account, given seed data, as a Key/Value (public key, pair) in Keyring Pair Dictionary
     * @description Stores in a keyring pair dictionary the public key of the pair as a key and the pair as the associated value.
     * Allows user to provide the account seed as an argument, and then generates a keyring pair from it that it passes to
     * `addPair` to store in a keyring pair dictionary the public key of the generated pair as a key and the pair as the associated value.
     */
    @Override
    public Types.KeyringPair addFromSeed(byte[] seed, Types.KeyringPairMeta meta, String type) {
        meta = meta != null ? meta : new Types.KeyringPairMeta();
        type = StringUtils.isBlank(type) ? this.type : type;

        org.polkadot.utils.crypto.Types.Keypair keypair = type.equals(org.polkadot.utils.crypto.Types.KeypairType_SR)
                ? Schnorrkel.schnorrkelKeypairFromSeed(seed)
                : Nacl.naclKeypairFromSeed(seed);

        PairInfo pairInfo = new PairInfo();
        pairInfo.setPublicKey(keypair.getPublicKey());
        pairInfo.setSecretKey(keypair.getSecretKey());
        return this.addPair(Index.createPair(type, pairInfo, meta, null));
    }

    /**
     * @name addFromUri
     * @summary Creates an account via an suri
     * @description Extracts the phrase, path and password from a SURI format for specifying secret keys `<secret>/<soft-key>//<hard-key>///<password>` (the `///password` may be omitted, and `/<soft-key>` and `//<hard-key>` maybe repeated and mixed). The secret can be a hex string, mnemonic phrase or a string (to be padded)
     */
    @Override
    public Types.KeyringPair addFromUri(String suri, Types.KeyringPairMeta meta, String type) {
        return this.addPair(
                this.createFromUri(suri, meta, type)
        );
    }

    /**
     * @name createFromUri
     * @summry Creates a Keypair from an suri
     * @description This creates a pair from the suri, but does not add it to the keyring
     */
    @Override
    public Types.KeyringPair createFromUri(String _suri, Types.KeyringPairMeta meta, String type) {
        //TODO only for api test
        Pair<byte[], byte[]> keys = TestingPairs.getKeys(_suri.replace("//", ""));

        PairInfo pairInfo = new PairInfo();
        pairInfo.setPublicKey(keys.getLeft());
        pairInfo.setSecretKey(keys.getRight());
        Types.KeyringPair pair = Index.createPair(type, pairInfo, meta, null);
        return pair;
    }


    /**
     * @name getPair
     * @summary Retrieves an account keyring pair from the Keyring Pair Dictionary, given an account address
     * @description Returns a keyring pair value from the keyring pair dictionary by performing
     * a key lookup using the provided account address or public key (after decoding it).
     */
    @Override
    public Types.KeyringPair getPair(String address) {
        return this.pairs.get(address);
    }

    /**
     * @name removePair
     * @description Deletes the provided input address or public key from the stored Keyring Pair Dictionary.
     */
    @Override
    public void removePair(String address) {
        this.pairs.remove(address);
    }

    /**
     * @name toJson
     * @summary Returns a JSON object associated with the input argument that contains metadata assocated with an account
     * @description Returns a JSON object containing the metadata associated with an account
     * when valid address or public key and when the account passphrase is provided if the account secret
     * is not already unlocked and available in memory. Note that in [Polkadot-JS Apps](https://github.com/polkadot-js/apps) the user
     * may backup their account to a JSON file that contains this information.
     */
    @Override
    public Types.KeyringPairJson toJson(String address, String passphrase) {
        return this.pairs.get(address).toJson(passphrase);
    }


}
