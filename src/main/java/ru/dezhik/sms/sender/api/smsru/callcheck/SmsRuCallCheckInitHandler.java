package ru.dezhik.sms.sender.api.smsru.callcheck;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import ru.dezhik.sms.sender.RequestValidationException;
import ru.dezhik.sms.sender.api.InvocationStatus;
import ru.dezhik.sms.sender.api.SenderStatus;
import ru.dezhik.sms.sender.api.smsru.AbstractSMSRuApiHandler;

import java.io.IOException;
import java.util.List;

public class SmsRuCallCheckInitHandler
        extends AbstractSMSRuApiHandler<SmsRuCallCheckInitRequest, SmsRuCallCheckInitResponse> {

    private final JsonFactory factory = JsonFactory.builder()
            .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
            .build();
    @Override
    public String getMethodPath() {
        return "/callcheck/add";
    }

    @Override
    public void validate(SmsRuCallCheckInitRequest request) throws IllegalArgumentException {
        if (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty()) {
            throw new RequestValidationException("SMS.ru callcheck init request requires a non empty phone.");
        }
    }

    @Override
    public void appendParams(SmsRuCallCheckInitRequest request, List<NameValuePair> params) {
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            params.add(new BasicNameValuePair("phone", request.getPhoneNumber()));
        }

        params.add(new BasicNameValuePair("json", "1"));
    }

    @Override
    public SmsRuCallCheckInitResponse parseResponse(SmsRuCallCheckInitRequest request, String responseStr) {
        final SmsRuCallCheckInitResponse response = new SmsRuCallCheckInitResponse();
        try (final JsonParser parser = factory.createParser(responseStr)) {

            if (parser.nextToken() != JsonToken.START_OBJECT) {
                throw new IOException("Expected data to start with an Object");
            }
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                final String fName = parser.currentName();
                parser.nextToken();
                if (fName.equals("status")) {
                    if (parser.getText().equalsIgnoreCase("ok")) {
                        response.setStatus(SenderStatus.OK);
                    } else if (parser.getText().equalsIgnoreCase("error")) {
                        response.setStatus(SenderStatus.ERROR);
                    } else {
                        response.setStatus(SenderStatus.PARSING_ERROR);
                    }
                } else if (fName.equals("check_id")) {
                    response.setCallCheckId(parser.getText());
                } else if (fName.equals("call_phone")) {
                    response.setCallPhone(parser.getText());
                } else if (fName.equals("call_phone_pretty")) {
                    response.setCallPhonePretty(parser.getText());
                } else if (fName.equals("call_phone_html")) {
                    response.setCallPhoneHtml(parser.getText());
                }
            }
        } catch (Throwable ex) {
            request.setStatus(InvocationStatus.RESPONSE_PARSING_ERROR);
            request.setException(ex);

            response.setStatus(SenderStatus.PARSING_ERROR);
            return response;
        }

        return response;
    }
}
