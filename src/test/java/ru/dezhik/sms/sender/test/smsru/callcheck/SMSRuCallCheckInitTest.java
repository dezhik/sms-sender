package ru.dezhik.sms.sender.test.smsru.callcheck;

import org.junit.Assert;
import org.junit.Test;
import ru.dezhik.sms.sender.api.smsru.SMSRuResponseStatus;
import ru.dezhik.sms.sender.api.smsru.callcheck.SmsRuCallCheckGetStatusRequest;
import ru.dezhik.sms.sender.api.smsru.callcheck.SmsRuCallCheckGetStatusResponse;
import ru.dezhik.sms.sender.api.smsru.callcheck.SmsRuCallCheckInitRequest;
import ru.dezhik.sms.sender.api.smsru.callcheck.SmsRuCallCheckInitResponse;
import ru.dezhik.sms.sender.test.smsru.SMSRuIntegrationTest;

public class SMSRuCallCheckInitTest extends SMSRuIntegrationTest {

    @Test
    public void invalidCheckId() {
        SmsRuCallCheckGetStatusResponse response = senderService.execute(
                new SmsRuCallCheckGetStatusRequest("1234")
        );
        Assert.assertNotNull(response);
        Assert.assertTrue(response.isValid());
        Assert.assertEquals(SMSRuResponseStatus.CALLCHECK_EXPIRED_OR_INCORRECT, response.getCheckStatus());
        Assert.assertNotNull(response.getCheckStatusText());
    }

    @Test
    public void fullCallCheckId() {
        SmsRuCallCheckInitResponse initResponse = senderService.execute(
                new SmsRuCallCheckInitRequest(senderConfiguration.getTestPhoneNumber())
        );

        Assert.assertNotNull(initResponse);
        Assert.assertNotNull(initResponse.getCallCheckId());
        Assert.assertNotNull(initResponse.getCallPhone());
        Assert.assertNotNull(initResponse.getCallPhonePretty());
        Assert.assertNotNull(initResponse.getCallPhoneHtml());

        SmsRuCallCheckGetStatusResponse checkResponse = senderService.execute(
                new SmsRuCallCheckGetStatusRequest(initResponse.getCallCheckId())
        );
        Assert.assertNotNull(checkResponse);
        Assert.assertTrue(checkResponse.isValid());
        Assert.assertEquals(SMSRuResponseStatus.CALLCHECK_PHONE_NOT_CONFIRMED, checkResponse.getCheckStatus());
        Assert.assertNotNull(checkResponse.getCheckStatusText());
    }
}
