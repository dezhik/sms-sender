package ru.dezhik.sms.sender.api;

/**
 * @author ilya.dezhin
 */
public enum InvocationStatus {
    /**
     * Useful while using AsyncSenderService
     * Means that request has been created and submitted. Processing has not been started yet.
     */
    QUEUED(false),

    /**
     * Useful while using AsyncSenderService
     * Means that request is currently running by executor's worker.
     */
    RUNNING(false),

    /**
     * Validation of request's parameters failed. Http request to the API has not been made.
     */
    VALIDATION_ERROR(true),

    /**
     * Request processing failed due network error.
     */
    NETWORK_ERROR(true),

    /**
     * Remote API response HTTP status code is not 200.
     */
    RESPONSE_CODE_ERROR(true),

    /**
     * Remote API's response has incompatible format.
     * This could be due API server inner error or if API was changed without backwards compatibility.
     */
    RESPONSE_PARSING_ERROR(true),

    /**
     * Library's inner error. It's better to debug code where this status is set
     * or try to create Issue on Github but don't forget to give detailed description of steps you made.
     */
    ERROR(true),

    /**
     * Indicated that request was executed successfully and the remote API's response was parsed without errors.
     */
    SUCCESS(false),
    ;

    final boolean abnormal;

    InvocationStatus(boolean abnormal) {
        this.abnormal = abnormal;
    }

    public boolean isAbnormal() {
        return abnormal;
    }
}
