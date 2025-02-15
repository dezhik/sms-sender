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
import ru.dezhik.sms.sender.api.smsru.SMSRuResponseStatus;

import java.io.IOException;
import java.util.List;

public class SmsRuCallCheckGetStatusHandler
        extends AbstractSMSRuApiHandler<SmsRuCallCheckGetStatusRequest, SmsRuCallCheckGetStatusResponse> {

    private final JsonFactory factory = JsonFactory.builder()
            .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
            .build();
    @Override
    public String getMethodPath() {
        return "/callcheck/status";
    }

    @Override
    public void validate(SmsRuCallCheckGetStatusRequest request) throws IllegalArgumentException {
        if (request.getCallCheckId() == null || request.getCallCheckId().trim().isEmpty()) {
            throw new RequestValidationException("Non empty callcheck id is required.");
        }
    }

    @Override
    public void appendParams(SmsRuCallCheckGetStatusRequest request, List<NameValuePair> params) {
        params.add(new BasicNameValuePair("check_id", request.getCallCheckId()));
        params.add(new BasicNameValuePair("json", "1"));
    }

    @Override
    public SmsRuCallCheckGetStatusResponse parseResponse(SmsRuCallCheckGetStatusRequest request, String responseStr) {
        final SmsRuCallCheckGetStatusResponse response = new SmsRuCallCheckGetStatusResponse();
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
                } else if (fName.equals("check_status")) {
                    response.setCheckStatus(SMSRuResponseStatus.forValue(parser.getIntValue()));
                } else if (fName.equals("check_status_text")) {
                    response.setCheckStatusText(parser.getText());
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
