package ru.dezhik.sms.sender.api.smsru;

import java.util.HashMap;
import java.util.Map;

/**
 * Sms.ru API response statuses.
 *
 * @author ilya.dezhin
 */
public enum SMSRuResponseStatus {
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
    ;
    private final static Map<Integer, SMSRuResponseStatus> statusMap =
            new HashMap<Integer, SMSRuResponseStatus>(SMSRuResponseStatus.values().length, 1f);

    static {
        for (SMSRuResponseStatus status : values()) {
            statusMap.put(status.code, status);
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