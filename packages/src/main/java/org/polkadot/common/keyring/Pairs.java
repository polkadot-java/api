package org.polkadot.common.keyring;

import com.google.common.collect.Lists;
import org.polkadot.common.keyring.address.AddressCodec;
import org.polkadot.utils.Utils;

import java.util.LinkedHashMap;
import java.util.List;

public class Pairs implements Types.KeyringPairs {

//TODO 2019-05-23 11:26 byte[] to string
    public static class KeyringPairMap extends LinkedHashMap<byte[], Types.KeyringPair> {
    }

    private KeyringPairMap map;

    public Pairs() {
        map = new KeyringPairMap();
    }

    @Override
    public Types.KeyringPair add(Types.KeyringPair pair) {
        // @ts-ignore we use coercion :(
        this.map.put(pair.publicKey(), pair);
        return pair;
    }

    @Override
    public List<Types.KeyringPair> all() {
        return Lists.newArrayList(this.map.values());
    }

    @Override
    public Types.KeyringPair get(String address) {
        // @ts-ignore we use coercion :(
        byte[] key = AddressCodec.decodeAddress(address);
        Types.KeyringPair pair = this.map.get(key);

        if (pair == null) {
            String formatted = Utils.isU8a(address) || Utils.isHex(address)
                    ? Utils.u8aToHex(Utils.u8aToU8a(address))
                    : address;
            throw new RuntimeException("Unable to retrieve keypair" + formatted);
        }

        return pair;
    }

    @Override
    public void remove(String address) {
        this.map.remove(AddressCodec.decodeAddress(address));
    }
}
