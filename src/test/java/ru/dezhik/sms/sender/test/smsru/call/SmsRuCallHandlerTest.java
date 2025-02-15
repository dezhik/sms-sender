package ru.dezhik.sms.sender.test.smsru.call;

import org.junit.Assert;
import org.junit.Test;
import ru.dezhik.sms.sender.api.InvocationStatus;
import ru.dezhik.sms.sender.api.SenderStatus;
import ru.dezhik.sms.sender.api.smsru.call.SmsRuCallHandler;
import ru.dezhik.sms.sender.api.smsru.call.SmsRuCallRequest;
import ru.dezhik.sms.sender.api.smsru.call.SmsRuCallResponse;
import ru.dezhik.sms.sender.test.smsru.SMSRuIntegrationTest;

public class SmsRuCallHandlerTest extends SMSRuIntegrationTest {

    @Test
    public void validateCorrectPhoneNumbers() {
        final String[] testStrings = {
                /* Following are valid phone number examples */
                "(123)4567890", "1234567890", "123-456-7890", "(123)456-7890",
                "+71234567890", "+7-123-456-78-90", "+7-123-456-7890"
        };

        final SmsRuCallHandler handler = new SmsRuCallHandler();
        for (String phoneNumber : testStrings) {
            handler.validate(new SmsRuCallRequest(phoneNumber));
        }
    }

    @Test
    public void validateInvalidPhoneNumbers() {
        final String[] testStrings = {
                /* Invalid phone number examples */
                "", null, "123"
        };

        for (String invalidPhone : testStrings) {
            SmsRuCallRequest req = new SmsRuCallRequest(invalidPhone);
            SmsRuCallResponse resp = senderService.execute(req);

            Assert.assertNull(resp);
            Assert.assertEquals(InvocationStatus.VALIDATION_ERROR, req.getStatus());
        }
    }

    @Test
    public void integration() {
        SmsRuCallRequest req = new SmsRuCallRequest(senderConfiguration.getTestPhoneNumber());
        SmsRuCallResponse resp = senderService.execute(req);
        Assert.assertNotNull(resp);

        Assert.assertEquals(SenderStatus.OK, resp.getResponseStatus());
        Assert.assertNull(resp.getStatusText());
        Assert.assertNotNull(resp.getCallId());
        Assert.assertNotNull(resp.getUserCode());

        Assert.assertEquals(InvocationStatus.SUCCESS, req.getStatus());
    }
}
