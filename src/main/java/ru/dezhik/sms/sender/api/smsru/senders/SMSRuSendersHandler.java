package ru.dezhik.sms.sender.api.smsru.senders;

import java.util.List;
import java.util.StringTokenizer;

import org.apache.http.NameValuePair;

import ru.dezhik.sms.sender.api.smsru.AbstractSMSRuApiHandler;

/**
 * @author ilya.dezhin
 */
public class SMSRuSendersHandler extends AbstractSMSRuApiHandler<SMSRuSendersRequest, SMSRuSendersResponse> {
    @Override
    public String getMethodPath() {
        return "/my/senders";
    }

    @Override
    public void validate(SMSRuSendersRequest request) throws IllegalArgumentException {
    }

    @Override
    public void appendParams(SMSRuSendersRequest request, List<NameValuePair> params) {
    }

    @Override
    public SMSRuSendersResponse parseResponse(SMSRuSendersRequest request, String responseStr) {
        final SMSRuSendersResponse response = new SMSRuSendersResponse();
        final StringTokenizer tokenizer = tokenizeResponse(responseStr);
        parseAndSetStatus(request, response, tokenizer);
        while (tokenizer.hasMoreTokens()) {
            response.addSenders(tokenizer.nextToken());
        }
        return response;
    }
}
