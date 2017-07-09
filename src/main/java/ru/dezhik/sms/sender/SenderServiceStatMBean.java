package ru.dezhik.sms.sender;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author ilya.dezhin
 */
public interface SenderServiceStatMBean {
    boolean isAsync();
    String getCreatedAt();
    long getRequests();
    long getRetries();
    Map<String, AtomicLong> getSucceededRequestsStats();
    Map<String, AtomicLong> getFailedRequestsStats();
    Map<String, AtomicLong> getRequestsByStatusStats();
    String getLastSucceededRequestTime();
    String getLastFailedRequestTime();
}
