package org.polkadot.common;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public final class HttpClient {

    public static HttpResp get(String url, Params params, HeadOptions options) throws Exception {
        if (options == null) {
            options = HeadOptions.build();
        }
        String sendUrl = buildUrl(url, params);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(sendUrl);
            get.setConfig(options.getRequestConfig());
            options.getHeaders().forEach(header -> get.addHeader(header));
            final HeadOptions o = options;
            return client.execute(get, response -> {
                int status = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                String body = entity != null ? EntityUtils.toString(entity, o.encoding) : null;
                return new HttpResp(status, body);
            });
        }
    }

    public static HttpResp post(String url, Params params, HeadOptions options) throws Exception {
        if (options == null) {
            options = HeadOptions.build();
        }
        String sendUrl = buildUrl(url, params);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(sendUrl);
            if (params != null) {
                HttpEntity entity = params.toEntity(options.getHeaderValue(HttpHeaders.CONTENT_TYPE));
                post.setEntity(entity);
            }

            post.setConfig(options.getRequestConfig());
            options.getHeaders().forEach(header -> post.addHeader(header));
            final HeadOptions o = options;
            return client.execute(post, response -> {
                int status = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                String body = entity != null ? EntityUtils.toString(entity, o.encoding) : null;
                return new HttpResp(status, body);
            });
        }
    }

    public static HttpResp post(String url, String content, HeadOptions options) throws Exception {
        if (options == null) {
            options = HeadOptions.build();
        }
        String sendUrl = url;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(sendUrl);
            StringEntity postingString = new StringEntity(content, "utf-8");
            post.setEntity(postingString);
            post.setConfig(options.getRequestConfig());
            options.getHeaders().forEach(header -> post.addHeader(header));
            final HeadOptions o = options;
            return client.execute(post, response -> {
                int status = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                String body = entity != null ? EntityUtils.toString(entity, o.encoding) : null;
                return new HttpResp(status, body);
            });
        }
    }


    public static HttpResp put(String url, Params params, HeadOptions options) throws Exception {
        if (options == null) {
            options = HeadOptions.build();
        }
        String sendUrl = buildUrl(url, params);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPut put = new HttpPut(sendUrl);
            if (params != null) {
                HttpEntity entity = params.toEntity(options.getHeaderValue(HttpHeaders.CONTENT_TYPE));
                put.setEntity(entity);
            }
            put.setConfig(options.getRequestConfig());
            options.getHeaders().forEach(header -> put.addHeader(header));
            final HeadOptions o = options;
            return client.execute(put, response -> {
                int status = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                String body = entity != null ? EntityUtils.toString(entity, o.encoding) : null;
                return new HttpResp(status, body);
            });
        }
    }

    public static HttpResp delete(String url, Params params, HeadOptions options) throws Exception {
        if (options == null) {
            options = HeadOptions.build();
        }
        String sendUrl = buildUrl(url, params);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpDelete delete = new HttpDelete(sendUrl);
            delete.setConfig(options.getRequestConfig());
            options.getHeaders().forEach(header -> delete.addHeader(header));
            final HeadOptions o = options;
            return client.execute(delete, response -> {
                int status = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                String body = entity != null ? EntityUtils.toString(entity, o.encoding) : null;
                return new HttpResp(status, body);
            });
        }
    }

    private static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            return str;
        }
    }

    private static String buildUrl(String url, Params p) {
        if (p == null || p.isEmpty()) {
            return url;
        }
        if (url.contains("?")) {
            return url + "&" + p.httpBuildQuery();
        } else {
            return url + "?" + p.httpBuildQuery();
        }
    }

    public static class HeadOptions {
        private int connectTimeout = 3000;
        private int readTimeout = 10000;
        private String encoding = "UTF-8";
        private List<Header> headers;

        private HeadOptions() {
            headers = new ArrayList<>();
        }

        public static HeadOptions build() {
            return new HeadOptions();
        }

        public HeadOptions setEncoding(String encoding) {
            this.encoding = encoding;
            return this;
        }

        public HeadOptions setHeader(String header, String content) {
            headers.add(new BasicHeader(header, content));
            return this;
        }

        public HeadOptions setContentType(String contentType) {
            return setHeader(HttpHeaders.CONTENT_TYPE, contentType);
        }

        public HeadOptions setAuthorization(String authorization) {
            return setHeader(HttpHeaders.AUTHORIZATION, authorization);
        }

        public String getHeaderValue(String header) {
            for (Header h : headers) {
                if (StringUtils.equals(h.getName(), header)) {
                    return h.getValue();
                }
            }
            return null;
        }

        public List<Header> getHeaders() {
            return Collections.unmodifiableList(headers);
        }

        public HeadOptions setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public HeadOptions setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public RequestConfig getRequestConfig() {
            RequestConfig config = RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(readTimeout).build();
            return config;
        }
    }

    public static class Params {
        private Map<String, Object> p;

        private Params() {
            p = new HashMap<>();
        }

        public static Params build() {
            return new Params();
        }

        public Params addParam(String key, Object val) {
            if (StringUtils.isBlank(key) || val == null) {
                return this;
            }

            this.p.put(key, val);
            return this;
        }

        public String httpBuildQuery() {
            List<String> queries = new ArrayList<>();
            for (Map.Entry<String, Object> e : p.entrySet()) {
                queries.add(e.getKey() + "=" + HttpClient.urlEncode(String.valueOf(e.getValue())));
            }
            return StringUtils.join(queries, "&");
        }

        public boolean isEmpty() {
            return this.p.isEmpty();
        }

        public HttpEntity toEntity(String contentType) throws UnsupportedEncodingException {
            if (StringUtils.equals(contentType, "application/json")) {
                JSONObject jsonObject = new JSONObject();
                for (Map.Entry<String, Object> e : p.entrySet()) {
                    jsonObject.put(e.getKey(), e.getValue());
                }
                return new StringEntity(jsonObject.toString());
            } else {
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                for (Map.Entry<String, Object> e : p.entrySet()) {
                    nameValuePairs.add(new BasicNameValuePair(e.getKey(), String.valueOf(e.getValue())));
                }
                return new UrlEncodedFormEntity(nameValuePairs);
            }
        }
    }

    public static class HttpResp {
        private int status;
        private String body;

        public HttpResp(int status, String body) {
            this.status = status;
            this.body = body;
        }

        public int getStatus() {
            return status;
        }

        public String getBody() {
            return body;
        }

        @Override
        public String toString() {
            return "HttpResp{" + "status=" + status + ", body='" + body + '\'' + '}';
        }
    }
}
