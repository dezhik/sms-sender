package ru.dezhik.sms.sender.api.smsru.send;

import java.util.ArrayList;
import java.util.Collection;

import ru.dezhik.sms.sender.api.smsru.Pair;

/**
 * @author ilya.dezhin
 */
public class SMSRuBatchSendRequest extends AbstractSMSRuSendRequest<SMSRuBatchSendHandler> {
    Collection<Pair<String, String>> messages = new ArrayList<Pair<String, String>>();

    public void addReceiverAndTextPair(Pair<String, String> message) {
        messages.add(message);
    }

    public void addReceiverAndText(String receiver, String text) {
        messages.add(new Pair<String, String>(receiver, text));
    }

    public Collection<Pair<String, String>> getMessages() {
        return messages;
    }

    @Override
    public SMSRuBatchSendHandler getHandler() {
        return new SMSRuBatchSendHandler();
    }
}
