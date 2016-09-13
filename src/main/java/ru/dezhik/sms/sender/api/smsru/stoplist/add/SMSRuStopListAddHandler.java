package ru.dezhik.sms.sender.api.smsru.stoplist.add;

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
public class SMSRuStopListAddHandler extends AbstractSMSRuApiHandler<SMSRuStopListAddRequest, SMSRuApiResponse> {
    @Override
    public String getMethodPath() {
        return "/stoplist/add";
    }

    @Override
    public void validate(SMSRuStopListAddRequest request) throws IllegalArgumentException {
        if (request.getPhone() == null || request.getPhone().isEmpty()) {
            throw new RequestValidationException("Phone number not specified.");
        }

        if (request.getComment() == null || request.getComment().isEmpty()) {
            throw new RequestValidationException("You can't add a number to a stop list without comment.");
        }
    }

    @Override
    public void appendParams(SMSRuStopListAddRequest request, List<NameValuePair> params) {
        params.add(new BasicNameValuePair("stoplist_phone", request.getPhone()));
        params.add(new BasicNameValuePair("stoplist_text", request.getComment()));
    }

    @Override
    public SMSRuApiResponse parseResponse(SMSRuStopListAddRequest request, String responseStr) {
        final SMSRuApiResponse response = new SMSRuApiResponse();
        final StringTokenizer tokenizer = tokenizeResponse(responseStr);
        parseAndSetStatus(request, response, tokenizer);
        return response;
    }
}
