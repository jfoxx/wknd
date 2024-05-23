package com.adobe.aem.guides.wknd.core.models;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Set;

/**
 * Created by Douglas Prevelige on 3/18/2024.
 * Non-production code for POC purposes only.
 */
public class RESTJSON {

    private static final Logger log = LoggerFactory.getLogger(RESTJSON.class);

    public static String callGet(String url, Map<String,String> headers) {

        String responseString = "{\"error\": \"No response\"}";

        CloseableHttpClient httpclient = createAcceptSelfSignedCertificateClient();
        HttpGet httpGet = new HttpGet(url);
        if (headers != null && headers.size()>0) {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                httpGet.addHeader(key,headers.get(key));
            }
        }

        try {
            CloseableHttpResponse response = httpclient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int resp = statusLine.getStatusCode();
            log.info("GET response code: " + resp);

            HttpEntity respEntity = response.getEntity();
            if (respEntity != null && resp <400) {
                responseString = EntityUtils.toString(respEntity);
                log.info("respString: " +responseString);
            }
            //logHeaders(response.getAllHeaders());
        } catch (UnsupportedEncodingException e) {
            log.error("Error!", e);
        } catch (IOException e) {
            log.error("Error!", e);
        }
        return responseString;
    }

    private static CloseableHttpClient createAcceptSelfSignedCertificateClient() {

        // use the TrustSelfSignedStrategy to allow Self Signed Certificates
        SSLConnectionSocketFactory connectionFactory = null;
        try {
            SSLContext sslContext = SSLContextBuilder
                    .create()
                    .loadTrustMaterial(new TrustSelfSignedStrategy())
                    .build();

            // we can optionally disable hostname verification.
            // if you don't want to further weaken the security, you don't have to include this.
            HostnameVerifier allowAllHosts = new NoopHostnameVerifier();

            // create an SSL Socket Factory to use the SSLContext with the trust self signed certificate strategy
            // and allow all hosts verifier.
            connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error!", e);
        } catch (KeyManagementException e) {
            log.error("Error!", e);
        } catch (KeyStoreException e) {
            log.error("Error!", e);
        }

        // finally create the HttpClient using HttpClient factory methods and assign the ssl socket factory
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(60000)
                .setSocketTimeout(60000)
                .build();
        return HttpClients
                .custom()
                .setSSLSocketFactory(connectionFactory)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }
}