package ru.dezhik.sms.sender.api.smsru.send;

import java.util.Collection;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import ru.dezhik.sms.sender.RequestValidationException;
import ru.dezhik.sms.sender.api.smsru.Pair;

/**
 * @author ilya.dezhin
 */
public class SMSRuBatchSendHandler extends AbstractSMSRuSendHandler<SMSRuBatchSendRequest> {
    @Override
    public void validate(SMSRuBatchSendRequest request) throws IllegalArgumentException {
        super.validate(request);

        final Collection<Pair<String, String>> messages = request.getMessages();
        if (messages == null || messages.isEmpty()) {
            throw new RequestValidationException("No receiver/text pairs.");
        }

        for (Pair<String, String> message : messages) {
            if (message.getA() == null || message.getA().isEmpty()) {
                throw new RequestValidationException("Receiver/text pairs contain null/empty receiver.");
            }
            if (message.getB() == null || message.getB().isEmpty()) {
                throw new RequestValidationException("Receiver/text pairs contain null/empty text.");
            }
        }

    }

    @Override
    public void appendParams(SMSRuBatchSendRequest request, List<NameValuePair> params) {
        super.appendParams(request, params);
        for (Pair<String, String> message : request.getMessages()) {
            params.add(new BasicNameValuePair("multi[" + message.getA() + "]", message.getB()));
        }
    }
}
