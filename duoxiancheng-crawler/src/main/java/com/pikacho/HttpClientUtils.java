package com.pikacho;


import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpClientUtils {
    /**
     * http的get请求
     * @param url
     */
    public static String get(String url) {
        return get(url, "UTF-8");
    }

    /**
     * http的get请求，增加请求头参数
     * @param url
     */
    public static String get(String url, String charset) {
        // 创建http的get请求对象
        HttpGet httpGet = new HttpGet(url);
        // 设置请求头参数，将字符编码设置为charset
        return executeRequest(httpGet, charset);
    }

    /**
     * 执行一个http请求，传递HttpGet或HttpPost参数
     */
    public static String executeRequest(HttpUriRequest httpRequest, String charset) {
        // 声明http客户端连接
        CloseableHttpClient httpclient = null;
        if ("https".equals(httpRequest.getURI().getScheme())) {
            // 如果通信协议为https，那么就使用安全的连接ssl
            httpclient = createSSLInsecureClient();
        } else {
            // 否则就是普通的http连接
            httpclient = HttpClients.createDefault();
        }
        String result = "";
        try {
            try {
                // 获得响应
                CloseableHttpResponse response = httpclient.execute(httpRequest);
                HttpEntity entity = null;
                try {
                    entity = response.getEntity();
                    // 获取响应结果，键值对形式
                    result = EntityUtils.toString(entity, charset);
                } finally {
                    EntityUtils.consume(entity);
                    response.close();
                }
            } finally {
                httpclient.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * 创建 SSL连接
     */
    public static CloseableHttpClient createSSLInsecureClient() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (GeneralSecurityException ex) {
            throw new RuntimeException(ex);
        }
    }
}
