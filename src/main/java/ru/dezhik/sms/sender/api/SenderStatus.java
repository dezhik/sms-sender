package ru.dezhik.sms.sender.api;

import ru.dezhik.sms.sender.SenderServiceConfiguration;

public enum SenderStatus {
    /**
     * Default status, should be treated as status not parsed.
     */
    UNKNOWN,
    /**
     * Success status.
     */
    OK,
    /**
     * Remote api has returned error.
     */
    ERROR,
    /**
     * Some kind of parsing failure.
     * Debug it by setting config param {@link SenderServiceConfiguration#isReturnPlainResponse()} to true
     * which enables passing plain response via {@link ApiResponse#getPlainResponse()}.
     */
    PARSING_ERROR,
    ;
}
