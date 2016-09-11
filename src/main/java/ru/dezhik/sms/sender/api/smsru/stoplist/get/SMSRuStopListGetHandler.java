package ru.dezhik.sms.sender.api.smsru.stoplist.get;

import java.util.List;
import java.util.StringTokenizer;

import org.apache.http.NameValuePair;

import ru.dezhik.sms.sender.api.smsru.AbstractSMSRuApiHandler;
import ru.dezhik.sms.sender.api.smsru.Pair;

/**
 * @author ilya.dezhin
 */
public class SMSRuStopListGetHandler extends AbstractSMSRuApiHandler<SMSRuStopListGetRequest, SMSRuStopListGetResponse> {
    @Override
    public String getMethodPath() {
        return "/stoplist/get";
    }

    @Override
    public void validate(SMSRuStopListGetRequest request) throws IllegalArgumentException {
    }

    @Override
    public void appendParams(SMSRuStopListGetRequest request, List<NameValuePair> params) {
    }

    @Override
    public SMSRuStopListGetResponse parseResponse(SMSRuStopListGetRequest request, String responseStr) {
        final SMSRuStopListGetResponse response = new SMSRuStopListGetResponse();
        final StringTokenizer tokenizer = tokenizeResponse(responseStr);
        parseAndSetStatus(request, response, tokenizer);

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            String[] pair = token.split(";");
            if (pair.length == 2) {
                response.addBannedPhoneAndCommentPair(new Pair<String, String>(pair[0], pair[1]));
            }
        }
        return response;
    }
}
