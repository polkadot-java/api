package org.polkadot.rpc.provider.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.onehilltech.promises.Promise;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.polkadot.common.EventEmitter;
import org.polkadot.common.HttpClient;
import org.polkadot.rpc.provider.IProvider;
import org.polkadot.rpc.provider.Types;
import org.polkadot.rpc.provider.coder.RpcCoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * # @polkadot/rpc-provider/https
 * HttpProvider
 * The HTTP Provider allows sending requests using HTTP to a HTTP RPC server TCP port. It does not support subscriptions so you won't be able to listen to events such as new blocks or balance changes. It is usually preferrable using the WsProvider.
 *
 * **Example**  
 * 
 * ```java
 * import org.polkadot.rpc.provider.http.HttpProvider;
 * 
 * HttpProvider provider = new HttpProvider('http://127.0.0.1:9933');
 * ```
 *
 * @see org.polkadot.rpc.provider.ws.WsProvider
 */
public class HttpProvider implements IProvider {

    private static final Logger logger = LoggerFactory.getLogger(HttpProvider.class);
    static final String ERROR_SUBSCRIBE = "HTTP Provider does not have subscriptions, use WebSockets instead";

    private RpcCoder coder = new RpcCoder();
    private String endpoint;

    public HttpProvider(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * `true` when this provider supports subscriptions
     */
    @Override
    public boolean isHasSubscriptions() {
        return false;
    }

    /**
     * Returns a clone of the object
     */
    @Override
    public IProvider clone() {
        throw new UnsupportedOperationException("Unimplemented");
    }

    /**
     * Manually disconnect from the connection
     */
    @Override
    public void disconnect() {
        //noop
    }

    /**
     * @return {boolean} true if connected
     * Whether the node is connected or not.
     */
    @Override
    public boolean isConnected() {
        return true;
    }

    /**
     * Events are not supported with the HttpProvider, see {@link org.polkadot.rpc.provider.ws.WsProvider}.
     * HTTP Provider does not have 'on' emitters. WebSockets should be used instead.
     */
    @Override
    public void on(ProviderInterfaceEmitted emitted, EventEmitter.EventListener cb) {
        logger.error("HTTP Provider does not have 'on' emitters, use WebSockets instead");
    }

    /**
     * Send HTTP POST Request with Body to configured HTTP Endpoint.
     */
    @Override
    public Promise<String> send(String method, List<Object> params, SubscriptionHandler subscriptionHandler) {
        return new Promise((handler) -> {
            try {
                Types.JsonRpcRequest jsonRpcRequest = this.coder.encodeObject(method, params);
                String body = JSON.toJSONString(jsonRpcRequest);

                HttpClient.HeadOptions options = HttpClient.HeadOptions.build()
                        .setContentType("application/json")
                        .setHeader(HttpHeaders.ACCEPT, "application/json")
                        .setHeader(HttpHeaders.CONTENT_LENGTH, body.length() + "");
                HttpClient.HttpResp response = HttpClient.post(this.endpoint, body, options);

                assert response.getStatus() == HttpStatus.SC_OK : "[" + response.getStatus() + "]: " + response.getBody();

                Types.JsonRpcResponse jsonRpcResponse = JSONObject.parseObject(response.getBody(), Types.JsonRpcResponse.class);

                Object result = this.coder.decodeResponse(jsonRpcResponse);
                handler.resolve(result);
            } catch (Exception e1) {
                handler.reject(e1);
            }

        });
    }


    /**
     * Subscriptions are not supported with the HttpProvider, see {@link org.polkadot.rpc.provider.ws.WsProvider}.
     */
    @Override
    public Promise<String> subscribe(String type, String method, List<Object> params, CallbackHandler cb) {
        logger.error(ERROR_SUBSCRIBE);
        throw new UnsupportedOperationException(ERROR_SUBSCRIBE);
    }

    /**
     * Subscriptions are not supported with the HttpProvider, see {@link org.polkadot.rpc.provider.ws.WsProvider}.
     */
    @Override
    public Promise<String> unsubscribe(String type, String method, int id) {
        logger.error(ERROR_SUBSCRIBE);
        throw new UnsupportedOperationException(ERROR_SUBSCRIBE);
    }
}
