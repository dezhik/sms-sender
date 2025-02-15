package ru.dezhik.sms.sender.api.smsru.callcheck;

import ru.dezhik.sms.sender.api.ApiResponse;
import ru.dezhik.sms.sender.api.SenderStatus;

public class SmsRuCallCheckInitResponse extends ApiResponse<SenderStatus> {
    private String callCheckId;
    private String callPhone;
    private String callPhonePretty;
    private String callPhoneHtml;

    public String getCallCheckId() {
        return callCheckId;
    }

    public void setCallCheckId(String callCheckId) {
        this.callCheckId = callCheckId;
    }

    public String getCallPhone() {
        return callPhone;
    }

    public void setCallPhone(String callPhone) {
        this.callPhone = callPhone;
    }

    public String getCallPhonePretty() {
        return callPhonePretty;
    }

    public void setCallPhonePretty(String callPhonePretty) {
        this.callPhonePretty = callPhonePretty;
    }

    public String getCallPhoneHtml() {
        return callPhoneHtml;
    }

    public void setCallPhoneHtml(String callPhoneHtml) {
        this.callPhoneHtml = callPhoneHtml;
    }
}
