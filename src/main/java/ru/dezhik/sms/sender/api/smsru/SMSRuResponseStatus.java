package ru.dezhik.sms.sender.api.smsru;

import java.util.HashMap;
import java.util.Map;

/**
 * Sms.ru API response statuses.
 *
 * @author ilya.dezhin
 */
public enum SMSRuResponseStatus {
    /**
     * Status not present in sms.ru api!
     * It helps preserve backwards compatibility during migration of sending API from plain to json version.
     * Is set iff request contained multiple receivers/messages and API responded with different statuses for messages.
     * */
    MIXED(-127),
    /** sms with given id was not found. */
    SMS_NOT_FOUND(-1),
    /** SMS was taken for sending. */
    IN_QUEUE(100),
    /** Sending is in progress. */
    IN_PROGRESS(101),
    /** SMS is sent, but not delivered yet. */
    SENT(102),
    /** SMS is delivered. */
    DELIVERED(103),
    /** Can't be delivered due timeout. */
    DELIVERY_ERROR_TIMEOUT(104),
    /** Can't be delivered, removed by operator. */
    DELIVERY_ERROR_DELETED(105),
    /** Can't be delivered. */
    DELIVERY_ERROR_PHONE_FAILURE(106),
    /** Can't be delivered. Unknown reason. */
    DELIVERY_ERROR_UNKNOWN(107),
    /** Can't be delivered. Rejected. */
    DELIVERY_ERROR_REJECTED(108),
    /** Wrong API ID was used for sending. */
    WRONG_API_ID(200),
    /** Insufficient funds. */
    BALANCE_ERROR(201),
    /** Wrong sender. */
    SENDER_ERROR(202),
    /** SMS contains no text. */
    TEXT_ERROR(203),
    /** Desired sender name wasn't approved by administration. */
    MODERATION_ERROR(204),
    /** SMS is too long, exceeds 8 SMS's length limit. */
    MESSAGE_TOO_LONG(205),
    /** Daily limit on sms sending was reached. */
    DAILY_LIMIT(206),
    /** Either you can't send SMS on this number or more then 100 receivers were given. */
    RECEIVER_ERROR(207),
    /** Time argument were given in from format. */
    TIME_ERROR(208),
    /** At least one of receivers were previously added in to the black list. */
    BLACK_LIST(209),
    /** GET request type is prohibited, use POST instead. */
    HTTP_FORMAT_ERROR(210),
    /** Method was not found. */
    METHOD_NOT_FOUND(211),
    /** SMS text must be ecoded in UTF-8 format. */
    ENCODING_ERROR(212),
    /** Service is temporary unavailable. Try later. */
    UNAVAILABLE(220),
    /** SMS was not registered. Daily sms limit on the same phone (60 sms) was reached. */
    DAILY_PHONE_LIMIT(230),
    /** Limit of sent sms with duplicate text on the same phone in one minute was exceeded. Resets every minute. */
    PHONE_TEXT_MINUTE_LIMIT(231),
    /** Daily limit of sent sms with duplicate text on the same phone in one minute was exceeded. */
    PHONE_TEXT_DAILY_LIMIT(232),
    /** Incorrect authentication token. Probably it is expired or your IP has been changed. */
    WRONG_TOKEN(300),
    /** Wrong password or such login was not found. */
    WRONG_PASSWORD(301),
    /** User's account is not approved, approve is done with code sent to you in SMS after the registration. */
    ACCOUNT_NOT_APPROVED(302),
    /** Passed confirmation code is invalid. */
    CONFIRMATION_CODE_INVALID(303),
    /** Too many confirmation codes were sent. Please, try later. */
    CONFIRMATION_CODE_SENDING_LIMIT(304),
    /** Too many failed attempts for confirmation code check. Please, try later. */
    CONFIRMATION_CODE_CHECK_LIMIT(305),
    /** Phone number has not confirmed yet. Still waiting for incoming phone call. */
    CALLCHECK_PHONE_NOT_CONFIRMED(400),
    /** Phone number was successfully confirmed. */
    CALLCHECK_PHONE_CONFIRMED(401),
    /** The time allotted for verification has expired or the check_id is incorrect. */
    CALLCHECK_EXPIRED_OR_INCORRECT(402),


    /** API error. Please try later. */
    SERVER_ERROR(500),
    /**
     * IP user from the TOR network, too many such messages in a short time.
     * Can be configured in account settings.
     * */
    LIMIT_TOR_USER(501),
    /**
     * The user's IP does not match his country, too many such messages in a short period of time.
     * Can be configured in account settings.
     */
    LIMIT_COUNTRY_USER(502),
    /**
     * Too many messages to this country in a short period of time.
     * Can be configured in account settings.
     */
    LIMIT_COUNTRY(503),
    /**
     * Too many authorization codes abroad in a short period of time.
     * Can be configured in account settings.
     */
    LIMIT_COUNTRY_CONFIRMATION_CODE(504),
    /**
     * Too many messages to one IP address.
     * Can be configured in account settings.
     */
    LIMIT_MESSAGES_IP(505),
    /**
     * Too many messages with end user's IP address belonging to the hosting company (%s in the last 10 minutes).
     */
    LIMIT_MESSAGES_SUSPICIOUS_IP(506),
    /**
     * 507	IP адрес пользователя указан неверно, либо идет из частной подсети (192.*, 10.*, итд).
     * Manage IP black and whitelist at https://sms.ru/?panel=settings&subpanel=send
     */
    WRONG_USER_IP(507),
    /**
     * Reached limit of allowed calls in 5 minutes.
     * Manage limit at https://sms.ru/?panel=settings&subpanel=send
     */
    LIMIT_PHONE_CALLS(508),
    /** Invalid	callback, e.g. URL doesn't start with http:// */
    INVALID_CALLBACK(901),
    /** Callback not found. Probably was previously removed.  */
    CALLBACK_NOT_FOUND(902),
    ;
    private final static Map<Integer, SMSRuResponseStatus> statusMap =
            new HashMap<>(SMSRuResponseStatus.values().length, 1f);

    static {
        for (SMSRuResponseStatus status : values()) {
            if (statusMap.put(status.code, status) != null)
                throw new IllegalStateException("Constants with same code detected: " + status.code);
        }
    }

    public final int code;

    SMSRuResponseStatus(int code) {
        this.code = code;
    }

    public static SMSRuResponseStatus forValue(int code) {
        return statusMap.get(code);
    }
}