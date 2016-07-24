package com.serotonin.money.vo.tx;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.serotonin.money.vo.Account;
import com.serotonin.money.vo.Asset;

public class ManagementFee extends Transaction {
    ManagementFee() {
        // no op
    }

    public ManagementFee(int id, int accountId, Date date, String symbol, double shares, double price)
            throws TransactionException {
        this(id, accountId, date, symbol, new BigDecimal(shares), new BigDecimal(price));
    }

    public ManagementFee(int id, int accountId, Date date, String symbol, BigDecimal shares, BigDecimal price)
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
        return TransactionType.MANAGEMENT_FEE;
    }

    @Override
    public void apply(Account account) throws TransactionException {
        Asset asset = account.getAsset(getSymbol(), false);
        if (asset == null)
            throw new TransactionException("Management fee in asset that does not exist: '" + getSymbol() + "'");

        asset.setLastTransactionDate(getTransactionDate());
        asset.subtractQuantity(getShares());
    }

    @Override
    public BigDecimal getCashAmount() {
        return null;
    }
}
