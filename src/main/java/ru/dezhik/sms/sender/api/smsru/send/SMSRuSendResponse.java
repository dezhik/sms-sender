package ru.dezhik.sms.sender.api.smsru.send;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.dezhik.sms.sender.api.smsru.SMSRuApiResponse;
import ru.dezhik.sms.sender.api.smsru.SMSRuResponseStatus;

/**
 * Iff all resulting sms entries have the same status,
*  otherwise setting {@link SMSRuResponseStatus#MIXED} status.
 * @author ilya.dezhin
 */
public class SMSRuSendResponse extends SMSRuApiResponse {
    private List<String> msgIds = new ArrayList<String>();
    private Double balance;
    private Map<String, SendingResult> smsByPhoneNumber;

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public void addMsgId(String msgId) {
        msgIds.add(msgId);
    }

    public List<String> getMsgIds() {
        return msgIds;
    }

    public Map<String, SendingResult> getSmsByPhoneNumber() {
        return smsByPhoneNumber;
    }

    public void setSmsByPhoneNumber(Map<String, SendingResult> smsByPhoneNumber) {
        this.smsByPhoneNumber = smsByPhoneNumber;
    }
}
