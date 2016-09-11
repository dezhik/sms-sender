package ru.dezhik.sms.sender.api.smsru.auth;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import ru.dezhik.sms.sender.SenderServiceConfiguration;

/**
 * @author ilya.dezhin
 */
public class TokenAuthProvider implements AuthProvider {
    private static final String GET_TOKEN_URL = "http://sms.ru/auth/get_token";
    private static final long TOKEN_LIFETIME = 10 * 60 * 1000;
    private final AtomicReference<AuthTokenHolder> tokenHolder = new AtomicReference<AuthTokenHolder>(new AuthTokenHolder());

    private static class AuthTokenHolder {
        String token;
        long expirationTime;
    }

    private final SenderServiceConfiguration config;
    private final CloseableHttpClient httpClient;
    private final MessageDigest md;

    public TokenAuthProvider(SenderServiceConfiguration config) throws NoSuchAlgorithmException {
        this.config = config;
        this.httpClient = config.getHttpClient();
        this.md = MessageDigest.getInstance("SHA-512");
        if (config.getLogin() == null || config.getLogin().isEmpty()) {
            throw new IllegalStateException("Login in required for token authentication.");
        }
    }

    private String getToken() throws IOException {
        final AuthTokenHolder holder = tokenHolder.get();
        if (holder.token == null || holder.expirationTime >= System.currentTimeMillis()) {
            long time = System.currentTimeMillis();
            final CloseableHttpResponse response = httpClient.execute(new HttpPost(GET_TOKEN_URL));
            final String responseStr = EntityUtils.toString(response.getEntity());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK && responseStr.length() > 0) {
                holder.expirationTime = time + TOKEN_LIFETIME;
                holder.token = responseStr;
            } else {
                throw new IllegalStateException("Can't get API token.");
            }
        }
        return holder.token;
    }

    @Override
    public List<NameValuePair> provideAuthParams() throws IOException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        final String token = getToken();
        params.add(new BasicNameValuePair("login", config.getLogin()));
        params.add(new BasicNameValuePair("token", token));
        //todo + check with apiId
        params.add(new BasicNameValuePair("sha512", Hex.encodeHex(md.digest(new String(config.getPassword() + token).getBytes()))));
        return params;
    }
}
