package com.serotonin.money.vo;

import java.math.BigDecimal;
import java.util.Date;

public class AssetInvestment {
    private BigDecimal amount;
    private final Date date;

    public AssetInvestment(BigDecimal amount, Date date) {
        this.amount = amount;
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "AssetInvestment [amount=" + amount + ", date=" + date + "]";
    }
}
