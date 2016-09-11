package ru.dezhik.sms.sender.api.smsru.send;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author ilya.dezhin
 */
public class SMSRuSendRequest extends AbstractSMSRuSendRequest<SMSRuSendHandler> {
    Collection<String> receivers = new LinkedList<String>();
    String text;

    @Override
    public SMSRuSendHandler getHandler() {
        return new SMSRuSendHandler();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Collection<String> getReceivers() {
        return receivers;
    }

    public void setReceivers(Collection<String> receivers) {
        this.receivers = receivers;
    }

    public void addReceiver(String receiver) {
        this.receivers.add(receiver);
    }
}
