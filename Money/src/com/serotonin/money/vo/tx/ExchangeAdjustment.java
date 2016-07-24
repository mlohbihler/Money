package com.serotonin.money.vo.tx;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.serotonin.money.vo.Account;
import com.serotonin.money.vo.Asset;

public class ExchangeAdjustment extends Transaction {
    ExchangeAdjustment() {
        // no op
    }

    public ExchangeAdjustment(int id, int accountId, Date date, String symbol, int shares, double price)
            throws TransactionException {
        this(id, accountId, date, symbol, new BigDecimal(shares), new BigDecimal(price));
    }

    public ExchangeAdjustment(int id, int accountId, Date date, String symbol, BigDecimal shares, BigDecimal price)
            throws TransactionException {
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
        return TransactionType.EXCHADJ;
    }

    @Override
    public void apply(Account account) {
        Asset asset = account.getAsset(getSymbol(), true);
        asset.setLastTransactionDate(getTransactionDate());
        asset.addQuantity(getShares());

        BigDecimal cost = getShares().multiply(getPrice());
        if (getFee() != null)
            cost = cost.add(getFee());
        if (getForeignExchange() != null)
            cost = cost.add(getForeignExchange());

        asset.addBookValue(cost);
        asset.addInvestment(cost, getTransactionDate());
    }

    @Override
    public BigDecimal getCashAmount() {
        return null;
    }
}
