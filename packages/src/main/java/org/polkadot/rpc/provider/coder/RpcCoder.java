package org.polkadot.rpc.provider.coder;

import com.alibaba.fastjson.JSON;
import org.polkadot.rpc.provider.Types.JsonRpcError;
import org.polkadot.rpc.provider.Types.JsonRpcRequest;
import org.polkadot.rpc.provider.Types.JsonRpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class RpcCoder {
    private static final Logger logger = LoggerFactory.getLogger(RpcCoder.class);

    private AtomicInteger id = new AtomicInteger(0);

    public Object decodeResponse(JsonRpcResponse response) {
        assert Objects.isNull(response) : "Empty response object received";
        assert "2.0".equals(response.getJsonrpc()) : "Invalid jsonrpc field in decoded object";

        boolean isSubscription = response.getParams() != null && response.getMethod() != null;
        //    assert(isNumber(response.id) || (isSubscription && isNumber(response.params.subscription)), 'Invalid id field in decoded object');
        this.checkError(response.getError());

        assert response.getResult() != null || isSubscription : "No result found in JsonRpc response";
        if (isSubscription) {
            this.checkError(response.getParams().getError());

            return response.getParams().getResult();
        }
        return response.getResult();
    }


    public String encodeJson(String method, List<Object> params) {
        return JSON.toJSONString(this.encodeObject(method, params));
    }

    public JsonRpcRequest encodeObject(String method, List<Object> params) {
        return new JsonRpcRequest(this.id.incrementAndGet(), "2.0", method, params);
    }

    public int getId() {
        return this.id.get();
    }

    private void checkError(JsonRpcError error) {
        if (error != null) {
            String data = error.getData() == null ?
                    "" : "(" + error.getData().substring(0, 10) + ")";
            String msg = String.format("%d : %s%s", error.getCode(), error.getMessage(), data);
            logger.error("{}", msg);
            throw new RuntimeException(msg);
        }

    }
}
