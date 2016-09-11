package ru.dezhik.sms.sender.api.smsru.stoplist.delete;

import ru.dezhik.sms.sender.api.ApiRequest;
import ru.dezhik.sms.sender.api.smsru.SMSRuSimpleResponse;

/**
 * @author ilya.dezhin
 */
public class SMSRuStopListDeleteRequest extends ApiRequest<SMSRuStopListDeleteHandler, SMSRuSimpleResponse> {
    private String phone;

    @Override
    public SMSRuStopListDeleteHandler getHandler() {
        return new SMSRuStopListDeleteHandler();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
