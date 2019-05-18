package test.org.polkadot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.onehilltech.promises.Promise;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.polkadot.rpc.provider.IProvider;
import org.polkadot.rpc.provider.coder.RpcCoder;
import org.polkadot.rpc.provider.ws.WsProvider.WsStateAwaiting;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WsProviderDir {

    private WebSocketClient cc;
    private boolean connected = false;

    private Map<Integer, WsStateAwaiting> handlers = new HashMap<>();


    public WsProviderDir(String url) throws URISyntaxException {


        cc = new WebSocketClient(new URI(url)) {

            @Override
            public void onMessage(String message) {
                System.out.println("got: " + message + "\n");
                JSONObject jsonObject = JSON.parseObject(message);
                if (!jsonObject.containsKey("method")) {
                    Integer id = jsonObject.getInteger("id");
                    System.out.println(" get result response id " + id);

                    WsStateAwaiting wsStateAwaiting = handlers.get(id);
                    if (wsStateAwaiting != null) {
                        wsStateAwaiting.callBack.callback(null, jsonObject);
                    }

                } else {
                    String method = jsonObject.getString("method");
                    System.out.println(" get pub response " + method);
                    Integer subId = jsonObject.getJSONObject("params").getInteger("subscription");

                }
            }

            @Override
            public void onOpen(ServerHandshake handshake) {
                System.out.println("You are connected to ChatServer: " + getURI() + "\n");
                WsProviderDir.this.connected = true;
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("You have been disconnected from: " + getURI() + "; Code: " + code + " " + reason + "\n");
            }

            @Override
            public void onError(Exception ex) {
                System.out.println("Exception occured ...\n" + ex + "\n");
                ex.printStackTrace();
            }
        };


    }


    //public void send(String str,) {
    //    cc.send(str);
    //}
    RpcCoder rpcCoder = new RpcCoder();

    public Promise send(String method, List<Object> params, IProvider.SubscriptionHandler subscription) {

        Promise promise = new Promise((handler) -> {
            try {
                String json = rpcCoder.encodeJson(method, params);
                int id = rpcCoder.getId();
                IProvider.CallbackHandler<Exception, Object> callback = (err, result) -> {
                    System.out.println(" call back " + err + " " + result);
                    if (err != null) {
                        handler.reject(err);
                    } else {
                        handler.resolve(result);
                    }
                };

                this.handlers.put(id, new WsStateAwaiting(callback, method, params, subscription));

                WsProviderDir.this.cc.send(json);
            } catch (Exception e) {
                handler.reject(e);
            }

        });

        return promise;


    }

    public Promise subscribe(String type, String method, List<Object> params, IProvider.CallbackHandler cb) {
        return this.send(method, params, new IProvider.SubscriptionHandler(cb, type));
    }


    public void rev() {

    }

    public void connect() {
        cc.connect();
    }


    public WebSocketClient getCc() {
        return cc;
    }

    public void setCc(WebSocketClient cc) {
        this.cc = cc;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }


}
