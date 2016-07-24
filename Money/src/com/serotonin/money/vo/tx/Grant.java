package com.serotonin.money.vo.tx;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.serotonin.money.vo.Account;

public class Grant extends Transaction {
    Grant() {
        // no op
    }

    public Grant(int id, int accountId, Date date, String beneficiary, double amount) throws TransactionException {
        this(id, accountId, date, beneficiary, new BigDecimal(amount));
    }

    public Grant(int id, int accountId, Date date, String beneficiary, BigDecimal amount) throws TransactionException {
        if (StringUtils.isEmpty(beneficiary))
            throw new TransactionException("Bad beneficiary");
        if (!isGTZero(amount))
            throw new TransactionException("Bad amount");

        data(id, accountId, date, null, beneficiary, null, amount, null, null, null);
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.GRANT;
    }

    @Override
    public void apply(Account account) {
        account.addCashBalance(getPrice());
        account.addInvestment(getPrice(), getTransactionDate());
    }

    @Override
    public BigDecimal getCashAmount() {
        return getPrice();
    }
}
