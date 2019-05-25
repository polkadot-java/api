package org.polkadot.rpc.provider.http;

/**
 * # @polkadot/rpc-provider/https
 *
 * HttpProvider
 *
 * The HTTP Provider allows sending requests using HTTP to a HTTP RPC server TCP port. It does not support subscriptions so you won't be able to listen to events such as new blocks or balance changes. It is usually preferrable using the [[WsProvider]].
 *
 * **Example**
 *
 * ```java
 * import org.polkadot.rpc.provider.http.HttpProvider;
 *
 * HttpProvider provider = new HttpProvider('http://127.0.0.1:9933');
 * ```
 *
 * @see [[WsProvider]]
 */
public class HttpProvider {
}
