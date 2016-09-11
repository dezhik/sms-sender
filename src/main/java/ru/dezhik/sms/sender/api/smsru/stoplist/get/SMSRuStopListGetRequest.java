package ru.dezhik.sms.sender.api.smsru.stoplist.get;

import ru.dezhik.sms.sender.api.ApiRequest;

/**
 * @author ilya.dezhin
 */
public class SMSRuStopListGetRequest extends ApiRequest<SMSRuStopListGetHandler, SMSRuStopListGetResponse> {
    @Override
    public SMSRuStopListGetHandler getHandler() {
        return new SMSRuStopListGetHandler();
    }
}
