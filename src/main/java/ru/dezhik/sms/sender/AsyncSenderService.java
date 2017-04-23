package ru.dezhik.sms.sender;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import ru.dezhik.sms.sender.api.ApiCallback;
import ru.dezhik.sms.sender.api.ApiRequest;
import ru.dezhik.sms.sender.api.ApiRequestHandler;
import ru.dezhik.sms.sender.api.ApiResponse;

/**
 *
 * @author ilya.dezhin
 */
public class AsyncSenderService {
    private final SenderServiceConfiguration config;
    private final SenderService senderService;
    private final ExecutorService apiExecutors;

    public AsyncSenderService(SenderServiceConfiguration config) {
        this.config = config;
        this.senderService = new SenderService(config);
        this.apiExecutors = config.getExecutorService() != null
                ? config.getExecutorService()
                : Executors.newSingleThreadExecutor();
    }

    /**
     * Should be used if you want to process response by yourself
     * without callback mechanics.
     * Makes retries using {@link SenderServiceConfiguration#getRetryPolicies()} if necessary.
     * @param request API request to execute
     * @return response wrapped in {@link Future}.
     * Note that response could be null if network error occurred and retries didn't help.
     */
    public <H extends ApiRequestHandler, R extends ApiResponse> Future<R> execute(
            final ApiRequest<H, R> request
    ) {
        return apiExecutors.submit(new Callable<R>() {
            @Override
            public R call() {
                return senderService.execute(request);
            }
        });
    }

    /**
     *
     * @param request
     * @param callbacks which should be executed after response parsing
     */
    public <H extends ApiRequestHandler, R extends ApiResponse> void execute(
            final ApiRequest<H, R> request,
            final ApiCallback... callbacks
    ) {
        apiExecutors.submit(new Runnable() {
            @Override
            public void run() {
                final R response = senderService.execute(request);
                if (callbacks != null) {
                    for (final ApiCallback callback : callbacks) {
                        if (callback.apply(request, response)) {
                            callback.execute(request, response);
                        }
                    }
                }
            }
        });
    }

    /**
     * This method should be used if you want to change default handler for
     * {@link ApiRequest} instances or adding new API methods.
     *
     * @param requestClass specifies {@link Class} which instances would be processed by the handler
     * @param handler for validating and parsing requests of such type
     * @throws IllegalArgumentException if requestClass or handler is null.
     */
    public void registerHandler(Class<? extends ApiRequest> requestClass, ApiRequestHandler handler) {
        senderService.registerHandler(requestClass, handler);
    }

    /**
     * 1. Initiates executor shutdown
     * 2. Waits for specified in configuration period of time for actively
     * executing tasks to terminate and tries to stop them if necessary.
     * 3. Initiates shutdown on the underlying {@link SenderService}
     * @throws InterruptedException if interrupted while waiting
     * @throws IOException if an I/O error occurs
     */
    public void shutdown() throws InterruptedException, IOException {
        apiExecutors.shutdown();
        if (!apiExecutors.awaitTermination(config.getAsyncTerminationTimeoutMs(), TimeUnit.MILLISECONDS)) {
            apiExecutors.shutdownNow();
        }
        senderService.shutdown();
    }
}
