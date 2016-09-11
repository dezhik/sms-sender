package ru.dezhik.sms.sender.api.smsru.cost;

import ru.dezhik.sms.sender.api.ApiRequest;

/**
 * @author ilya.dezhin
 */
public class SMSRuCostRequest extends ApiRequest<SMSRuCostHandler, SMSRuCostResponse> {
    String text;
    String receiver;
    boolean translit;

    @Override
    public SMSRuCostHandler getHandler() {
        return new SMSRuCostHandler();
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isTranslit() {
        return translit;
    }

    public void setTranslit(boolean translit) {
        this.translit = translit;
    }
}
