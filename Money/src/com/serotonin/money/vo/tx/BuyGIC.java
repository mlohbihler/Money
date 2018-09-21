package com.serotonin.money.vo.tx;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.serotonin.money.util.RateOfReturn;
import com.serotonin.money.vo.Account;
import com.serotonin.money.vo.Asset;

public class BuyGIC extends Transaction {
    private BigDecimal market;

    public BuyGIC() {
        // no op
    }

    public BuyGIC(final int id, final int accountId, final Date date, final String symbol, final String description,
            final double amount, final double rate, final double term) throws TransactionException {
        this(id, accountId, date, symbol, description, new BigDecimal(amount), new BigDecimal(rate),
                new BigDecimal(term));
    }

    public BuyGIC(final int id, final int accountId, final Date date, final String symbol, final String description,
            final BigDecimal amount, final BigDecimal rate, final BigDecimal term) throws TransactionException {
        if (StringUtils.isEmpty(symbol))
            throw new TransactionException("Bad symbol");
        if (StringUtils.isEmpty(description))
            throw new TransactionException("Bad description");
        if (!isGTZero(amount))
            throw new TransactionException("Bad amount");
        if (!isGTZero(rate))
            throw new TransactionException("Bad rate");
        if (!isGTZero(term))
            throw new TransactionException("Bad term");

        data(id, accountId, date, symbol, description, null, amount, rate, term, null);
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.BUYGIC;
    }

    @Override
    public void apply(final Account account) {
        account.subtractCashBalance(getPrice());

        final Asset asset = new Asset(getSymbol(), this);
        account.getAssets().add(asset);
        asset.setLastTransactionDate(getTransactionDate());
        asset.addQuantity(BigDecimal.ONE);
        asset.addBookValue(getPrice());
        asset.subtractCashReturn(getPrice());
        asset.addInvestment(getPrice(), getTransactionDate());
    }

    @Override
    public BigDecimal getCashAmount() {
        return getPrice();
    }

    public BigDecimal getMarketValue(final Date to) {
        final double years = RateOfReturn.differenceInYears(getTransactionDate(), to);
        final BigDecimal rate = getForeignExchange().divide(new BigDecimal(100)).add(BigDecimal.ONE);
        final double accruedRate = Math.pow(rate.doubleValue(), years);
        market = getPrice().multiply(new BigDecimal(accruedRate));
        return market;
    }

    public BigDecimal getMarketValue() {
        return market;
    }
}
