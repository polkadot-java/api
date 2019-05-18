package org.polkadot.rpc.provider;

import java.util.List;

public interface Types {

    class JsonRpcObject {

        private int id;
        private String jsonrpc = "2.0";

        public JsonRpcObject() {

        }

        public JsonRpcObject(int id, String jsonrpc) {
            this.id = id;
            this.jsonrpc = jsonrpc;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getJsonrpc() {
            return jsonrpc;
        }

        public void setJsonrpc(String jsonrpc) {
            this.jsonrpc = jsonrpc;
        }
    }


    class JsonRpcError {

        private int code;
        private String data;
        private String message;

        /*
         code: number,
      data?: number | string,
      message: string
        * */
        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


    class JsonRpcRequest extends JsonRpcObject {

        private String method;
        private List<Object> params;

        public JsonRpcRequest(int id, String jsonRpc, String method, List<Object> params) {
            super(id, jsonRpc);
            this.method = method;
            this.params = params;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public List<Object> getParams() {
            return params;
        }

        public void setParams(List<Object> params) {
            this.params = params;
        }
    }


    class SubscriptionParam {
        private JsonRpcError error;
        private Object result;
        private int subscription;

        public JsonRpcError getError() {
            return error;
        }

        public void setError(JsonRpcError error) {
            this.error = error;
        }

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            this.result = result;
        }

        public int getSubscription() {
            return subscription;
        }

        public void setSubscription(int subscription) {
            this.subscription = subscription;
        }
    }

    class JsonRpcResponse extends JsonRpcObject {
        public JsonRpcResponse() {
        }

        public JsonRpcResponse(int id, String jsonrpc) {
            super(id, jsonrpc);
        }

        private JsonRpcError error;
        private String result;

        ///Subscription
        private String method;
        private SubscriptionParam params;

        public JsonRpcError getError() {
            return error;
        }

        public void setError(JsonRpcError error) {
            this.error = error;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public SubscriptionParam getParams() {
            return params;
        }

        public void setParams(SubscriptionParam params) {
            this.params = params;
        }
    }

}
