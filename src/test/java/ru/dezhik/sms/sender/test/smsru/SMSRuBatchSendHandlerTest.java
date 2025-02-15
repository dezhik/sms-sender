package ru.dezhik.sms.sender.test.smsru;

import org.junit.Assert;
import org.junit.Test;

import ru.dezhik.sms.sender.api.InvocationStatus;
import ru.dezhik.sms.sender.RequestValidationException;
import ru.dezhik.sms.sender.api.smsru.Pair;
import ru.dezhik.sms.sender.api.smsru.SMSRuResponseStatus;
import ru.dezhik.sms.sender.api.smsru.send.*;

import java.io.IOException;
import java.util.Map;

/**
 * {@link SMSRuBatchSendHandler#parseResponse} function is not covered with tests because
 * it is inherited from {@link ru.dezhik.sms.sender.api.smsru.send.AbstractSMSRuSendHandler}
 * and is covered in {@link SMSRuSendHandlerTest}
 * @author ilya.dezhin
 */
public class SMSRuBatchSendHandlerTest {
    private static final String DEFAULT_TEXT = "Hello world!";
    private static final String DEFAULT_RECEIVER = "+79101112233";
    private static final String ADDITIONAL_RECEIVER = "+74993221627";

    private SMSRuBatchSendHandler handler = new SMSRuBatchSendHandler();

    @Test(expected = RequestValidationException.class)
    public void validateEmptyBatchRequestTest() {
        SMSRuBatchSendRequest request = new SMSRuBatchSendRequest();
        handler.validate(request);
    }

    @Test(expected = RequestValidationException.class)
    public void validateBatchRequestWithNullTextTest() {
        SMSRuBatchSendRequest request = new SMSRuBatchSendRequest();
        request.addReceiverAndTextPair(new Pair<String, String>(DEFAULT_RECEIVER, DEFAULT_TEXT));
        request.addReceiverAndTextPair(new Pair<String, String>(ADDITIONAL_RECEIVER, null));
        handler.validate(request);
    }

    @Test(expected = RequestValidationException.class)
    public void validateBatchRequestWithEmptyTextTest() {
        SMSRuBatchSendRequest request = new SMSRuBatchSendRequest();
        request.addReceiverAndTextPair(new Pair<String, String>(DEFAULT_RECEIVER, DEFAULT_TEXT));
        request.addReceiverAndTextPair(new Pair<String, String>(ADDITIONAL_RECEIVER, ""));
        handler.validate(request);
    }

    @Test(expected = RequestValidationException.class)
    public void validateBatchRequestWithNullReceiverTest() {
        SMSRuBatchSendRequest request = new SMSRuBatchSendRequest();
        request.addReceiverAndTextPair(new Pair<String, String>(DEFAULT_RECEIVER, DEFAULT_TEXT));
        request.addReceiverAndTextPair(new Pair<String, String>(null, DEFAULT_TEXT));
        handler.validate(request);
    }

    @Test(expected = RequestValidationException.class)
    public void validateBatchRequestWithEmptyReceiverTest() {
        SMSRuBatchSendRequest request = new SMSRuBatchSendRequest();
        request.addReceiverAndTextPair(new Pair<String, String>(DEFAULT_RECEIVER, DEFAULT_TEXT));
        request.addReceiverAndTextPair(new Pair<String, String>("", DEFAULT_TEXT));
        handler.validate(request);
    }

    @Test(expected = IOException.class)
    public void parsingEmptyResponseTest() throws IOException {
        final SMSRuBatchSendRequest request = new SMSRuBatchSendRequest();
        final SMSRuSendResponse response = handler.parseResponse(request, "");
    }

