package ru.dezhik.sms.sender.api.smsru.stoplist.delete;

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
public class SMSRuStopListDeleteHandler extends AbstractSMSRuApiHandler<SMSRuStopListDeleteRequest, SMSRuApiResponse> {
    @Override
    public String getMethodPath() {
        return "/stoplist/del";
    }

    @Override
    public void validate(SMSRuStopListDeleteRequest request) throws IllegalArgumentException {
        if (request.getPhone() == null || request.getPhone().isEmpty()) {
            throw new RequestValidationException("Phone number not specified.");
        }
    }

    @Override
    public void appendParams(SMSRuStopListDeleteRequest request, List<NameValuePair> params) {
        params.add(new BasicNameValuePair("stoplist_phone", request.getPhone()));
    }

    @Override
    public SMSRuApiResponse parseResponse(SMSRuStopListDeleteRequest request, String responseStr) {
        final SMSRuApiResponse response = new SMSRuApiResponse();
        final StringTokenizer tokenizer = tokenizeResponse(responseStr);
        parseAndSetStatus(request, response, tokenizer);
        return response;
    }
}
