package ru.dezhik.sms.sender.api.smsru.callcheck;

import ru.dezhik.sms.sender.api.ApiRequest;

public class SmsRuCallCheckInitRequest
        extends ApiRequest<SmsRuCallCheckInitHandler, SmsRuCallCheckInitResponse> {

    public SmsRuCallCheckInitRequest(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private final String phoneNumber;

    @Override
    public SmsRuCallCheckInitHandler getHandler() {
        return new SmsRuCallCheckInitHandler();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
