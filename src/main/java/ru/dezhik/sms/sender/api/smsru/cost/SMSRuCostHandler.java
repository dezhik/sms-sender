package ru.dezhik.sms.sender.api.smsru.cost;

import java.util.List;
import java.util.StringTokenizer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import ru.dezhik.sms.sender.RequestValidationException;
import ru.dezhik.sms.sender.api.smsru.AbstractSMSRuApiHandler;

/**
 * @author ilya.dezhin
 */
public class SMSRuCostHandler extends AbstractSMSRuApiHandler<SMSRuCostRequest, SMSRuCostResponse> {
    @Override
    public String getMethodPath() {
        return "/sms/cost";
    }

    @Override
    public void validate(SMSRuCostRequest request) throws IllegalArgumentException {
        if (request.receiver == null || request.receiver.isEmpty()) {
            throw new RequestValidationException("SMS must have a receiver.");
        }

        if (request.text == null || request.text.isEmpty()) {
            throw new RequestValidationException("SMS must have a text.");
        }
    }

    @Override
    public void appendParams(SMSRuCostRequest request, List<NameValuePair> params) {
        params.add(new BasicNameValuePair("to", request.getReceiver()));
        params.add(new BasicNameValuePair("text", request.getText()));
        if (request.isTranslit()) {
            params.add(new BasicNameValuePair("translit", "1"));
        }
    }

    @Override
    public SMSRuCostResponse parseResponse(SMSRuCostRequest request, String responseStr) {
        final StringTokenizer tokens = tokenizeResponse(responseStr);
        final SMSRuCostResponse response = new SMSRuCostResponse();
        parseAndSetStatus(request, response, tokens);
        response.setPrice(parseDoubleSafe(request, tokens));
        response.setSmsNeeded(parseIntSafe(request, tokens));
        return response;
    }
}
