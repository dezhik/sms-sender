package ru.dezhik.sms.sender.api.smsru.stoplist.add;

import ru.dezhik.sms.sender.api.ApiRequest;
import ru.dezhik.sms.sender.api.smsru.SMSRuApiResponse;

/**
 * @author ilya.dezhin
 */
public class SMSRuStopListAddRequest extends ApiRequest<SMSRuStopListAddHandler, SMSRuApiResponse> {
    private String phone;
    private String comment;

    @Override
    public SMSRuStopListAddHandler getHandler() {
        return new SMSRuStopListAddHandler();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
