package com.serotonin.money.util;

import java.math.BigDecimal;

public class Investment {
    public final BigDecimal amount;
    public final double years;

    public Investment(double amount, double years) {
        this(new BigDecimal(amount), years);
    }

    public Investment(BigDecimal amount, double years) {
        this.amount = amount;
        this.years = years;
    }
}
