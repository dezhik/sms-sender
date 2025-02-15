package ru.dezhik.sms.sender.api.smsru.send;

import ru.dezhik.sms.sender.api.smsru.SMSRuResponseStatus;

public class SendingResult {

    private SMSRuResponseStatus status;
    private String statusText;
    private String smsId;

    public SMSRuResponseStatus getStatus() {
        return status;
    }

    public void setStatus(SMSRuResponseStatus status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getSmsId() {
        return smsId;
    }

    public void setSmsId(String smsId) {
        this.smsId = smsId;
    }
}
