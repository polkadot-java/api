package org.polkadot.rpc.provider.ws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.onehilltech.promises.Promise;
import org.apache.commons.lang3.StringUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.polkadot.common.EventEmitter;
import org.polkadot.common.ExecutorsManager;
import org.polkadot.rpc.provider.Constants;
import org.polkadot.rpc.provider.IProvider;
import org.polkadot.rpc.provider.IWsProvider;
import org.polkadot.rpc.provider.Types;
import org.polkadot.rpc.provider.Types.JsonRpcResponse;
import org.polkadot.rpc.provider.coder.RpcCoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


/**
 * # @polkadot/rpc-provider/ws
 *
 * @name WsProviderDir
 * @description The WebSocket Provider allows sending requests using WebSocket to a WebSocket RPC server TCP port. Unlike the [[HttpProvider]], it does support subscriptions and allows listening to events such as new blocks or balance changes.
 * @example <BR>
 * <p>
 * ```javascript
 * import Api from '@polkadot/api/promise';
 * import WsProviderDir from '@polkadot/rpc-provider/ws';
 * <p>
 * const provider = new WsProviderDir('ws://127.0.0.1:9944');
 * const api = new Api(provider);
 * ```
 * @see [[HttpProvider]]
 */
public class WsProvider implements IWsProvider {

    private static final Map<String, String> ALIASSES = new HashMap<>();

    static {
        ALIASSES.put("chain_finalisedHead", "chain_finalizedHead");
    }


    public static class WsStateAwaiting<T> {
        public CallbackHandler<Throwable, T> callBack;
        public String method;
        public List<Object> params;
        public SubscriptionHandler subscription;

        public WsStateAwaiting(CallbackHandler<Throwable, T> callBack, String method, List<Object> params, SubscriptionHandler subscription) {
            this.callBack = callBack;
            this.method = method;
            this.params = params;
            this.subscription = subscription;
        }
    }

    static class WsStateSubscription extends SubscriptionHandler {
        String method;
        List<Object> params;

        public WsStateSubscription(String method, List<Object> params) {
            this.method = method;
            this.params = params;
        }

