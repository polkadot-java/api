package org.polkadot.types.rpc;

/**
 * @name NetworkState
 * @description Wraps the properties retrieved from the chain via the `system.network_state` RPC call.
 */
public class NetworkState extends Json {
    public NetworkState(Object value) {
        super(value);
    }
}
