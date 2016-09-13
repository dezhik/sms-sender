package ru.dezhik.sms.sender.api.smsru.cost;

import ru.dezhik.sms.sender.api.smsru.SMSRuApiResponse;

/**
 * @author ilya.dezhin
 */
public class SMSRuCostResponse extends SMSRuApiResponse {
    private Double price;
    private Integer smsNeeded;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getSmsNeeded() {
        return smsNeeded;
    }

    public void setSmsNeeded(Integer smsNeeded) {
        this.smsNeeded = smsNeeded;
    }
}
