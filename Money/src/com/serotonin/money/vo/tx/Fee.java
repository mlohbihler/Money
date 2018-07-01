package com.serotonin.money.vo.tx;

import java.math.BigDecimal;
import java.util.Date;

import com.serotonin.money.vo.Account;

public class Fee extends Transaction {
    Fee() {
        // no op
    }

    public Fee(final int id, final int accountId, final Date date, final double amount) throws TransactionException {
        this(id, accountId, date, new BigDecimal(amount));
    }

    public Fee(final int id, final int accountId, final Date date, final BigDecimal amount)
            throws TransactionException {
        if (!isGTZero(amount))
            throw new TransactionException("Bad amount");
        data(id, accountId, date, null, null, null, null, null, amount, null);
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.FEE;
    }

    @Override
    public void apply(final Account account) throws TransactionException {
        account.subtractCashBalance(getFee());
    }

    @Override
    public BigDecimal getCashAmount() {
        return getFee();
    }
}
