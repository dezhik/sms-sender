package ru.dezhik.sms.sender.api.smsru.status;

import java.util.List;
import java.util.StringTokenizer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import ru.dezhik.sms.sender.RequestValidationException;
import ru.dezhik.sms.sender.api.smsru.AbstractSMSRuApiHandler;
import ru.dezhik.sms.sender.api.smsru.SMSRuApiResponse;

/**
 * @author ilya.dezhin
 */
public class SMSRuStatusHandler extends AbstractSMSRuApiHandler<SMSRuStatusRequest, SMSRuApiResponse> {
    @Override
    public String getMethodPath() {
        return "/sms/status";
    }

    @Override
    public void validate(SMSRuStatusRequest request) throws IllegalArgumentException {
        if (request.getId() == null || request.getId().isEmpty()) {
            throw new RequestValidationException("SMS id can't be empty.");
        }
    }

    @Override
    public void appendParams(SMSRuStatusRequest request, List<NameValuePair> params) {
        params.add(new BasicNameValuePair("id", request.getId()));
    }

    @Override
    public SMSRuApiResponse parseResponse(SMSRuStatusRequest request, String responseStr) {
        final SMSRuApiResponse response = new SMSRuApiResponse();
        StringTokenizer tokenizer = tokenizeResponse(responseStr);
        parseAndSetStatus(request, response, tokenizer);
        return response;
    }

}
