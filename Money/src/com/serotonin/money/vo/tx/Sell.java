package com.serotonin.money.vo.tx;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.serotonin.money.vo.Account;
import com.serotonin.money.vo.Asset;

public class Sell extends Transaction {
    Sell() {
        // no op
    }

    public Sell(int id, int accountId, Date transactionDate, String symbol, double shares, double price, double fx,
            double fee) throws TransactionException {
        this(id, accountId, transactionDate, symbol, new BigDecimal(shares), new BigDecimal(price), new BigDecimal(fx),
                new BigDecimal(fee));
    }

    public Sell(int id, int accountId, Date transactionDate, String symbol, BigDecimal shares, BigDecimal price,
            BigDecimal fx, BigDecimal fee) throws TransactionException {
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
        return TransactionType.SELL;
    }

    @Override
    public void apply(Account account) throws TransactionException {
        Asset asset = account.getAsset(getSymbol(), false);
        if (asset == null)
            throw new TransactionException("Sell in asset that does not exist: '" + getSymbol() + "'");
        if (asset.getQuantity().doubleValue() <= 0)
            throw new TransactionException("Sell in asset with zero quantity: '" + getSymbol() + "'");

        BigDecimal ratio = new BigDecimal(1 - getShares().doubleValue() / asset.getQuantity().doubleValue());

        asset.setLastTransactionDate(getTransactionDate());
        asset.subtractQuantity(getShares());
        asset.setBookValue(asset.getBookValue().multiply(ratio));

        BigDecimal value = getCashAmount();

        account.addCashBalance(value);
        asset.addCashReturn(value);
        asset.addInvestment(value.negate(), getTransactionDate());
    }

    @Override
    public BigDecimal getCashAmount() {
        BigDecimal value = getShares().multiply(getPrice());
        if (getForeignExchange() != null)
            value = value.add(getForeignExchange());
        if (getFee() != null)
            value = value.subtract(getFee());
        return value;
    }
}
