package com.serotonin.money.vo.tx;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.serotonin.money.vo.Account;
import com.serotonin.money.vo.Asset;

public class StockDividend extends Transaction {
    StockDividend() {
        // no op
    }

    public StockDividend(int id, int accountId, Date date, String symbol, double amount) throws TransactionException {
        this(id, accountId, date, symbol, new BigDecimal(amount));
    }

    public StockDividend(int id, int accountId, Date date, String symbol, BigDecimal amount)
            throws TransactionException {
        if (StringUtils.isEmpty(symbol))
            throw new TransactionException("Bad symbol");
        if (!isGTZero(amount))
            throw new TransactionException("Bad amount");

        data(id, accountId, date, symbol, null, null, amount, null, null, null);
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.STOCKDIV;
    }

    @Override
    public void apply(Account account) throws TransactionException {
        Asset asset = account.getAsset(getSymbol(), false);
        if (asset == null)
            throw new TransactionException("Stock dividend in asset that does not exist: '" + getSymbol() + "'");

        account.addCashBalance(getPrice());
        asset.setLastTransactionDate(getTransactionDate());
        asset.addCashReturn(getPrice());
    }

    @Override
    public BigDecimal getCashAmount() {
        return getPrice();
    }
}
