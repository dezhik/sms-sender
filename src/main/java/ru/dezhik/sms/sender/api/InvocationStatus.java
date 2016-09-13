package ru.dezhik.sms.sender.api;

/**
 * @author ilya.dezhin
 */
public enum InvocationStatus {
    QUEUED(false),
    RUNNING(false),
    SUCCESS(false),
    VALIDATION_ERROR(true),
    NETWORK_ERROR(true),
    RESPONSE_PARSING_ERROR(true),
    ERROR(true),
    ;
    final boolean abnormal;

    InvocationStatus(boolean abnormal) {
        this.abnormal = abnormal;
    }

    public boolean isAbnormal() {
        return abnormal;
    }
}
