package ru.dezhik.sms.sender.api.smsru.call;

import ru.dezhik.sms.sender.api.ApiResponse;
import ru.dezhik.sms.sender.api.SenderStatus;

/**
 * @author ilya.dezhin
 */
public class SmsRuCallResponse extends ApiResponse<SenderStatus> {

    private String userCode;
    private String callId;
    private Double cost;
    private Double balance;

    private String statusText;
    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    @Override
    public String toString() {
        return "SmsRuCallResponse{" +
                "userCode='" + userCode + '\'' +
                ", callId='" + callId + '\'' +
                ", cost=" + cost +
                ", balance=" + balance +
                ", statusText='" + statusText + '\'' +
                ", status=" + status +
                ", plainResponse='" + plainResponse + '\'' +
                '}';
    }
}
