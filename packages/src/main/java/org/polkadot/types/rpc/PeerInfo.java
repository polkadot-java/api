package org.polkadot.types.rpc;

import org.polkadot.types.Types;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.primitive.Text;
import org.polkadot.types.primitive.U32;
import org.polkadot.types.type.BlockNumber;
import org.polkadot.types.type.Hash;

import java.util.HashMap;
import java.util.Map;

/**
 * @name PeerInfo
 * @description A system peer info indicator, reported back over RPC
 */
public class PeerInfo extends Struct {

    final static Map<String, String> JSON_MAP = new HashMap<>();

    static {
        JSON_MAP.put("bestHash", "best_hash");
        JSON_MAP.put("bestNumber", "best_number");
        JSON_MAP.put("protocolVersion", "protocol_version");
        JSON_MAP.put("peerId", "peer_id");
    }

    public PeerInfo(Object value) {
        super(new Types.ConstructorDef()
                        .add("peerId", Text.class)
                        .add("roles", Text.class)
                        .add("protocolVersion", U32.class)
                        .add("bestHash", Hash.class)
                        .add("bestNumber", BlockNumber.class)
                , value, JSON_MAP);
    }

    /**
     * @description The best block hash for the peer
     */
    public Hash getBestHash() {
        return this.getField("bestHash");
    }

    /**
     * @description The best block hash for the peer
     */
    public BlockNumber getBestNumber() {
        return this.getField("bestNumber");
    }

    /**
     * @description The p2p network id for the peer
     */
    public Text getPeerId() {
        return this.getField("peerId");
    }

    /**
     * @description The index of the peer in our list
     */
    public U32 getProtocolVersion() {
        return this.getField("protocolVersion");
    }

    /**
     * @description The roles of the peer on the network
     */
    public Text getRoles() {
        return this.getField("roles");
    }

}
