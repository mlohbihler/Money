package com.serotonin.money.vo.tx;

import java.math.BigDecimal;
import java.util.Date;

import com.serotonin.money.vo.Account;

public class CashAdjustment extends Transaction {
    public CashAdjustment() {
        // no op
    }

    public CashAdjustment(final int id, final int accountId, final Date date, final double amount)
            throws TransactionException {
        this(id, accountId, date, new BigDecimal(amount));
    }

    public CashAdjustment(final int id, final int accountId, final Date date, final BigDecimal amount)
            throws TransactionException {
        if (!isGTZero(amount))
            throw new TransactionException("Bad amount");

        data(id, accountId, date, null, null, null, amount, null, null, null);
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.CASHADJ;
    }

    @Override
    public void apply(final Account account) {
        account.addCashBalance(getPrice());
        account.addInvestment(getPrice(), getTransactionDate());
    }

    @Override
    public BigDecimal getCashAmount() {
        return getPrice();
    }
}
