package com.ruoyi.common.utils.aliyun;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.core.http.HttpClient;
import com.aliyun.core.http.HttpMethod;
import com.aliyun.core.http.ProxyOptions;
import com.aliyun.httpcomponent.httpclient.ApacheAsyncHttpClientBuilder;
import com.aliyun.sdk.service.dm20151123.models.*;
import com.aliyun.sdk.service.dm20151123.*;
import com.google.gson.Gson;
import darabonba.core.RequestConfiguration;
import darabonba.core.client.ClientOverrideConfiguration;
import darabonba.core.utils.CommonUtil;
import darabonba.core.TeaPair;

//import javax.net.ssl.KeyManager;
//import javax.net.ssl.X509TrustManager;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.io.*;

public class SendEmail {

    private static final String ALIBABA_CLOUD_ACCESS_KEY_ID = "LTAI5tPJpcvuvZgZZcSXmAdD";
    private static final String ALIBABA_CLOUD_ACCESS_KEY_SECRET = "4sQHaxIX8QFUpXcRhng1RNe1AlccZe";
    private static final String ALIBABA_EMAIL_ACCOUNT_NAME = "mail@notify.MihootCRM.cn";
    private static final Integer ALIBABA_EMAIL_ADDRESS_TYPE = 1;
    private static final Boolean ALIBABA_EMAIL_REPLY_TO_ADDRESS = false;


    public static void main(String[] args) throws Exception {
        SingleSendMailSample(
                "minaction@outlook.com",
                "This is from My API",
                "<h1>Test</h1>");
    }
    /**
     * SingleSendMail - 发送单条邮件
     */
    public static void SingleSendMailSample(
                                              String toAddress,
                                              String subject,
                                              String htmlBody) throws Exception {

        // HttpClient Configuration
        /*HttpClient httpClient = new ApacheAsyncHttpClientBuilder()
                .connectionTimeout(Duration.ofSeconds(10)) // Set the connection timeout time, the default is 10 seconds
                .responseTimeout(Duration.ofSeconds(10)) // Set the response timeout time, the default is 20 seconds
                .maxConnections(128) // Set the connection pool size
                .maxIdleTimeOut(Duration.ofSeconds(50)) // Set the connection pool timeout, the default is 30 seconds
                // Configure the proxy
                .proxy(new ProxyOptions(ProxyOptions.Type.HTTP, new InetSocketAddress("<your-proxy-hostname>", 9001))
                        .setCredentials("<your-proxy-username>", "<your-proxy-password>"))
                // If it is an https connection, you need to configure the certificate, or ignore the certificate(.ignoreSSL(true))
                .x509TrustManagers(new X509TrustManager[]{})
                .keyManagers(new KeyManager[]{})
                .ignoreSSL(false)
                .build();*/

        // Configure Credentials authentication information, including ak, secret, token
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                // Please ensure that the environment variables ALIBABA_CLOUD_ACCESS_KEY_ID and ALIBABA_CLOUD_ACCESS_KEY_SECRET are set.

                .accessKeyId(ALIBABA_CLOUD_ACCESS_KEY_ID)
                .accessKeySecret(ALIBABA_CLOUD_ACCESS_KEY_SECRET)
                //.securityToken(System.getenv("ALIBABA_CLOUD_SECURITY_TOKEN")) // use STS token
                .build());

        // Configure the Client
        AsyncClient client = AsyncClient.builder()
                .region("cn-hangzhou") // Region ID
                //.httpClient(httpClient) // Use the configured HttpClient, otherwise use the default HttpClient (Apache HttpClient)
                .credentialsProvider(provider)
                //.serviceConfiguration(Configuration.create()) // Service-level configuration
                // Client-level configuration rewrite, can set Endpoint, Http request parameters, etc.
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                // Endpoint 请参考 https://api.aliyun.com/product/Dm
                                .setEndpointOverride("dm.aliyuncs.com")
                        //.setConnectTimeout(Duration.ofSeconds(30))
                )
                .build();

        // Parameter settings for API request
        SingleSendMailRequest singleSendMailRequest = SingleSendMailRequest.builder()
                .accountName(ALIBABA_EMAIL_ACCOUNT_NAME)
                .addressType(ALIBABA_EMAIL_ADDRESS_TYPE)
                .replyToAddress(ALIBABA_EMAIL_REPLY_TO_ADDRESS)
                .toAddress(toAddress)
                .subject(subject)
                .htmlBody(htmlBody)
                // Request-level configuration rewrite, can set Http request parameters, etc.
                // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
                .build();

        // Asynchronously get the return value of the API request
        CompletableFuture<SingleSendMailResponse> response = client.singleSendMail(singleSendMailRequest);
        // Synchronously get the return value of the API request
        SingleSendMailResponse resp = response.get();
        System.out.println(new Gson().toJson(resp));
        // Asynchronous processing of return values
        /*response.thenAccept(resp -> {
            System.out.println(new Gson().toJson(resp));
        }).exceptionally(throwable -> { // Handling exceptions
            System.out.println(throwable.getMessage());
            return null;
        });*/

        // Finally, close the client
        client.close();
    }
}
