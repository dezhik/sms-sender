package ru.dezhik.sms.sender.api.smsru.auth;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import ru.dezhik.sms.sender.SenderServiceConfiguration;

/**
 * @author ilya.dezhin
 */
public class PasswordAuthProvider implements AuthProvider {
    private final SenderServiceConfiguration config;

    public PasswordAuthProvider(SenderServiceConfiguration config) {
        this.config = config;
        if (config.getLogin() == null || config.getLogin().isEmpty()) {
            throw new IllegalStateException("Login in required for login/password authentication.");
        }
        if (config.getPassword() == null || config.getPassword().isEmpty()) {
            throw new IllegalStateException("Password in required for login/password authentication.");
        }
    }

    @Override
    public List<NameValuePair> provideAuthParams() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("login", config.getLogin()));
        params.add(new BasicNameValuePair("password", config.getPassword()));
        return params;
    }
}
