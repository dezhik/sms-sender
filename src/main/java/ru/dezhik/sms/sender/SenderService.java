package ru.dezhik.sms.sender;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import ru.dezhik.sms.sender.api.ApiRequest;
import ru.dezhik.sms.sender.api.ApiRequestHandler;
import ru.dezhik.sms.sender.api.ApiResponse;
import ru.dezhik.sms.sender.api.InvocationStatus;
import ru.dezhik.sms.sender.api.smsru.auth.AuthProvider;
import ru.dezhik.sms.sender.api.smsru.auth.DefaultAuthProvider;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ilya.dezhin
 */
@Contract(threading = ThreadingBehavior.SAFE)
public class SenderService {
    private static final AtomicInteger idCounter = new AtomicInteger();
    private final SenderServiceConfiguration config;
    private final CloseableHttpClient httpClient;
    private final AuthProvider authProvider;
    private final String serviceName;

    private final Map<Class<? extends ApiRequest>, ApiRequestHandler> handlersRegistry =
            new ConcurrentHashMap<Class<? extends ApiRequest>, ApiRequestHandler>();
    private final SenderServiceStat serviceStat;
    private final ObjectName mxbeanName;

    public SenderService(SenderServiceConfiguration config) {
        this(config, false);
    }

    SenderService(SenderServiceConfiguration config, boolean async) {
        if (config == null) {
            throw new IllegalStateException();
        }
        this.config = config;
        this.httpClient = config.getHttpClient() != null ? config.getHttpClient() : HttpClients.createDefault();

        final Class<? extends AuthProvider> authClass = config.getAuthProviderClass() != null
                ? config.getAuthProviderClass()
                : DefaultAuthProvider.class;
        try {
            this.authProvider = authClass.getConstructor(SenderServiceConfiguration.class).newInstance(config);
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            this.serviceName = "SenderService-" + idCounter.incrementAndGet();
            this.mxbeanName = new ObjectName("ru.dezhik.sms.sender:type=" + serviceName);
            this.serviceStat = new SenderServiceStat(async);
            mbs.registerMBean(serviceStat, mxbeanName);
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
     * @throws IllegalStateException if remote API server didn't return HTTP status 200 OK
     * @throws IllegalArgumentException if request validation fails
     *          or handler was not found and not specified in request class
     *          or incorrect API URI was constructed.
     */
    public <H extends ApiRequestHandler, R extends ApiResponse> R execute(final ApiRequest<H, R> request) {
        //finds proper handler or throws IllegalStateException
        final ApiRequestHandler handler = getRequestHandler(request);
        serviceStat.requests.incrementAndGet();

        R response = null;
        try {
            // Validating request params
            handler.validate(request);
            // Executing API request to the remote server and parsing result
            response = executeImpl(handler, request);
        } catch (RequestValidationException e) {
            processStatus(request, InvocationStatus.VALIDATION_ERROR, e);
        }

        return response;
    }

    private <H extends ApiRequestHandler, R extends ApiResponse> R executeImpl(
            final ApiRequestHandler handler, final ApiRequest<H, R> request) {
        final String methodURI = config.getApiHost() + handler.getMethodPath();
        R response = null;
        RetryPolicy retryPolicy = null;
        do {
            request.setStatus(InvocationStatus.RUNNING);
            try {
                request.incrementExecutionAttempt();
                if (retryPolicy != null) {
                    serviceStat.retries.incrementAndGet();
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
                if (code == HttpStatus.SC_OK) {
                    final String responseBody = EntityUtils.toString(httpResponse.getEntity());
                    // request.status could be changed here, e.g. to RESPONSE_PARSING_ERROR
                    response = (R) handler.parseResponse(request, responseBody);

                    if (config.isReturnPlainResponse() && response != null) {
                        response.setPlainResponse(responseBody);
                    }

                    if (request.getStatus() == InvocationStatus.RUNNING && response != null) {
                        processStatus(request, InvocationStatus.SUCCESS, null);
                        break;
                    } else {
                        processStatus(request, request.getStatus(), null);
                    }
                } else {
                    processStatus(request, InvocationStatus.RESPONSE_CODE_ERROR,
                            new RemoteException("sms.ru api wrong response code: " + code));
                }
            } catch (IOException e) {
                processStatus(request, InvocationStatus.NETWORK_ERROR, e);
            } catch (URISyntaxException e) {
                processStatus(request, InvocationStatus.ERROR, e);
                break;
            }
        } while ((retryPolicy = findApplicableRetryPolicy(request, response)) != null);

        return response;
    }

    private <H extends ApiRequestHandler, R extends ApiResponse> void processStatus(
            ApiRequest<H, R> request, InvocationStatus status, Throwable ex) {
        request.setStatus(status);
        request.setException(ex);
        serviceStat.reportStatus(status);

        if (status == InvocationStatus.SUCCESS) {
            serviceStat.reportSucceededRequest(request);
        } else {
            serviceStat.reportFailedRequest(request);
        }
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
     * @throws IllegalArgumentException if requestClass or handler is null.
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
     * @throws IOException if an I/O error occurs
     */
    public void shutdown() throws IOException {
        if (mxbeanName != null) {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            try {
                mbs.unregisterMBean(mxbeanName);
            } catch (InstanceNotFoundException e) {
            } catch (MBeanRegistrationException e) {
            }
        }

        if (httpClient != null) {
            httpClient.close();
        }
    }

    private ApiRequestHandler getRequestHandler(final ApiRequest<? extends ApiRequestHandler, ?> request) {
        ApiRequestHandler handler = handlersRegistry.get(request.getClass());
        if (handler == null) {
            handler = request.getHandler();
            if (handler == null) {
                processStatus(request, InvocationStatus.ERROR, null);
                throw new IllegalArgumentException();
            }
            handler.setConfig(config);
            handlersRegistry.put(request.getClass(), handler);
        }
        return handler;
    }

    @Override
    public String toString() {
        return getClass().getName() + "@" + serviceName;
    }
}
