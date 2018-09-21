package com.serotonin.money.vo.tx;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.serotonin.money.vo.Account;
import com.serotonin.money.vo.Asset;
import com.serotonin.money.vo.AssetInvestment;

/**
 * Transfers shares from one asset to another.
 *
 * transactionDate: date
 * settlementDate: not used
 * symbol: pair of symbols delimited with '/' (from/to)
 * shares: from shares
 * price: to shares
 */
public class Merger extends Transaction {
    public Merger() {
        // no op
    }

    public Merger(final int id, final int accountId, final Date date, final String fromSymbol, final String toSymbol,
            final double fromShares, final double toShares) throws TransactionException {
        this(id, accountId, date, fromSymbol, toSymbol, new BigDecimal(fromShares), new BigDecimal(toShares));
    }

    public Merger(final int id, final int accountId, final Date date, final String fromSymbol, final String toSymbol,
            final BigDecimal fromShares, final BigDecimal toShares) throws TransactionException {
        if (StringUtils.isEmpty(fromSymbol))
            throw new TransactionException("Bad 'from' symbol");
        if (StringUtils.isEmpty(toSymbol))
            throw new TransactionException("Bad 'to' symbol");
        if (!isGTZero(fromShares))
            throw new TransactionException("Bad 'from' shares");
        if (!isGTZero(toShares))
            throw new TransactionException("Bad 'to' shares");

        data(id, accountId, date, fromSymbol, toSymbol, fromShares, toShares, null, null, null);
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.MERGER;
    }

    @Override
    public void apply(final Account account) throws TransactionException {
        final String from = getSymbol();
        final String to = getSymbol2();

        // Take the shares from the from account and determine the book value.
        final Asset fromAsset = account.getAsset(from, false);
        if (fromAsset == null)
            throw new TransactionException("Merger from asset that does not exist: '" + from + "'");

        BigDecimal fromShares = getShares();
        if (fromShares == null)
            fromShares = fromAsset.getQuantity();
        if (fromAsset.getQuantity().doubleValue() < fromShares.doubleValue())
            throw new TransactionException("Merger in asset with insufficient shares: '" + from + "'");

        final BigDecimal ratio = new BigDecimal(fromShares.doubleValue() / fromAsset.getQuantity().doubleValue());
        final BigDecimal bookValue = fromAsset.getBookValue().multiply(ratio).setScale(2, RoundingMode.HALF_UP);

        fromAsset.setLastTransactionDate(getTransactionDate());
        fromAsset.subtractQuantity(fromShares);
        fromAsset.subtractBookValue(bookValue);

        // Add the shares to the to account with the calculated book value.
        BigDecimal toShares = getPrice();
        if (toShares == null)
            toShares = fromShares;
        final Asset toAsset = account.getAsset(to, true);
        toAsset.setLastTransactionDate(getTransactionDate());
        toAsset.addQuantity(toShares);
        toAsset.addBookValue(bookValue);

        // Investment calculations
        final BigDecimal cashReturn = fromAsset.getCashReturn().multiply(ratio).setScale(2, RoundingMode.HALF_UP);
        toAsset.addCashReturn(cashReturn);
        fromAsset.subtractCashReturn(cashReturn);

        for (final AssetInvestment investment : fromAsset.getInvestments()) {
            final BigDecimal proRata = investment.getAmount().multiply(ratio).setScale(2, RoundingMode.HALF_UP);
            toAsset.addInvestment(proRata, investment.getDate());
            investment.setAmount(investment.getAmount().subtract(proRata));
        }
    }

    @Override
    public BigDecimal getCashAmount() {
        return null;
    }
}
