package com.serotonin.money.vo.tx;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.serotonin.money.vo.Account;
import com.serotonin.money.vo.Asset;

public class CashDividend extends Transaction {
    public CashDividend() {
        // no op
    }

    public CashDividend(final int id, final int accountId, final Date date, final String symbol, final double amount)
            throws TransactionException {
        this(id, accountId, date, symbol, new BigDecimal(amount));
    }

    public CashDividend(final int id, final int accountId, final Date date, final String symbol,
            final BigDecimal amount) throws TransactionException {
        if (StringUtils.isEmpty(symbol))
            throw new TransactionException("Bad symbol");
        if (!isGTZero(amount))
            throw new TransactionException("Bad amount");

        data(id, accountId, date, symbol, null, null, amount, null, null, null);
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.CASHDIV;
    }

    @Override
    public void apply(final Account account) throws TransactionException {
        final Asset asset = account.getAsset(getSymbol(), false);
        if (asset == null)
            throw new TransactionException("Cash dividend in asset that does not exist: '" + getSymbol() + "'");

        account.addCashBalance(getPrice());
        asset.setLastTransactionDate(getTransactionDate());
        asset.addCashReturn(getPrice());
    }

    @Override
    public BigDecimal getCashAmount() {
        return getPrice();
    }
}
