package com.serotonin.money.vo.tx;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.serotonin.money.vo.Account;
import com.serotonin.money.vo.Asset;

public class Buy extends Transaction {
    public Buy() {
        // no op
    }

    public Buy(final int id, final int accountId, final Date transactionDate, final String symbol, final double shares,
            final double price, final double fx, final double fee) throws TransactionException {
        this(id, accountId, transactionDate, symbol, new BigDecimal(shares), new BigDecimal(price), new BigDecimal(fx),
                new BigDecimal(fee));
    }

    public Buy(final int id, final int accountId, final Date transactionDate, final String symbol,
            final BigDecimal shares, final BigDecimal price, final BigDecimal fx, final BigDecimal fee)
            throws TransactionException {
        if (StringUtils.isEmpty(symbol))
            throw new TransactionException("Bad symbol");
        if (!isGTZero(shares))
            throw new TransactionException("Bad shares");
        if (!isGTZero(price))
            throw new TransactionException("Bad price");

        data(id, accountId, transactionDate, symbol, null, shares, price, fx, fee, null);
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.BUY;
    }

    @Override
    public void apply(final Account account) {
        final Asset asset = account.getAsset(getSymbol(), true);
        asset.setLastTransactionDate(getTransactionDate());
        asset.addQuantity(getShares());

        final BigDecimal cost = getCashAmount();

        asset.addBookValue(cost);
        asset.subtractCashReturn(cost);
        account.subtractCashBalance(cost);
        asset.addInvestment(cost, getTransactionDate());
    }

    @Override
    public BigDecimal getCashAmount() {
        BigDecimal cost = getShares().multiply(getPrice());
        if (getFee() != null)
            cost = cost.add(getFee());
        if (getForeignExchange() != null)
            cost = cost.add(getForeignExchange());
        return cost;
    }
}
