package com.serotonin.money.vo.tx;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.serotonin.money.vo.Account;
import com.serotonin.money.vo.Asset;

public class FeeRebate extends Transaction {
    FeeRebate() {
        // no op
    }

    public FeeRebate(int id, int accountId, Date date, String symbol, double amount) throws TransactionException {
        this(id, accountId, date, symbol, new BigDecimal(amount));
    }

    public FeeRebate(int id, int accountId, Date date, String symbol, BigDecimal amount) throws TransactionException {
        if (StringUtils.isEmpty(symbol))
            throw new TransactionException("Bad symbol");
        if (!isGTZero(amount))
            throw new TransactionException("Bad amount");

        data(id, accountId, date, symbol, null, null, null, null, amount, null);
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.FEE_REBATE;
    }

    @Override
    public void apply(Account account) throws TransactionException {
        Asset asset = account.getAsset(getSymbol(), false);
        if (asset == null)
            throw new TransactionException("Fee rebate in asset that does not exist: '" + getSymbol() + "'");

        account.addCashBalance(getFee());
        asset.setLastTransactionDate(getTransactionDate());
        asset.subtractBookValue(getFee());
        asset.addCashReturn(getFee());
        asset.addInvestment(getFee().negate(), getTransactionDate());
    }

    @Override
    public BigDecimal getCashAmount() {
        return getFee();
    }
}
