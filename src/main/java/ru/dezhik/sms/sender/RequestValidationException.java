package ru.dezhik.sms.sender;

/**
 * @author ilya.dezhin
 */
public class RequestValidationException extends RuntimeException {

    public RequestValidationException(String message) {
        super(message);
    }

    public RequestValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
