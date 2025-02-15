package ru.dezhik.sms.sender.api.smsru.callcheck;

import ru.dezhik.sms.sender.api.ApiResponse;
import ru.dezhik.sms.sender.api.SenderStatus;
import ru.dezhik.sms.sender.api.smsru.SMSRuResponseStatus;

import java.util.Arrays;

public class SmsRuCallCheckGetStatusResponse extends ApiResponse<SenderStatus> {
    private static final SMSRuResponseStatus[] VALID_CALLCHECK_STATUSES =
        new SMSRuResponseStatus[] {
            SMSRuResponseStatus.CALLCHECK_PHONE_NOT_CONFIRMED,
            SMSRuResponseStatus.CALLCHECK_PHONE_CONFIRMED,
            SMSRuResponseStatus.CALLCHECK_EXPIRED_OR_INCORRECT
    };

    /**
     * Use {@link #isValid()} to verify status is correct.
     */
    private SMSRuResponseStatus checkStatus;
    /**
     * Detailed status message
     */
    private String checkStatusText;

    public boolean isValid() {
        return checkStatus != null && Arrays.binarySearch(VALID_CALLCHECK_STATUSES, checkStatus) >= 0;
    }

    public SMSRuResponseStatus getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(SMSRuResponseStatus checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getCheckStatusText() {
        return checkStatusText;
    }

    public void setCheckStatusText(String checkStatusText) {
        this.checkStatusText = checkStatusText;
    }
}
