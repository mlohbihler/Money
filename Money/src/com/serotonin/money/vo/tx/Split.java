package com.serotonin.money.vo.tx;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.serotonin.money.vo.Account;
import com.serotonin.money.vo.Asset;

public class Split extends Transaction {
    Split() {
        // no op
    }

    public Split(int id, int accountId, Date date, String symbol, double shares) throws TransactionException {
        this(id, accountId, date, symbol, new BigDecimal(shares));
    }

    public Split(int id, int accountId, Date date, String symbol, BigDecimal shares) throws TransactionException {
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
    public void apply(Account account) throws TransactionException {
        Asset asset = account.getAsset(getSymbol(), false);
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
