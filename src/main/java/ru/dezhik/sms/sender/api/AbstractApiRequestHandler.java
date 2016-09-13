package ru.dezhik.sms.sender.api;

import ru.dezhik.sms.sender.SenderServiceConfiguration;

/**
 * @author ilya.dezhin
 */
public abstract class AbstractApiRequestHandler<Req extends ApiRequest, Resp extends ApiResponse>
        implements ApiRequestHandler<Req, Resp> {
    protected SenderServiceConfiguration config;

    @Override
    public void setConfig(SenderServiceConfiguration config) {
        this.config = config;
    }
}
