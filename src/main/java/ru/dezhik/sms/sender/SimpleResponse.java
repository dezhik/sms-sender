package ru.dezhik.sms.sender;

/**
 * @author ilya.dezhin
 */
public class SimpleResponse<T> {

    /**
     * SMS sending system specific status, e.g. {@link ru.dezhik.sms.sender.api.smsru.SMSRuResultStatus}
     */
    protected T responseStatus;
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

    public T getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(T responseStatus) {
        this.responseStatus = responseStatus;
    }
}
