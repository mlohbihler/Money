package com.serotonin.money.vo.tx;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.serotonin.money.vo.Account;
import com.serotonin.money.vo.Asset;

public class Tax extends Transaction {
    public Tax() {
        // no op
    }

    public Tax(final int id, final int accountId, final Date date, final String symbol, final double tax)
            throws TransactionException {
        this(id, accountId, date, symbol, new BigDecimal(tax));
    }

    public Tax(final int id, final int accountId, final Date date, final String symbol, final BigDecimal tax)
            throws TransactionException {
        if (StringUtils.isEmpty(symbol))
            throw new TransactionException("Bad symbol");
        if (!isGTZero(tax))
            throw new TransactionException("Bad tax");

        data(id, accountId, date, symbol, null, null, null, null, tax, null);
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.TAX;
    }

    @Override
    public void apply(final Account account) throws TransactionException {
        final Asset asset = account.getAsset(getSymbol(), false);
        if (asset == null)
            throw new TransactionException("Tax in asset that does not exist: '" + getSymbol() + "'");

        account.subtractCashBalance(getFee());
        asset.setLastTransactionDate(getTransactionDate());
        asset.subtractCashReturn(getFee());
    }

    @Override
    public BigDecimal getCashAmount() {
        return getFee();
    }
}
