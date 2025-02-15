package ru.dezhik.sms.sender.test.smsru;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import ru.dezhik.sms.sender.api.InvocationStatus;
import ru.dezhik.sms.sender.RequestValidationException;
import ru.dezhik.sms.sender.api.smsru.SMSRuResponseStatus;
import ru.dezhik.sms.sender.api.smsru.send.*;

/**
 *
 * @author ilya.dezhin
 */
public class SMSRuSendHandlerTest {
    private static final String DEFAULT_TEXT = "Hello world!";
    private static final String DEFAULT_RECEIVER = "+79101112233";
    private static final String ADDITIONAL_RECEIVER = "+7910123456";
    private static final String SMS_ID = "000000-10000000";

    private final SMSRuSendHandler handler = new SMSRuSendHandler();

    @Test(expected = RequestValidationException.class)
    public void validateEmptyRequestTest() {
        SMSRuSendRequest request = new SMSRuSendRequest();
        handler.validate(request);
    }

    @Test(expected = RequestValidationException.class)
    public void validateRequestWithWrongPostponedTimeTest() {
        SMSRuSendRequest request = new SMSRuSendRequest();
        request.setText(DEFAULT_TEXT);
        request.addReceiver(DEFAULT_RECEIVER);
        request.setPostponedSendingTimeMs(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(8));
        handler.validate(request);
    }

    @Test(expected = RequestValidationException.class)
    public void validateRequestWithEmptyTextTest() {
        SMSRuSendRequest request = new SMSRuSendRequest();
        request.addReceiver(DEFAULT_RECEIVER);
        handler.validate(request);
    }

    @Test(expected = RequestValidationException.class)
    public void validateRequestWithReceiversListTest() {
        SMSRuSendRequest request = new SMSRuSendRequest();
        request.setText(DEFAULT_TEXT);
        request.setPostponedSendingTimeMs(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(4));
        handler.validate(request);
    }

    @Test
    public void validateCorrectPostponedRequestTest() {
        SMSRuSendRequest request = new SMSRuSendRequest();
        request.setText(DEFAULT_TEXT);
        request.addReceiver(DEFAULT_RECEIVER);
        request.setPostponedSendingTimeMs(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(4));
        request.setTranslitEnabled(true);
        handler.validate(request);
    }

    @Test
    public void validateCorrectRequestTest() {
        SMSRuSendRequest request = new SMSRuSendRequest();
        request.setText(DEFAULT_TEXT);
        request.addReceiver(DEFAULT_RECEIVER);
        request.setTranslitEnabled(true);
        handler.validate(request);
    }

    @Test
    public void validateCorrectRequestWithMultipleReceiversTest() {
        SMSRuSendRequest request = new SMSRuSendRequest();
        request.setText(DEFAULT_TEXT);
        request.addReceiver(DEFAULT_RECEIVER);
        request.addReceiver(DEFAULT_RECEIVER);
        handler.validate(request);
    }

    @Test
    public void parsingResponseModerationError() throws IOException {
        final SMSRuSendRequest request = new SMSRuSendRequest();
        final SMSRuSendResponse response = handler.parseResponse(
                request,
                "{\n" +
                        "    \"status\": \"OK\",\n" +
                        "    \"status_code\": 100,\n" +
                        "    \"sms\": {\n" +
                        "        \"" + DEFAULT_RECEIVER + "\": {\n" +
                        "            \"status\": \"ERROR\",\n" +
                        "            \"status_code\": 204,\n" +
                        "            \"status_text\": \"Вы не подключили данного оператора. Подайте заявку " +
                        "через раздел *Отправители* на сайте SMS.RU - https:\\/\\/sms.ru\\/?panel=senders\"\n" +
                        "        }\n" +
                        "    },\n" +
                        "    \"balance\": 100.01\n" +
                        "}");
        Assert.assertEquals(InvocationStatus.QUEUED, request.getStatus());
        Assert.assertEquals(SMSRuResponseStatus.MODERATION_ERROR, response.getStatus());
        Assert.assertEquals(Double.valueOf(100.01d), response.getBalance());
        Assert.assertEquals(0, response.getMsgIds().size());
        Assert.assertNotNull(response.getSmsByPhoneNumber());
        Assert.assertEquals(1, response.getSmsByPhoneNumber().size());

        final SendingResult sendingRes = response.getSmsByPhoneNumber().get(DEFAULT_RECEIVER);
        Assert.assertNotNull(sendingRes);
        Assert.assertEquals(SMSRuResponseStatus.MODERATION_ERROR, sendingRes.getStatus());
        Assert.assertNotNull(sendingRes.getStatusText());
        Assert.assertNull(sendingRes.getSmsId());

    }

    /**
     * Response returned while executing SMS sending with
     * {@link AbstractSMSRuSendRequest#isTestSendingEnabled()} enabled
     * doesn't contain info about actual balance.
     */
    @Test
    public void parsingResponseMultiReceiver() throws IOException {
        final SMSRuSendRequest request = new SMSRuSendRequest();
        final SMSRuSendResponse response = handler.parseResponse(
                request,
                "{\n" +
                        "    \"status\": \"OK\",\n" +
                        "    \"status_code\": 100,\n" +
                        "    \"sms\": {\n" +
                        "        \"" + DEFAULT_RECEIVER + "\": {\n" +
                        "            \"status\": \"OK\",\n" +
                        "            \"status_code\": 100,\n" +
                        "            \"sms_id\": \"" + SMS_ID + "\"\n" +
                        "        },\n" +
                        "        \"" + ADDITIONAL_RECEIVER + "\": {\n" +
                        "            \"status\": \"ERROR\",\n" +
                        "            \"status_code\": 207,\n" +
                        "            \"status_text\": \"На этот номер (или один из номеров) нельзя отправлять сообщения," +
                        " либо указано более 100 номеров в списке получателей\"\n" +
                        "        }\n" +
                        "    } ,\n" +
                        "    \"balance\": 4122.56\n" +
                        "}");
        Assert.assertEquals(InvocationStatus.QUEUED, request.getStatus());
        Assert.assertEquals(SMSRuResponseStatus.MIXED, response.getStatus());
        Assert.assertEquals(1, response.getMsgIds().size());
        Assert.assertEquals(Double.valueOf(4122.56d), response.getBalance());

        final SendingResult firstSmsResult = response.getSmsByPhoneNumber().get(DEFAULT_RECEIVER);
        Assert.assertNotNull(firstSmsResult);
        Assert.assertEquals(SMSRuResponseStatus.IN_QUEUE, firstSmsResult.getStatus());
        Assert.assertEquals(SMS_ID, firstSmsResult.getSmsId());
        Assert.assertNull(firstSmsResult.getStatusText());

        final SendingResult secondSmsResult = response.getSmsByPhoneNumber().get(ADDITIONAL_RECEIVER);
        Assert.assertNotNull(secondSmsResult);
        Assert.assertEquals(SMSRuResponseStatus.RECEIVER_ERROR, secondSmsResult.getStatus());
        Assert.assertNotNull(secondSmsResult.getStatusText());
        Assert.assertNull(secondSmsResult.getSmsId());

        Assert.assertEquals(SMS_ID, response.getMsgIds().get(0));
    }

    @Test(expected = IOException.class)
    public void parsingIncompatibleResponseTest() throws IOException {
        final SMSRuSendRequest request = new SMSRuSendRequest();
        SMSRuSendResponse response = handler.parseResponse(request, "200\n");
    }
}
