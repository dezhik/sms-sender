package ru.dezhik.sms.sender.api.smsru.send;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.json.JsonReadFeature;
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

    private final JsonFactory factory = JsonFactory.builder()
            .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
            .build();

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

        params.add(new BasicNameValuePair("json", "1"));
    }

    @Override
    public SMSRuSendResponse parseResponse(Req request, String responseStr) throws IOException {
        final SMSRuSendResponse response = new SMSRuSendResponse();

        try (final JsonParser parser = factory.createParser(responseStr)) {
            if (parser.nextToken() != JsonToken.START_OBJECT) {
                throw new IOException("Expected data to start with an Object");
            }

            while (parser.nextToken() != JsonToken.END_OBJECT) {
                final String fName = parser.currentName();
                parser.nextToken();
                if (fName.equals("sms")) {
                    if (parser.currentToken() == JsonToken.START_OBJECT) {
                        parser.nextToken();

                        final Map<String, SendingResult> resultByPhoneNumber =
                                new HashMap<>();

                        final Set<SMSRuResponseStatus> statuses = new HashSet<>();

                        while (parser.currentToken() != JsonToken.END_OBJECT) {
                            final String receiverPhone = parser.currentName();
                            parser.nextToken();

                            final SendingResult smsSendingResult = new SendingResult();
                            if (parser.currentToken() == JsonToken.START_OBJECT) {
                                parser.nextToken();

                                while (parser.currentToken() != JsonToken.END_OBJECT) {
                                    final String smsFieldName = parser.currentName();
                                    parser.nextToken();

                                    if (smsFieldName.equals("status_code")) {
                                       smsSendingResult.setStatus(SMSRuResponseStatus.forValue(parser.getIntValue()));
                                       statuses.add(smsSendingResult.getStatus());
                                    } else if (smsFieldName.equals("status_text")) {
                                        smsSendingResult.setStatusText(parser.getText());
                                    } else if (smsFieldName.equals("sms_id")) {
                                        smsSendingResult.setSmsId(parser.getText());
                                        response.getMsgIds().add(parser.getText());
                                    }
                                    parser.nextToken();
                                }

                            }
                            resultByPhoneNumber.put(receiverPhone, smsSendingResult);

                            parser.nextToken();
                        }
                        response.setSmsByPhoneNumber(resultByPhoneNumber);

                        // iff all resulting sms entries have the same status, otherwise setting {@link SMSRuResponseStatus#MIXED} status
                        response.setStatus(
                            statuses.size() == 1
                                    ? statuses.stream().findFirst().get()
                                    : SMSRuResponseStatus.MIXED
                        );
                    }
                } else if (fName.equals("balance")) {
                    response.setBalance(parseDoubleSafe(parser.getText()));
                }
            }
        }
        return response;
    }

}
