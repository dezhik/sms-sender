package ru.dezhik.sms.sender.api.smsru.call;

import ru.dezhik.sms.sender.api.ApiRequest;

/**
 * @author ilya.dezhin
 */
public class SmsRuCallRequest extends ApiRequest<SmsRuCallHandler, SmsRuCallResponse> {

    private String phoneNumber;
    /**
     * Passing value different from -1 enables sms.ru built-in ip rate limiter.
     */
    private String userIp;

    public SmsRuCallRequest(String phoneNumber) {
        if (phoneNumber != null) {
            String stripped = phoneNumber.startsWith("+")
                    ? phoneNumber.substring(1) : phoneNumber;
            this.phoneNumber = stripped.replace("-", "");
        }

        this.userIp = "-1"; // no ip check needed
    }

    public SmsRuCallRequest(String phoneNumber, String userIp) {
        this.phoneNumber = phoneNumber;
        this.userIp = "-1"; //
    }

    @Override
    public SmsRuCallHandler getHandler() {
        return new SmsRuCallHandler();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUserIp() {
        return userIp;
    }
}