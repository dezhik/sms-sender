package ru.dezhik.sms.sender;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.impl.client.CloseableHttpClient;

import ru.dezhik.sms.sender.api.smsru.auth.AuthProvider;

/**
 * @author ilya.dezhin
 */
public class SenderServiceConfiguration {
    final String apiHost;
    final boolean testSendingEnabled;
    final boolean translitEnabled;
    /** could be useful while debugging some error */
    final boolean returnPlainResponse;
    final String apiId;
    final String login;
    final String password;
    final String fromName;
    final String partnerId;
    final Class<? extends AuthProvider> authProviderClass;
    final CloseableHttpClient httpClient;
    final ExecutorService executorService;
    final List<RetryPolicy> retryPolicies;
    final long asyncTerminationTimeoutMs;

    SenderServiceConfiguration(
            final String apiHost,
            final boolean testSendingEnabled,
            final boolean translitEnabled,
            final boolean returnPlainResponse,
            final String apiId,
            final String login,
            final String password,
            final String fromName,
            final String partnerId,
            final Class<? extends AuthProvider> authProviderClass,
            final CloseableHttpClient httpClient,
            final ExecutorService executorService,
            final List<RetryPolicy> retryPolicies,
            final long asyncTerminationTimeoutMs) {
        this.apiHost = apiHost;
        this.testSendingEnabled = testSendingEnabled;
        this.translitEnabled = translitEnabled;
        this.returnPlainResponse = returnPlainResponse;
        this.apiId = apiId;
        this.login = login;
        this.password = password;
        this.fromName = fromName;
        this.partnerId = partnerId;
        this.authProviderClass = authProviderClass;
        this.httpClient = httpClient;
        this.executorService = executorService;
        this.retryPolicies = retryPolicies;
        this.asyncTerminationTimeoutMs = asyncTerminationTimeoutMs;
    }

    public String getApiHost() {
        return apiHost;
    }

    public boolean isTestSendingEnabled() {
        return testSendingEnabled;
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public boolean isReturnPlainResponse() {
        return returnPlainResponse;
    }

    public String getApiId() {
        return apiId;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getFromName() {
        return fromName;
    }

    public boolean isTranslitEnabled() {
        return translitEnabled;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public Class<? extends AuthProvider> getAuthProviderClass() {
        return authProviderClass;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public List<RetryPolicy> getRetryPolicies() {
        return retryPolicies;
    }

    public long getAsyncTerminationTimeoutMs() {
        return asyncTerminationTimeoutMs;
    }
}
