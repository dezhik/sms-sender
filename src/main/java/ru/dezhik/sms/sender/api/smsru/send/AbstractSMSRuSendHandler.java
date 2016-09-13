package ru.dezhik.sms.sender.api.smsru.send;

import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import ru.dezhik.sms.sender.api.InvocationStatus;
import ru.dezhik.sms.sender.RequestValidationException;
import ru.dezhik.sms.sender.api.smsru.AbstractSMSRuApiHandler;
import ru.dezhik.sms.sender.api.smsru.SMSRuResponseStatus;

/**
 * @author ilya.dezhin
 */
public abstract class AbstractSMSRuSendHandler<Req extends AbstractSMSRuSendRequest>
        extends AbstractSMSRuApiHandler<Req, SMSRuSendResponse> {
    private final static String BALANCE_STRING = "balance=";

    @Override
    public String getMethodPath() {
        return "/sms/send";
    }

    @Override
    public void validate(Req request) {
        if (request.getPostponedSendingTimeMs() != null &&
                request.getPostponedSendingTimeMs() > System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)) {
            throw new RequestValidationException("Sending can't be scheduled beyond 7 days period.");
        }
    }

    @Override
    public void appendParams(Req request, List<NameValuePair> params) {
        if (config.isTestSendingEnabled() || request.isTestSendingEnabled()) {
            params.add(new BasicNameValuePair("test", "1"));
        }

        if (config.isTranslitEnabled() || request.isTranslitEnabled()) {
            params.add(new BasicNameValuePair("translit", "1"));
        }

        if (request.getFrom() != null && !request.getFrom().isEmpty()) {
            params.add(new BasicNameValuePair("from", request.from));
        } else if (config.getFromName() != null && !request.getFrom().isEmpty()) {
            params.add(new BasicNameValuePair("from", config.getFromName()));
        }

        if (config.getPartnerId() != null && !config.getPartnerId().isEmpty()) {
            params.add(new BasicNameValuePair("partner_id", config.getPartnerId()));
        }

        if (request.getPostponedSendingTimeMs() != null) {
            params.add(new BasicNameValuePair("time", String.valueOf(request.getPostponedSendingTimeMs() / 1000)));
        }
    }

    @Override
    public SMSRuSendResponse parseResponse(Req request, String responseStr) {
        final SMSRuSendResponse response = new SMSRuSendResponse();
        final StringTokenizer tokens = tokenizeResponse(responseStr);

        parseAndSetStatus(request, response, tokens);

        if (request.getStatus() == InvocationStatus.RESPONSE_PARSING_ERROR
                || response.getResponseStatus() != SMSRuResponseStatus.IN_QUEUE) {
            return response;
        }

        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if (tokens.hasMoreTokens() || !token.startsWith(BALANCE_STRING)) {
                response.addMsgId(token);
            } else {
                try {
                    response.setBalance(Double.parseDouble(token.substring(BALANCE_STRING.length()).trim()));
                } catch (NumberFormatException e) {
                }
            }

        }
        return response;
    }

}
