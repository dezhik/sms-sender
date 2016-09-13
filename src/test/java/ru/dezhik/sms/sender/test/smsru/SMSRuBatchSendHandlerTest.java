package ru.dezhik.sms.sender.test.smsru;

import org.junit.Assert;
import org.junit.Test;

import ru.dezhik.sms.sender.api.InvocationStatus;
import ru.dezhik.sms.sender.RequestValidationException;
import ru.dezhik.sms.sender.api.smsru.Pair;
import ru.dezhik.sms.sender.api.smsru.SMSRuResponseStatus;
import ru.dezhik.sms.sender.api.smsru.send.SMSRuBatchSendHandler;
import ru.dezhik.sms.sender.api.smsru.send.SMSRuBatchSendRequest;
import ru.dezhik.sms.sender.api.smsru.send.SMSRuSendResponse;

/**
 * {@link SMSRuBatchSendHandler#parseResponse} function is not covered with tests because
 * it is inherited from {@link ru.dezhik.sms.sender.api.smsru.send.AbstractSMSRuSendHandler}
 * and is covered in {@link SMSRuSendHandlerTest}
 * @author ilya.dezhin
 */
public class SMSRuBatchSendHandlerTest {
    private static final String DEFAULT_TEXT = "Hello world!";
    private static final String DEFAULT_RECEIVER = "+79101112233";

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
        request.addReceiverAndTextPair(new Pair<String, String>("+79101112234", null));
        handler.validate(request);
    }

    @Test(expected = RequestValidationException.class)
    public void validateBatchRequestWithEmptyTextTest() {
        SMSRuBatchSendRequest request = new SMSRuBatchSendRequest();
        request.addReceiverAndTextPair(new Pair<String, String>(DEFAULT_RECEIVER, DEFAULT_TEXT));
        request.addReceiverAndTextPair(new Pair<String, String>("+79101112234", ""));
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

    @Test
    public void parsingEmptyResponseTest() {
        final SMSRuBatchSendRequest request = new SMSRuBatchSendRequest();
        final SMSRuSendResponse response = handler.parseResponse(request, "");
        Assert.assertEquals(InvocationStatus.RESPONSE_PARSING_ERROR, request.getStatus());
    }

    @Test
    public void parsingSuccessfulBatchSendResponseTest() {
        final SMSRuBatchSendRequest request = new SMSRuBatchSendRequest();
        final SMSRuSendResponse response = handler.parseResponse(
                request, "100\n201630-1000001\n201630-1000002\nbalance=18.38");
        Assert.assertEquals(InvocationStatus.QUEUED, request.getStatus());
        Assert.assertEquals(SMSRuResponseStatus.IN_QUEUE, response.getResponseStatus());
        Assert.assertEquals(new Double(18.38d), response.getBalance());
        Assert.assertEquals(2, response.getMsgIds().size());
        Assert.assertEquals("201630-1000001", response.getMsgIds().get(0));
        Assert.assertEquals("201630-1000002", response.getMsgIds().get(1));
    }

    /**
     * Response returned while executing SMS sending with
     * {@link ru.dezhik.sms.sender.api.smsru.send.AbstractSMSRuSendRequest#testSendingEnabled} enabled
     * doesn't contain info about actual balance.
     */
    @Test
    public void parsingSuccessfulBatchTestSendResponseTest() {
        final SMSRuBatchSendRequest request = new SMSRuBatchSendRequest();
        final SMSRuSendResponse response = handler.parseResponse(request, "100\n000-00000\n000-00000");
        Assert.assertEquals(SMSRuResponseStatus.IN_QUEUE, response.getResponseStatus());
        Assert.assertEquals(2, response.getMsgIds().size());
        Assert.assertEquals("000-00000", response.getMsgIds().get(0));
        Assert.assertEquals("000-00000", response.getMsgIds().get(1));
    }
}
