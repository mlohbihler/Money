package com.serotonin.money.vo.tx;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.serotonin.money.vo.Account;
import com.serotonin.money.vo.Asset;

public class TransferIn extends Transaction {
    public TransferIn() {
        // no op
    }

    public TransferIn(final int id, final int accountId, final Date transactionDate, final double amount)
            throws TransactionException {
        this(id, accountId, transactionDate, new BigDecimal(amount));
    }

    public TransferIn(final int id, final int accountId, final Date transactionDate, final BigDecimal amount)
            throws TransactionException {
        if (!isGTZero(amount))
            throw new TransactionException("Bad amount");

        data(id, accountId, transactionDate, null, null, null, amount, null, null, null);
    }

    public TransferIn(final int id, final int accountId, final Date transactionDate, final String symbol,
            final double shares, final double price, final double bookValue) throws TransactionException {
        this(id, accountId, transactionDate, symbol, new BigDecimal(shares), new BigDecimal(price),
                new BigDecimal(bookValue));
    }

    public TransferIn(final int id, final int accountId, final Date transactionDate, final String symbol,
            final BigDecimal shares, final BigDecimal price, final BigDecimal bookValue) throws TransactionException {
        if (StringUtils.isEmpty(symbol))
            throw new TransactionException("Bad symbol");
        if (!isGTZero(shares))
            throw new TransactionException("Bad shares");
        if (!isGTZero(price))
            throw new TransactionException("Bad price");
        if (!isGTZero(bookValue))
            throw new TransactionException("Bad book value");

        data(id, accountId, transactionDate, symbol, null, shares, price, null, null, bookValue);
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.TRANSFER_IN;
    }

    @Override
    public void apply(final Account account) {
        if (StringUtils.isBlank(getSymbol())) {
            // Cash
            account.addCashBalance(getBookValue());
            account.addInvestment(getBookValue(), getTransactionDate());
        } else {
            // In kind
            final Asset asset = account.getAsset(getSymbol(), true);
            asset.setLastTransactionDate(getTransactionDate());
            asset.addQuantity(getShares());
            asset.addBookValue(getBookValue());
            final BigDecimal market = getShares().multiply(getPrice());
            asset.subtractCashReturn(market);
            asset.addInvestment(market, getTransactionDate());

            account.addInvestment(market, getTransactionDate());
            //
            //            if ("AIM1551".equals(getSymbol()))
            //                System.out.println("Transfer in " + getSymbol() + ": date=" + getTransactionDate() + ", amount="
            //                        + getShares() + ", q=" + asset.getQuantity() + ", bv=" + asset.getBookValue());
        }
    }

    @Override
    public BigDecimal getCashAmount() {
        if (StringUtils.isBlank(getSymbol()))
            // Cash
            return getBookValue();
        return null;
    }
}
