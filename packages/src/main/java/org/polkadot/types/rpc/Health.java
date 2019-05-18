package org.polkadot.types.rpc;

import org.polkadot.types.Types;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.primitive.Bool;
import org.polkadot.types.primitive.USize;
import org.polkadot.utils.MapUtils;

import java.util.Map;

/**
 * @name Health
 * @description A system health indicator, reported back over RPC
 */
public class Health extends Struct {
    public static Map<String, String> JSON_MAP = MapUtils.ofMap(
            "isSyncing", "is_syncing",
            "shouldHavePeers", "should_have_peers");


    public Health(Object value) {
        super(new Types.ConstructorDef()
                        .add("peers", USize.class)
                        .add("isSyncing", Bool.class)
                        .add("shouldHavePeers", Bool.class)
                , value);
    }


    /**
     * @description The sync status
     */
    public Bool isSyncing() {
        return this.getField("isSyncing");
    }

    /**
     * @description The numbers of peers as [[U32]]
     */
    public USize getPeers() {
        return this.getField("peers");
    }

    /**
     * @description Should this node have peers (not active on --dev)
     */
    public Bool shouldHavePeers() {
        return this.getField("shouldHavePeers");
    }
}
