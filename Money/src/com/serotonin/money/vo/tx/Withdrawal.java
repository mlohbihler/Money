package com.serotonin.money.vo.tx;

import java.math.BigDecimal;
import java.util.Date;

import com.serotonin.money.vo.Account;

public class Withdrawal extends Transaction {
    public Withdrawal() {
        // no op
    }

    public Withdrawal(final int id, final int accountId, final Date date, final double amount)
            throws TransactionException {
        this(id, accountId, date, new BigDecimal(amount));
    }

    public Withdrawal(final int id, final int accountId, final Date date, final BigDecimal amount)
            throws TransactionException {
        if (!isGTZero(amount))
            throw new TransactionException("Bad amount");

        data(id, accountId, date, null, null, null, amount, null, null, null);
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.WITHDRAWAL;
    }

    @Override
    public void apply(final Account account) {
        account.subtractCashBalance(getPrice());
        account.addInvestment(getPrice().negate(), getTransactionDate());
    }

    @Override
    public BigDecimal getCashAmount() {
        return getPrice();
    }
}
