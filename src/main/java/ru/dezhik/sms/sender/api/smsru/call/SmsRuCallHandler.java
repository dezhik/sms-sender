package ru.dezhik.sms.sender.api.smsru.call;

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
import java.util.regex.Pattern;

/**
 * @author ilya.dezhin
 */
public class SmsRuCallHandler
        extends AbstractSMSRuApiHandler<SmsRuCallRequest, SmsRuCallResponse> {

    private static final Pattern PHONE_VALIDATOR = Pattern.compile("(?:\\(\\d{1,3}\\)|\\d{1,3}[-]*)\\d{3}[-]*\\d{4}");

    private final JsonFactory factory = JsonFactory.builder()
            .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
            .build();

    @Override
    public String getMethodPath() {
        return "/code/call";
    }

    @Override
    public void validate(SmsRuCallRequest request) throws IllegalArgumentException {
        if (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty()) {
            throw new RequestValidationException("SMS code via call request requires a non empty phone.");
        }
        if (!PHONE_VALIDATOR.matcher(request.getPhoneNumber()).find()) {
            throw new RequestValidationException(String.format("Invalid phone format in %s", request.getPhoneNumber()));
        }
    }

    @Override
    public void appendParams(SmsRuCallRequest request, List<NameValuePair> params) {

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            params.add(new BasicNameValuePair("phone", request.getPhoneNumber()));
        }

        if (request.getUserIp() != null && !request.getUserIp().isEmpty()) {
            params.add(new BasicNameValuePair("ip", request.getUserIp()));
        }

        if (config.getPartnerId() != null && !config.getPartnerId().isEmpty()) {
            params.add(new BasicNameValuePair("partner_id", config.getPartnerId()));
        }
    }

    /**
     *{
     *     "status": "OK", // Запрос выполнен успешно (нет ошибок в авторизации, проблем с отправителем, итд...)
     *     "code": "1435", // Последние 4 цифры номера, с которого мы совершим звонок пользователю
     *     "call_id": "000000-10000000", // ID звонка
     *     "cost": 0.4, // Стоимость звонка
     *     "balance": 4122.56 // Ваш баланс после совершения звонка
     * }
     *
     *
     */
    @Override
    public SmsRuCallResponse parseResponse(SmsRuCallRequest request, String responseStr) {
        final SmsRuCallResponse response = new SmsRuCallResponse();
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
                } else if (fName.equals("status_text")) {
                    response.setStatusText(parser.getText());
                } else if (fName.equals("code")) {
                    response.setUserCode(parser.getText());
                } else if (fName.equals("call_id")) {
                    response.setCallId(parser.getText());
                } else if (fName.equals("cost")) {
                    response.setCost(parseDoubleSafe(parser.getText()));
                } else if (fName.equals("balance")) {
                    response.setBalance(parseDoubleSafe(parser.getText()));
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
