package com.serotonin.money.vo.tx;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.serotonin.money.vo.Account;
import com.serotonin.money.vo.Asset;

public class Split extends Transaction {
    public Split() {
        // no op
    }

    public Split(final int id, final int accountId, final Date date, final String symbol, final double shares)
            throws TransactionException {
        this(id, accountId, date, symbol, new BigDecimal(shares));
    }

    public Split(final int id, final int accountId, final Date date, final String symbol, final BigDecimal shares)
            throws TransactionException {
        if (StringUtils.isEmpty(symbol))
            throw new TransactionException("Bad symbol");
        if (isEmpty(shares))
            throw new TransactionException("Bad shares");

        data(id, accountId, date, symbol, null, shares, null, null, null, null);
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.SPLIT;
    }

    @Override
    public void apply(final Account account) throws TransactionException {
        final Asset asset = account.getAsset(getSymbol(), false);
        if (asset == null)
            throw new TransactionException("Split in asset that does not exist: '" + getSymbol() + "'");
        asset.setLastTransactionDate(getTransactionDate());
        asset.addQuantity(getShares());
    }

    @Override
    public BigDecimal getCashAmount() {
        return null;
    }
}
