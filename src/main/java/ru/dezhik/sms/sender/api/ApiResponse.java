package ru.dezhik.sms.sender.api;

import ru.dezhik.sms.sender.SenderServiceConfiguration;
import ru.dezhik.sms.sender.api.smsru.SMSRuResponseStatus;

/**
 * @author ilya.dezhin
 */
public class ApiResponse<T> {

    /**
     * SMS sending system specific status, e.g. {@link SMSRuResponseStatus}
     * For SMSRuResponseStatus iff all resulting sms entries have the same status,
     * otherwise setting {@link SMSRuResponseStatus#MIXED}.
     */
    protected T status;
    /**
     * This field would be set only if no network error occurred
     * and {@link SenderServiceConfiguration#isReturnPlainResponse()} equals true.
     */
    protected String plainResponse;

    public String getPlainResponse() {
        return plainResponse;
    }

    public void setPlainResponse(String plainResponse) {
        this.plainResponse = plainResponse;
    }

    /**
     * @deprecated Use {@link ApiResponse#getStatus()} instead
     */
    @Deprecated
    public T getResponseStatus() {
        return status;
    }

    public void setStatus(T status) {
        this.status = status;
    }

    public T getStatus() {
        return status;
    }
}
