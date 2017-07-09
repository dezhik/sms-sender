package ru.dezhik.sms.sender;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import ru.dezhik.sms.sender.api.ApiRequest;
import ru.dezhik.sms.sender.api.InvocationStatus;

/**
 * @author ilya.dezhin
 */
public class SenderServiceStat implements SenderServiceStatMBean {
    private static final DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:SS");
    private final boolean async;
    private final Date createdAt;
    final AtomicLong requests = new AtomicLong(0);
    final AtomicLong retries = new AtomicLong(0);
    final ConcurrentHashMap<String, AtomicLong> requestsByStatus = new ConcurrentHashMap<String, AtomicLong>();
    final ConcurrentHashMap<String, AtomicLong> succeededRequestsByName = new ConcurrentHashMap<String, AtomicLong>();
    final ConcurrentHashMap<String, AtomicLong> failedRequestsByName = new ConcurrentHashMap<String, AtomicLong>();
    volatile Date lastSucceededRequestTime;
    volatile Date lastFailedRequestTime;

    SenderServiceStat(boolean async) {
        this.async = async;
        this.createdAt = new Date(System.currentTimeMillis());
    }

    @Override
    public boolean isAsync() {
        return async;
    }

    @Override
    public String getCreatedAt() {
        return dateFormat.format(createdAt);
    }

    @Override
    public long getRequests() {
        return requests.get();
    }

    @Override
    public long getRetries() {
        return retries.get();
    }

    @Override
    public Map<String, AtomicLong> getSucceededRequestsStats() {
        return succeededRequestsByName;
    }

    @Override
    public Map<String, AtomicLong> getFailedRequestsStats() {
        return failedRequestsByName;
    }

    @Override
    public Map<String, AtomicLong> getRequestsByStatusStats() {
        return requestsByStatus;
    }

    @Override
    public String getLastSucceededRequestTime() {
        return lastSucceededRequestTime != null ? dateFormat.format(lastSucceededRequestTime) : "--";
    }

    @Override
    public String getLastFailedRequestTime() {
        return lastFailedRequestTime != null ? dateFormat.format(lastFailedRequestTime) : "--";
    }

    public void reportStatus(final InvocationStatus status) {
        if (status != null) {
            incrementCounter(requestsByStatus, status.name());
        }
    }

    public void reportSucceededRequest(ApiRequest request) {
        incrementCounter(succeededRequestsByName, request.getClass().getName());
        lastSucceededRequestTime = new Date(System.currentTimeMillis());
    }

    public void reportFailedRequest(ApiRequest request) {
        incrementCounter(failedRequestsByName, request.getClass().getName());
        lastFailedRequestTime = new Date(System.currentTimeMillis());
    }

    private void incrementCounter(final ConcurrentHashMap<String, AtomicLong> map, final String key) {
        AtomicLong counter = map.get(key);
        if (counter == null) {
            counter = map.putIfAbsent(key, new AtomicLong(1));
            if (counter == null) {
                return;
            }
        }
        counter.incrementAndGet();
    }
}
