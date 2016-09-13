package ru.dezhik.sms.sender.api.smsru.senders;

import java.util.LinkedList;
import java.util.List;

import ru.dezhik.sms.sender.api.smsru.SMSRuApiResponse;

/**
 * @author ilya.dezhin
 */
public class SMSRuSendersResponse extends SMSRuApiResponse {
    List<String> senders = new LinkedList<String>();

    public List<String> getSenders() {
        return senders;
    }

    public void addSenders(String sender) {
        senders.add(sender);
    }
}
