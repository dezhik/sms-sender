package ru.dezhik.sms.sender.api.smsru.send;

import java.util.Collection;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import ru.dezhik.sms.sender.RequestValidationException;

/**
 * @author ilya.dezhin
 */
public class SMSRuSendHandler extends AbstractSMSRuSendHandler<SMSRuSendRequest> {
    private static final String JOIN_DELIMITER = ",";

    @Override
    public void validate(SMSRuSendRequest request) throws IllegalArgumentException {
        super.validate(request);

        if (request.receivers == null || request.receivers.size() == 0) {
            throw new RequestValidationException("SMS must have at least one receiver.");
        }

        if (request.text == null || request.text.isEmpty()) {
            throw new RequestValidationException("SMS must have a text.");
        }
    }

    @Override
    public void appendParams(SMSRuSendRequest request, List<NameValuePair> params) {
        super.appendParams(request, params);
        params.add(new BasicNameValuePair("text", request.text));
        params.add(new BasicNameValuePair("to", join(request.receivers)));
    }

    private String join(Collection<String> list) {
        final StringBuilder sb = new StringBuilder();
        int n = 0;
        for (String str : list) {
            sb.append(str);
            if (n++ < list.size() - 1) {
                sb.append(JOIN_DELIMITER);
            }
        }
        return sb.toString();
    }
}
