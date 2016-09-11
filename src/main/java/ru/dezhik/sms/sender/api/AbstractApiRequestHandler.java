package ru.dezhik.sms.sender.api;

import ru.dezhik.sms.sender.SenderServiceConfiguration;
import ru.dezhik.sms.sender.SimpleResponse;

/**
 * @author ilya.dezhin
 */
public abstract class AbstractApiRequestHandler<Req extends ApiRequest, Resp extends SimpleResponse>
        implements ApiRequestHandler<Req, Resp> {
    protected SenderServiceConfiguration config;

    @Override
    public void setConfig(SenderServiceConfiguration config) {
        this.config = config;
    }
}
