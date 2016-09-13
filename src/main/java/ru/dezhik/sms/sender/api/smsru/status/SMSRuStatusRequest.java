package ru.dezhik.sms.sender.api.smsru.status;

import ru.dezhik.sms.sender.api.ApiRequest;
import ru.dezhik.sms.sender.api.smsru.SMSRuApiResponse;

/**
 * @author ilya.dezhin
 */
public class SMSRuStatusRequest extends ApiRequest<SMSRuStatusHandler, SMSRuApiResponse> {
    private final String id;

    public SMSRuStatusRequest(String id) {
        this.id = id;
    }

    @Override
    public SMSRuStatusHandler getHandler() {
        return new SMSRuStatusHandler();
    }


    public String getId() {
        return id;
    }
}
