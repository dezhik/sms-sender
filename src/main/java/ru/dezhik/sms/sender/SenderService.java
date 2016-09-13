package ru.dezhik.sms.sender;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import ru.dezhik.sms.sender.api.ApiRequest;
import ru.dezhik.sms.sender.api.ApiRequestHandler;
import ru.dezhik.sms.sender.api.InvocationStatus;
import ru.dezhik.sms.sender.api.ApiResponse;
import ru.dezhik.sms.sender.api.smsru.auth.AuthProvider;
import ru.dezhik.sms.sender.api.smsru.auth.DefaultAuthProvider;

/**
 * @author ilya.dezhin
 */
@ThreadSafe
public class SenderService {

    private final SenderServiceConfiguration config;
    private final CloseableHttpClient httpClient;
    private final AuthProvider authProvider;

    private final Map<Class<? extends ApiRequest>, ApiRequestHandler> handlersRegistry =
            new ConcurrentHashMap<Class<? extends ApiRequest>, ApiRequestHandler>();

    public SenderService(SenderServiceConfiguration config) {
        if (config == null) {
            throw new IllegalStateException();
        }
        this.config = config;
        this.httpClient = config.getHttpClient() != null ? config.getHttpClient() : HttpClients.createDefault();
        Class<? extends AuthProvider> authClass = config.getAuthProviderClass() != null ? config.getAuthProviderClass()
                : DefaultAuthProvider.class;
        try {
            this.authProvider = authClass.getConstructor(SenderServiceConfiguration.class).newInstance(config);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Executes a request using the given configuration and processes the
     * response using provided in the class handler.
     *
     * @param request API request to execute
     *
     * @return API response constructed by the handler or null if network exception occurred and retries failed.
     * @throws {@link IllegalStateException} if remote API server returns not success (200) status code
     * @throws {@link IllegalArgumentException} if request validation fails
     *          or handler was not found and not specified in request class
     *          or incorrect API URI was constructed.
     */
    public <H extends ApiRequestHandler, R extends ApiResponse> R execute(final ApiRequest<H, R> request) {
        ApiRequestHandler handler = handlersRegistry.get(request.getClass());
        if (handler == null) {
            handler = request.getHandler();
            if (handler == null) {
                throw new IllegalArgumentException();
            }
            handler.setConfig(config);
            handlersRegistry.put(request.getClass(), handler);
        }

        //Validating request
        try {
            handler.validate(request);
        } catch (RequestValidationException e) {
            request.setStatus(InvocationStatus.VALIDATION_ERROR);
            request.setException(e);
            return null;
        }

        //Executing API request to the remote server and parsing result.
        final String methodURI = config.getApiHost() + handler.getMethodPath();
        R response = null;
        RetryPolicy retryPolicy = null;
        do {
            request.setStatus(InvocationStatus.RUNNING);
            try {
                request.incrementExecutionAttempt();
                if (retryPolicy != null) {
                    try {
                        Thread.sleep(retryPolicy.getDelayDurationMs());
                    } catch (InterruptedException e) {
                    }
                }
                final List<NameValuePair> params = authProvider.provideAuthParams();
                handler.appendParams(request, params);

                final HttpPost httpPost = new HttpPost();
                httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                httpPost.setURI(new URI(methodURI));

                final HttpResponse httpResponse = httpClient.execute(httpPost);
                final int code = httpResponse.getStatusLine().getStatusCode();
                if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    throw new IllegalStateException("sms.ru api wrong response code: " + code);
                }

                final String responseBody = EntityUtils.toString(httpResponse.getEntity());
                response = (R) handler.parseResponse(request, responseBody);

                if (config.isReturnPlainResponse() && response != null) {
                    response.setPlainResponse(responseBody);
                }

                if (request.getStatus() == InvocationStatus.RUNNING && response != null) {
                    request.setStatus(InvocationStatus.SUCCESS);
                }
            } catch (IOException e) {
                request.setStatus(InvocationStatus.NETWORK_ERROR);
                request.setException(e);
            } catch (URISyntaxException e) {
                request.setStatus(InvocationStatus.ERROR);
                request.setException(e);
                return null;
            }
        } while ((retryPolicy = findApplicableRetryPolicy(request, response)) != null);

        return response;
    }

    protected <Req extends ApiRequest, Resp extends ApiResponse> RetryPolicy findApplicableRetryPolicy(
            Req request, Resp response
    ) {
        for (RetryPolicy retryPolicy : config.getRetryPolicies()) {
            if (retryPolicy.shouldRetry(request, response)) {
                return retryPolicy;
            }
        }
        return null;
    }

    /**
     * This method should be used if you want to change default handler for
     * {@link ApiRequest} instances.
     *
     * @param requestClass which would be processed by the handler.
     * @param handler used for validating and parsing requests of requestClass type.
     * @throws {@link IllegalArgumentException} if requestClass or handler is null.
     */
    public void registerHandler(Class<? extends ApiRequest> requestClass, ApiRequestHandler handler) {
        if (requestClass == null) {
            throw new IllegalArgumentException("Request class must be specified");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler must be specified");
        }
        handlersRegistry.put(requestClass, handler);
    }

    /**
     * Closes {@link CloseableHttpClient} if exists.
     * @throws IOException
     */
    public void shutdown() throws IOException {
        if (httpClient != null) {
            httpClient.close();
        }
    }
}
