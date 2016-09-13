package ru.dezhik.sms.sender;

import java.util.concurrent.TimeUnit;

import ru.dezhik.sms.sender.api.ApiRequest;
import ru.dezhik.sms.sender.api.InvocationStatus;
import ru.dezhik.sms.sender.api.ApiResponse;

/**
 * @author ilya.dezhin
 */
public class NetworkErrorRetryPolicy<Req extends ApiRequest, Resp extends ApiResponse> implements RetryPolicy<Req, Resp> {
    private final int maxExecutionAttempts;
    private final long delayMs;

    public NetworkErrorRetryPolicy() {
        this(3, TimeUnit.SECONDS.toMillis(2));
    }

    public NetworkErrorRetryPolicy(int maxAttempts, long delayMs) {
        this.maxExecutionAttempts = maxAttempts;
        this.delayMs = delayMs;
    }

    @Override
    public boolean shouldRetry(Req request, Resp response) {
        return request.getStatus() == InvocationStatus.NETWORK_ERROR
                && request.getExecutionAttempt() < maxExecutionAttempts;
    }

    @Override
    public long getDelayDurationMs() {
        return delayMs;
    }
}
