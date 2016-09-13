package ru.dezhik.sms.sender.api;

import java.util.List;

import org.apache.http.NameValuePair;

import ru.dezhik.sms.sender.RequestValidationException;
import ru.dezhik.sms.sender.SenderServiceConfiguration;

/**
 * @author ilya.dezhin
 */
public interface ApiRequestHandler<Req extends ApiRequest, Resp extends ApiResponse> {
    /**
     *
     * @return
     */
    String getMethodPath();

    /**
     * @param request target of validation
     * @throws RequestValidationException if not all mandatory fields are set or set incorrectly in the request
     */
    void validate(Req request) throws IllegalArgumentException;

    /**
     * Adds method specific parameters to the list of HTTP request params.
     * @param request API request
     * @param params List of parameters needed for authentication or empty list.
     */
    void appendParams(Req request, List<NameValuePair> params);

    /**
     * @param config for internal usage
     */
    void setConfig(SenderServiceConfiguration config);

    /**
     * Parse response string received from the remote API server
     * if only the response has "200 OK" http status {@link org.apache.http.HttpStatus#SC_OK}.
     * @param responseStr should not be null
     * @return parsed response
     */
    Resp parseResponse(Req request, String responseStr);
}
