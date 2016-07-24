package com.serotonin.money.vo.tx;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.serotonin.money.vo.Account;
import com.serotonin.money.vo.Asset;

public class SplitCash extends Transaction {
    SplitCash() {
        // no op
    }

    public SplitCash(int id, int accountId, Date date, String symbol, double amount) throws TransactionException {
        this(id, accountId, date, symbol, new BigDecimal(amount));
    }

    public SplitCash(int id, int accountId, Date date, String symbol, BigDecimal amount) throws TransactionException {
        if (StringUtils.isEmpty(symbol))
            throw new TransactionException("Bad symbol");
        if (!isGTZero(amount))
            throw new TransactionException("Bad amount");

        data(id, accountId, date, symbol, null, null, amount, null, null, null);
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.SPLIT_CASH;
    }

    @Override
    public void apply(Account account) throws TransactionException {
        Asset asset = account.getAsset(getSymbol(), false);
        if (asset == null)
            throw new TransactionException("Split cash in asset that does not exist: '" + getSymbol() + "'");

        account.addCashBalance(getPrice());
        asset.setBookValue(asset.getBookValue().subtract(getPrice()));
    }

    @Override
    public BigDecimal getCashAmount() {
        return getPrice();
    }
}
