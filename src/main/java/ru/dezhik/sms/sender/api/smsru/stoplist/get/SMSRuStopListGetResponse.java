package ru.dezhik.sms.sender.api.smsru.stoplist.get;

import java.util.LinkedList;
import java.util.List;

import ru.dezhik.sms.sender.api.smsru.Pair;
import ru.dezhik.sms.sender.api.smsru.SMSRuApiResponse;

/**
 * @author ilya.dezhin
 */
public class SMSRuStopListGetResponse extends SMSRuApiResponse {
    List<Pair<String, String>> bannedPhoneAndCommentPairs = new LinkedList<Pair<String, String>>();

    public void addBannedPhoneAndCommentPair(Pair<String, String> pair) {
        bannedPhoneAndCommentPairs.add(pair);
    }

    public List<Pair<String, String>> getBannedPhoneAndCommentPairs() {
        return bannedPhoneAndCommentPairs;
    }
}
