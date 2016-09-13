package ru.dezhik.sms.sender.test.smsru;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import ru.dezhik.sms.sender.api.InvocationStatus;
import ru.dezhik.sms.sender.RequestValidationException;
import ru.dezhik.sms.sender.api.smsru.SMSRuResponseStatus;
import ru.dezhik.sms.sender.api.smsru.send.SMSRuSendHandler;
import ru.dezhik.sms.sender.api.smsru.send.SMSRuSendRequest;
import ru.dezhik.sms.sender.api.smsru.send.SMSRuSendResponse;

/**
 *
 * @author ilya.dezhin
 */
public class SMSRuSendHandlerTest {
    private static final String DEFAULT_TEXT = "Hello world!";
    private static final String DEFAULT_RECEIVER = "+79101112233";

    private SMSRuSendHandler handler = new SMSRuSendHandler();

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
    public void parsingSuccessSendingOfSingleSMSTest() {
        final SMSRuSendRequest request = new SMSRuSendRequest();
        final SMSRuSendResponse response = handler.parseResponse(
                request,
                "100\n201630-1000000\nbalance=0.53");
        Assert.assertEquals(InvocationStatus.QUEUED, request.getStatus());
        Assert.assertEquals(SMSRuResponseStatus.IN_QUEUE, response.getResponseStatus());
        Assert.assertEquals(new Double(0.53d), response.getBalance());
        Assert.assertEquals(1, response.getMsgIds().size());
        Assert.assertEquals("201630-1000000", response.getMsgIds().get(0));
    }

    /**
     * Response returned while executing SMS sending with
     * {@link ru.dezhik.sms.sender.api.smsru.send.AbstractSMSRuSendRequest#testSendingEnabled} enabled
     * doesn't contain info about actual balance.
     */
    @Test
    public void parsingSuccessfulTestSendResponseTest() {
        final SMSRuSendRequest request = new SMSRuSendRequest();
        final SMSRuSendResponse response = handler.parseResponse(
                request,
                "100\n000-00000");
        Assert.assertEquals(InvocationStatus.QUEUED, request.getStatus());
        Assert.assertEquals(SMSRuResponseStatus.IN_QUEUE, response.getResponseStatus());
        Assert.assertEquals(1, response.getMsgIds().size());
        Assert.assertEquals("000-00000", response.getMsgIds().get(0));
    }

    @Test
    public void parsingWrongApiIdResponseTest() {
        final SMSRuSendRequest request = new SMSRuSendRequest();
        SMSRuSendResponse response = handler.parseResponse(request, "200\n");
        Assert.assertEquals(InvocationStatus.QUEUED, request.getStatus());
        Assert.assertEquals(SMSRuResponseStatus.WRONG_API_ID, response.getResponseStatus());
        Assert.assertEquals(0, response.getMsgIds().size());
        Assert.assertNull(response.getBalance());
    }
}
