package ru.dezhik.sms.sender.api.smsru.callcheck;

import ru.dezhik.sms.sender.api.ApiRequest;

public class SmsRuCallCheckGetStatusRequest
        extends ApiRequest<SmsRuCallCheckGetStatusHandler, SmsRuCallCheckGetStatusResponse> {

    private final String callCheckId;

    public SmsRuCallCheckGetStatusRequest(String callCheckId) {
        this.callCheckId = callCheckId;
    }

    @Override
    public SmsRuCallCheckGetStatusHandler getHandler() {
        return new SmsRuCallCheckGetStatusHandler();
    }

    public String getCallCheckId() {
        return callCheckId;
    }
}