        public WsStateSubscription(CallbackHandler<Object, Object> callBack, String type, String method, List<Object> params) {
            super(callBack, type);
            this.method = method;
            this.params = params;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(WsProvider.class);

    private boolean isConnected;
    private boolean autoConnect;

    private EventEmitter eventemitter = new EventEmitter();

    private RpcCoder coder = new RpcCoder();
    private String endpoint;

    private Map<Integer, WsStateAwaiting> handlers = new ConcurrentHashMap<>();

    private LinkedList<String> queued = new LinkedList<>();


    private Map<String, WsStateSubscription> subscriptions = new ConcurrentHashMap<>();

    private Map<String, JsonRpcResponse> waitingForId = new ConcurrentHashMap<>();

    private WebSocketClient webSocket;

    public WsProvider() {
        this(Constants.WS_URL, true);
    }

    public WsProvider(String endpoint) {
        this(endpoint, true);
    }

    public WsProvider(String endpoint, boolean autoConnect) {

        //assert(/^(wss|ws):\/\//.test(endpoint), `Endpoint should start with 'ws://', received '${endpoint}'`);
        if (Pattern.matches("^(wss|ws):\\/\\/", endpoint)) {
            throw new RuntimeException("Endpoint should start with 'ws://', received " + endpoint);
        }
        this.endpoint = endpoint;
        this.autoConnect = autoConnect;
        //this.coder = new RpcCoder();


        if (autoConnect) {
            this.connect();
        }
    }

    /**
     * @summary Manually connect
     * @description The [[WsProviderDir]] connects automatically by default, however if you decided otherwise, you may
     * connect manually using this method.
     */
    @Override
    public void connect() {

        try {
            this.webSocket = new WebSocketClient(new URI(this.endpoint)) {
                WsProvider wsProvider = WsProvider.this;

                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    logger.info("WebSocket onOpen: {}", getURI());

                    wsProvider.isConnected = true;
                    wsProvider.emit(ProviderInterfaceEmitted.connected);
                    wsProvider.sendQueue();
                    wsProvider.resubscribe();
                }

                @Override
                public void onMessage(String message) {
                    logger.info("WebSocket onMessage:{}", message);

                    JsonRpcResponse response = JSONObject.parseObject(message, JsonRpcResponse.class);
                    if (StringUtils.isEmpty(response.getMethod())) {
                        wsProvider.onSocketMessageResult(response);
                    } else {
                        wsProvider.onSocketMessageSubscribe(response);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {

                    if (wsProvider.autoConnect) {
                        logger.error("disconnected from ${} code: '${}' reason: '${}'",
                                this.getURI(), code, reason);
                    }

                    wsProvider.isConnected = false;
                    wsProvider.emit(ProviderInterfaceEmitted.disconnected);

                    if (wsProvider.autoConnect) {
                        ExecutorsManager.schedule(() -> wsProvider.connect(), 1000, TimeUnit.MILLISECONDS);
                    }
                }

                @Override
                public void onError(Exception ex) {
                    logger.error(" socket error ", ex);
                    wsProvider.emit(ProviderInterfaceEmitted.error, ex);
                }
            };
            this.webSocket.connect();
        } catch (
                Exception e) {
            logger.error("connect error", e);
        }

    }

    private void emit(EventEmitter.EventType type, Object... args) {
        this.eventemitter.emit(type, args);
    }

    private void onSocketMessageSubscribe(JsonRpcResponse response) {
        String method = ALIASSES.get(response.getMethod());
        if (method == null) {
            method = response.getMethod();
        }

        String subId = method + "::" + response.getParams().getSubscription();

        logger.info("handling: response =', {}, 'subscription =', {}", response, subId);

        WsStateSubscription handler = this.subscriptions.get(subId);

        if (handler == null) {
            // store the JSON, we could have out-of-order subid coming in
            this.waitingForId.put(subId, response);
            logger.info("Unable to find handler for subscription=${}", subId);
            return;
        }

        // housekeeping
        this.waitingForId.remove(subId);

        try {
            Object result = this.coder.decodeResponse(response);
            handler.getCallBack().callback(null, result);
        } catch (Exception e) {
            handler.getCallBack().callback(e, null);
        }
    }

    private void onSocketMessageResult(JsonRpcResponse response) {
        logger.info("handling response {}, {}", response, response.getId());

        WsStateAwaiting handler = this.handlers.get(response.getId());
        if (handler == null) {
            logger.error("Unable to find handler for id={}", response.getId());
            return;
        }

        try {
            Object result = this.coder.decodeResponse(response);

            // first send the result - in case of subs, we may have an update
            // immediately if we have some queued results already
            handler.callBack.callback(null, result);

            SubscriptionHandler subscription = handler.subscription;
            if (subscription != null) {
                String subId = subscription.getType() + "::" + result;
                this.subscriptions.put(subId, new WsStateSubscription(subscription.getCallBack(), subscription.getType(), handler.method, handler.params));

                // if we have a result waiting for this subscription already
                if (this.waitingForId.containsKey(subId)) {
                    this.onSocketMessageSubscribe(this.waitingForId.get(subId));
                }
            }

        } catch (Exception e) {
            handler.callBack.callback(e, null);
        }
        this.handlers.remove(response.getId());
    }

    private void sendQueue() {
        while (queued.peek() != null) {
            String head = queued.poll();
            try {
                this.webSocket.send(head);
            } catch (Exception e) {
                logger.error(" sendQueue error {}", head, e);
            }
        }
    }

    /**
     * @param method       The RPC methods to execute
     * @param params       Encoded paramaters as appliucable for the method
     * @param subscription Subscription details (internally used)
     * @summary Send JSON data using WebSockets to configured HTTP Endpoint or queue.
     */
    @Override
    public Promise<String> send(String method, List<Object> params, SubscriptionHandler subscription) {

        return new Promise((handler) -> {
            try {

                Types.JsonRpcRequest jsonRpcRequest = this.coder.encodeObject(method, params);
                String json = JSON.toJSONString(jsonRpcRequest);

                int id = jsonRpcRequest.getId();
                CallbackHandler<Exception, Object> callback = (err, result) -> {
                    if (err != null) {
                        handler.reject(err);
                    } else {
                        handler.resolve(result);
                    }
                };

                logger.info("call {}, {}, {}, {}", method, params, json, subscription);

                this.handlers.put(id, new WsStateAwaiting(callback, method, params, subscription));
                if (this.isConnected() && this.webSocket != null) {
                    this.webSocket.send(json);
                } else {
                    this.queued.set(id, json);
                }
            } catch (Exception e1) {
                handler.reject(e1);
            }

        });
    }

    private void resubscribe() {
        Map<String, WsStateSubscription> subscriptions = new HashMap<>(this.subscriptions);
        this.subscriptions.clear();

        for (WsStateSubscription subscription : subscriptions.values()) {

            // only re-create subscriptions which are not in author (only area where
            // transactions are created, i.e. submissions such as 'author_submitAndWatchExtrinsic'
            // are not included (and will not be re-broadcast)
            if (subscription.getType().startsWith("author_")) {
                continue;
            }

            try {
                Promise<String> subscribe = this.subscribe(subscription.getType(), subscription.method, subscription.params, subscription.getCallBack());
                subscribe.then((String subscribeId) -> {
                    logger.info(" resubscribe {}", subscribeId);
                    return null;
                });
            } catch (Exception e) {
                logger.error("resubscribe error {}", subscription, e);
            }
        }

    }


    /**
     * @summary `true` when this provider supports subscriptions
     */
    @Override
    public boolean isHasSubscriptions() {
        return true;
    }

    /**
     * @description Returns a clone of the object
     */
    @Override
    public IProvider clone() {
        return new WsProvider(this.endpoint);
    }

    /**
     * @description Manually disconnect from the connection, clearing autoconnect logic
     */
    @Override
    public void disconnect() {
        if (this.webSocket == null) {
            throw new RuntimeException("Cannot disconnect on a non-open websocket");
        }
        // switch off autoConnect, we are in manual mode now
        this.autoConnect = false;
        // 1000 - Normal closure; the connection successfully completed
        this.webSocket.close(1000);
        this.webSocket = null;
    }


    /**
     * @return {boolean} true if connected
     * @summary Whether the node is connected or not.
     */
    @Override
    public boolean isConnected() {
        return this.isConnected;
    }

    /**
     * @param {ProviderInterface$Emitted} type Event
     * @param {ProviderInterface$EmitCb}  sub  Callback
     * @summary Listens on events after having subscribed using the [[subscribe]] function.
     */
    @Override
    public void on(ProviderInterfaceEmitted emitted, EventEmitter.EventListener cb) {
        this.eventemitter.on(emitted, cb);
    }

    /**
     * @param {string}                     type     Subscription type
     * @param {string}                     method   Subscription method
     * @param {Array<any>}                 params   Parameters
     * @param {ProviderInterface$Callback} callback Callback
     * @return {Promise<number>}                     Promise resolving to the dd of the subscription you can use with [[unsubscribe]].
     * @name subscribe
     * @summary Allows subscribing to a specific event.
     * @example <BR>
     * <p>
     * ```javascript
     * const provider = new WsProviderDir('ws://127.0.0.1:9944');
     * const rpc = new Rpc(provider);
     * <p>
     * rpc.state.subscribeStorage([[storage.balances.freeBalance, <Address>]], (_, values) => {
     * console.log(values)
     * }).then((subscriptionId) => {
     * console.log('balance changes subscription id: ', subscriptionId)
     * })
     * ```
     */
    @Override
    public Promise<String> subscribe(String type, String method, List<Object> params, CallbackHandler cb) {
        return this.send(method, params, new SubscriptionHandler(cb, type));
    }

    /**
     * @summary Allows unsubscribing to subscriptions made with [[subscribe]].
     */
    @Override
    public Promise<String> unsubscribe(String type, String method, int id) {
        String subscription = type + "::" + id;

        // FIXME This now could happen with re-subscriptions. The issue is that with a re-sub
        // the assigned id now does not match what the API user originally received. It has
        // a slight complication in solving - since we cannot rely on the send id, but rather
        // need to find the actual subscription id to map it

        if (this.subscriptions.get(subscription) == null) {
            logger.info("Unable to find active subscription={}", subscription);
            return Promise.reject(new RuntimeException("Unable to find active subscription=" + subscription));
        }

        this.subscriptions.remove(subscription);
        //TODO 2019-05-09 21:37
        return this.send(method, Lists.newArrayList(id), null);
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean isAutoConnect() {
        return autoConnect;
    }

    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
    }

    public RpcCoder getCoder() {
        return coder;
    }

    public void setCoder(RpcCoder coder) {
        this.coder = coder;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Queue<String> getQueued() {
        return queued;
    }

    public void setQueued(LinkedList<String> queued) {
        this.queued = queued;
    }

    public Map<String, JsonRpcResponse> getWaitingForId() {
        return waitingForId;
    }

    public void setWaitingForId(Map<String, JsonRpcResponse> waitingForId) {
        this.waitingForId = waitingForId;
    }

    public WebSocket getWebSocket() {
        return webSocket;
    }

    public void setWebSocket(WebSocketClient webSocket) {
        this.webSocket = webSocket;
    }
}