    @Test
    public void parsingSuccessfulBatchSendResponseTest() throws IOException {
        final SMSRuBatchSendRequest request = new SMSRuBatchSendRequest();
        final SMSRuSendResponse response = handler.parseResponse(
                request,
                "{\n" +
                        "    \"status\": \"OK\",\n" +
                        "    \"status_code\": 100,\n" +
                        "    \"sms\": {\n" +
                        "        \"" + DEFAULT_RECEIVER + "\": {\n" +
                        "            \"status\": \"OK\",\n" +
                        "            \"status_code\": 100,\n" +
                        "            \"sms_id\": \"000000-10000000\"\n" +
                        "        },\n" +
                        "        \"" + ADDITIONAL_RECEIVER + "\": {\n" +
                        "            \"status\": \"OK\",\n" +
                        "            \"status_code\": 100,\n" +
                        "            \"sms_id\": \"000000-20000000\"\n" +
                        "        }\n" +
                        "    } ,\n" +
                        "    \"balance\": 1122.55\n" +
                        "}"
                );
        Assert.assertEquals(InvocationStatus.QUEUED, request.getStatus());
        Assert.assertEquals(SMSRuResponseStatus.IN_QUEUE, response.getStatus());
        Assert.assertEquals(Double.valueOf(1122.55d), response.getBalance());
        Assert.assertEquals(2, response.getMsgIds().size());
        Assert.assertEquals("000000-10000000", response.getMsgIds().get(0));
        Assert.assertEquals("000000-20000000", response.getMsgIds().get(1));

        final Map<String, SendingResult> sendingResults = response.getSmsByPhoneNumber();
        Assert.assertEquals(2, sendingResults.size());

        SendingResult defaultReceiverResult = sendingResults.get(DEFAULT_RECEIVER);
        Assert.assertNotNull(defaultReceiverResult);
        Assert.assertEquals(SMSRuResponseStatus.IN_QUEUE, defaultReceiverResult.getStatus());
        Assert.assertEquals("000000-10000000", defaultReceiverResult.getSmsId());
        Assert.assertNull(defaultReceiverResult.getStatusText());

        SendingResult additionalReceiverResult = sendingResults.get(ADDITIONAL_RECEIVER);
        Assert.assertNotNull(additionalReceiverResult);
        Assert.assertEquals(SMSRuResponseStatus.IN_QUEUE, additionalReceiverResult.getStatus());
        Assert.assertEquals("000000-20000000", additionalReceiverResult.getSmsId());
        Assert.assertNull(defaultReceiverResult.getStatusText());
    }

    /**
     * Response returned while executing SMS sending with
     * {@link ru.dezhik.sms.sender.api.smsru.send.AbstractSMSRuSendRequest#isTestSendingEnabled()} enabled
     * doesn't contain info about actual balance.
     */
    @Test
    public void parsingMixedBatchTestSendResponseTest() throws IOException {
        final SMSRuBatchSendRequest request = new SMSRuBatchSendRequest();
        final SMSRuSendResponse response = handler.parseResponse(request,
                "{\n" +
                        "    \"status\": \"OK\",\n" +
                        "    \"status_code\": 100,\n" +
                        "    \"sms\": {\n" +
                        "        \"" + DEFAULT_RECEIVER + "\": {\n" +
                        "            \"status\": \"OK\",\n" +
                        "            \"status_code\": 100,\n" +
                        "            \"sms_id\": \"000000-10000000\"\n" +
                        "        },\n" +
                        "        \"" + ADDITIONAL_RECEIVER + "\": {\n" +
                        "            \"status\": \"ERROR\",\n" +
                        "            \"status_code\": 207,\n" +
                        "            \"status_text\": \"На этот номер (или один из номеров) нельзя отправлять сообщения, " +
                        "               либо указано более 100 номеров в списке получателей\"\n" +
                        "        }\n" +
                        "    } ,\n" +
                        "    \"balance\": 1122.56\n" +
                        "}"
                );
        Assert.assertEquals(InvocationStatus.QUEUED, request.getStatus());
        Assert.assertEquals(SMSRuResponseStatus.MIXED, response.getStatus());
        Assert.assertEquals(1, response.getMsgIds().size());
        Assert.assertEquals("000000-10000000", response.getMsgIds().get(0));
        Assert.assertEquals(Double.valueOf(1122.56d), response.getBalance());

        final Map<String, SendingResult> sendingResults = response.getSmsByPhoneNumber();
        Assert.assertEquals(2, sendingResults.size());

        SendingResult defaultReceiverResult = sendingResults.get(DEFAULT_RECEIVER);
        Assert.assertNotNull(defaultReceiverResult);
        Assert.assertEquals(SMSRuResponseStatus.IN_QUEUE, defaultReceiverResult.getStatus());
        Assert.assertEquals("000000-10000000", defaultReceiverResult.getSmsId());
        Assert.assertNull(defaultReceiverResult.getStatusText());

        SendingResult additionalReceiverResult = sendingResults.get(ADDITIONAL_RECEIVER);
        Assert.assertNotNull(additionalReceiverResult);
        Assert.assertEquals(SMSRuResponseStatus.RECEIVER_ERROR, additionalReceiverResult.getStatus());
        Assert.assertNull(additionalReceiverResult.getSmsId());
        Assert.assertNotNull(additionalReceiverResult.getStatusText());
    }

    @Test(expected = IOException.class)
    public void parsingIncompatibleResponseTest() throws IOException {
        final SMSRuBatchSendRequest request = new SMSRuBatchSendRequest();
        SMSRuSendResponse response = handler.parseResponse(request, "200\n");
    }
}
