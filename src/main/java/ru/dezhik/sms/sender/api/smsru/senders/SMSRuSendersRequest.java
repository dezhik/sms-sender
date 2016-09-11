package ru.dezhik.sms.sender.api.smsru.senders;

import ru.dezhik.sms.sender.api.ApiRequest;

/**
 * @author ilya.dezhin
 */
public class SMSRuSendersRequest extends ApiRequest<SMSRuSendersHandler, SMSRuSendersResponse> {
    @Override
    public SMSRuSendersHandler getHandler() {
        return new SMSRuSendersHandler();
    }
}
