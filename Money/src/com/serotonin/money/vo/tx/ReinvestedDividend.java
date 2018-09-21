package com.serotonin.money.vo.tx;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.serotonin.money.vo.Account;
import com.serotonin.money.vo.Asset;

public class ReinvestedDividend extends Transaction {
    public ReinvestedDividend() {
        // no op
    }

    public ReinvestedDividend(final int id, final int accountId, final Date date, final String symbol,
            final double shares, final double price) throws TransactionException {
        this(id, accountId, date, symbol, new BigDecimal(shares), new BigDecimal(price));
    }

    public ReinvestedDividend(final int id, final int accountId, final Date date, final String symbol,
            final BigDecimal shares, final BigDecimal price) throws TransactionException {
        if (StringUtils.isEmpty(symbol))
            throw new TransactionException("Bad symbol");
        if (!isGTZero(shares))
            throw new TransactionException("Bad shares");
        if (!isGTZero(price))
            throw new TransactionException("Bad price");

        data(id, accountId, date, symbol, null, shares, price, null, null, null);
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.REINVDIV;
    }

    @Override
    public void apply(final Account account) throws TransactionException {
        final Asset asset = account.getAsset(getSymbol(), false);
        if (asset == null)
            throw new TransactionException("Reinvested dividend in asset that does not exist: '" + getSymbol() + "'");

        asset.setLastTransactionDate(getTransactionDate());
        asset.addQuantity(getShares());
        asset.addBookValue(getShares().multiply(getPrice()));
    }

    @Override
    public BigDecimal getCashAmount() {
        return null;
    }
}
