package ru.dezhik.sms.sender.test.smsru;

import org.junit.Assert;
import org.junit.Test;

import ru.dezhik.sms.sender.api.InvocationStatus;
import ru.dezhik.sms.sender.RequestValidationException;
import ru.dezhik.sms.sender.api.smsru.SMSRuResponseStatus;
import ru.dezhik.sms.sender.api.smsru.cost.SMSRuCostHandler;
import ru.dezhik.sms.sender.api.smsru.cost.SMSRuCostRequest;
import ru.dezhik.sms.sender.api.smsru.cost.SMSRuCostResponse;

/**
 * @author ilya.dezhin
 */
public class SMSRuCostHandlerTest {
    private static final String DEFAULT_TEXT = "Hello world!";
    private static final String DEFAULT_RECEIVER = "+79101112233";

    private SMSRuCostHandler handler = new SMSRuCostHandler();

    @Test(expected = RequestValidationException.class)
    public void validateNullReceiverTest() {
        SMSRuCostRequest request = new SMSRuCostRequest();
        handler.validate(request);
    }

    @Test(expected = RequestValidationException.class)
    public void validateEmptyReceiverTest() {
        SMSRuCostRequest request = new SMSRuCostRequest();
        request.setReceiver("");
        handler.validate(request);
    }

    @Test(expected = RequestValidationException.class)
    public void validateNullTextTest() {
        SMSRuCostRequest request = new SMSRuCostRequest();
        request.setReceiver(DEFAULT_RECEIVER);
        handler.validate(request);
    }

    @Test(expected = RequestValidationException.class)
    public void validateEmptyTextTest() {
        SMSRuCostRequest request = new SMSRuCostRequest();
        request.setReceiver(DEFAULT_RECEIVER);
        request.setText("");
        handler.validate(request);
    }

    @Test
    public void validateSuccessTest() {
        SMSRuCostRequest request = new SMSRuCostRequest();
        request.setReceiver(DEFAULT_RECEIVER);
        request.setText(DEFAULT_TEXT);
        handler.validate(request);
    }

    @Test
    public void parseSuccessCostResponseTest() {
        final SMSRuCostRequest request = new SMSRuCostRequest();
        final SMSRuCostResponse response = handler.parseResponse(request, "100\n1.62\n1");
        Assert.assertEquals(InvocationStatus.QUEUED, request.getStatus());
        Assert.assertEquals(new Double(1.62), response.getPrice());
        Assert.assertEquals(new Integer(1), response.getSmsNeeded());
    }

    @Test
    public void parseCostWithWrongReceiverResponseTest() {
        final SMSRuCostRequest request = new SMSRuCostRequest();
        final SMSRuCostResponse response = handler.parseResponse(request, "202");
        Assert.assertEquals(InvocationStatus.QUEUED, request.getStatus());
        Assert.assertEquals(SMSRuResponseStatus.SENDER_ERROR, response.getResponseStatus());
        Assert.assertNull(response.getPrice());
        Assert.assertNull(response.getSmsNeeded());
    }
}
