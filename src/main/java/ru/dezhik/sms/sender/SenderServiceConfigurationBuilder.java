package ru.dezhik.sms.sender;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import ru.dezhik.sms.sender.api.smsru.auth.AuthProvider;
import ru.dezhik.sms.sender.api.smsru.auth.DefaultAuthProvider;

/**
 * @author ilya.dezhin
 */
public class SenderServiceConfigurationBuilder {
    String apiHost = "https://sms.ru/";
    boolean testSendingEnabled;
    String testPhoneNumber;
    boolean translitEnabled;
    //could be useful while debugging some error
    boolean returnPlainResponse;
    String apiId;
    String login;
    String password;
    String fromName;
    String partnerId = "81302";
    Class<? extends AuthProvider> authProviderClass;
    CloseableHttpClient httpClient;
    ExecutorService executorService;
    List<RetryPolicy> retryPolicies = new LinkedList<RetryPolicy>();
    long asyncTerminationTimeoutMs = 2 * 1000;

    SenderServiceConfigurationBuilder() {
    }

    public static SenderServiceConfigurationBuilder create() {
        return new SenderServiceConfigurationBuilder();
    }

    public SenderServiceConfigurationBuilder setApiHost(String apiHost) {
        this.apiHost = apiHost;
        return this;
    }

    public SenderServiceConfigurationBuilder setTestSendingEnabled(boolean testSendingEnabled) {
        this.testSendingEnabled = testSendingEnabled;
        return this;
    }

    public SenderServiceConfigurationBuilder setTranslitEnabled(boolean translitEnabled) {
        this.translitEnabled = translitEnabled;
        return this;
    }

    public SenderServiceConfigurationBuilder setReturnPlainResponse(boolean returnPlainResponse) {
        this.returnPlainResponse = returnPlainResponse;
        return this;
    }

    public SenderServiceConfigurationBuilder setApiId(String apiId) {
        this.apiId = apiId;
        return this;
    }

    public SenderServiceConfigurationBuilder setLogin(String login) {
        this.login = login;
        return this;
    }

    public SenderServiceConfigurationBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public SenderServiceConfigurationBuilder setFromName(String fromName) {
        this.fromName = fromName;
        return this;
    }

    public SenderServiceConfigurationBuilder setAuthProviderClass(Class<? extends AuthProvider> authProviderClass) {
        this.authProviderClass = authProviderClass;
        return this;
    }

    public SenderServiceConfigurationBuilder setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    public SenderServiceConfigurationBuilder setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public SenderServiceConfigurationBuilder addRetryPolicy(RetryPolicy retryPolicy) {
        if (this.retryPolicies == null) {
            this.retryPolicies = new LinkedList<RetryPolicy>();
        }
        if (retryPolicy != null) {
            this.retryPolicies.add(retryPolicy);
        }
        return this;
    }

    public SenderServiceConfigurationBuilder setPartnerId(String partnerId) {
        if (partnerId != null) {
            this.partnerId = partnerId;
        }
        return this;
    }

    public SenderServiceConfigurationBuilder load(InputStream propertiesStream) throws IOException {
        final Properties properties = new Properties();
        properties.load(propertiesStream);
        return load(properties);
    }

    /**
     * Is a convenient way to load all properties for a service, except
     * {@link #httpClient}, {@link #authProviderClass} and {@link #executorService} fields.
     * The property would override default or previously set value only if
     * it is found in {@link Properties} input argument and is not empty.
     *
     * @param properties map containing properties for sending service
     * @return {@link SenderServiceConfigurationBuilder} current builder
     */
    public SenderServiceConfigurationBuilder load(Properties properties) {
        if (properties == null) {
            return this;
        }

        String apiUrl = properties.getProperty("apiHost");
        if (apiUrl != null && !apiUrl.trim().isEmpty()) {
            this.apiId = apiUrl.trim();
        }

        String apiId = properties.getProperty("apiId");
        if (apiId != null && !apiId.trim().isEmpty()) {
            this.apiId = apiId.trim();
        }

        String login = properties.getProperty("login");
        if (login != null && !login.trim().isEmpty()) {
            this.login = login.trim();
        }

        String password = properties.getProperty("password");
        if (password != null && !password.trim().isEmpty()) {
            this.password = password.trim();
        }

        String fromName = properties.getProperty("fromName");
        if (fromName != null && !fromName.trim().isEmpty()) {
            this.fromName = fromName.trim();
        }

        Boolean testSendingEnabled = getNullableBoolean(properties.getProperty("testSendingEnabled"));
        if (testSendingEnabled != null) {
            this.testSendingEnabled = testSendingEnabled.booleanValue();
        }

        String testPhoneNumber = properties.getProperty("testPhoneNumber");
        if (testPhoneNumber != null && !testPhoneNumber.trim().isEmpty()) {
            this.testPhoneNumber = testPhoneNumber.trim();
        }

        Boolean translitEnabled = getNullableBoolean(properties.getProperty("translitEnabled"));
        if (translitEnabled != null) {
            this.translitEnabled = translitEnabled.booleanValue();
        }

        Boolean returnPlainResponse = getNullableBoolean(properties.getProperty("returnPlainResponse"));
        if (returnPlainResponse != null) {
            this.returnPlainResponse = returnPlainResponse.booleanValue();
        }

        Integer numberOfRetries = getNullableInteger(properties.getProperty("numberOfRetries"));
        if (numberOfRetries != null) {
            Long retryDelayInMs = getNullableLong(properties.getProperty("retryDelayMs"));
            addRetryPolicy(new NetworkErrorRetryPolicy(
                    numberOfRetries,
                    retryDelayInMs != null ? retryDelayInMs : TimeUnit.SECONDS.toMillis(2)
                )
            );
        }
        setPartnerId(properties.getProperty("asyncTerminationTimeoutMs"));

        Long asyncTerminationTimeoutMs = getNullableLong(properties.getProperty("asyncTerminationTimeoutMs"));
        if (asyncTerminationTimeoutMs != null) {
            this.asyncTerminationTimeoutMs = asyncTerminationTimeoutMs;
        }

        return this;
    }

    private Boolean getNullableBoolean(String value) {
        if (value != null && !value.trim().isEmpty()) {
            return Boolean.parseBoolean(value);
        }
        return null;
    }

    private Integer getNullableInteger(String value) {
        Integer num = null;
        if (value != null && !value.trim().isEmpty()) {
            try {
                num = Integer.parseInt(value);
            } catch (NumberFormatException nfe) {}
        }
        return num;
    }

    private Long getNullableLong(String value) {
        Long num = null;
        if (value != null && !value.trim().isEmpty()) {
            try {
                num = Long.parseLong(value);
            } catch (NumberFormatException nfe) {}
        }
        return num;
    }

    public SenderServiceConfiguration build() {
        if (retryPolicies != null && retryPolicies.isEmpty()) {
            retryPolicies.add(new NetworkErrorRetryPolicy());
        }
        return new SenderServiceConfiguration(
                apiHost,
                testSendingEnabled,
                testPhoneNumber,
                translitEnabled,
                returnPlainResponse,
                apiId,
                login,
                password,
                fromName,
                partnerId,
                authProviderClass != null ? authProviderClass : DefaultAuthProvider.class,
                httpClient != null ? httpClient : HttpClients.createDefault(),
                executorService,
                Collections.unmodifiableList(retryPolicies != null ? retryPolicies : new LinkedList<RetryPolicy>()),
                asyncTerminationTimeoutMs
        );
    }
}
