package com.serotonin.money.vo.tx;

import java.math.BigDecimal;
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
    Merger() {
        // no op
    }

    public Merger(int id, int accountId, Date date, String fromSymbol, String toSymbol, double fromShares,
            double toShares) throws TransactionException {
        this(id, accountId, date, fromSymbol, toSymbol, new BigDecimal(fromShares), new BigDecimal(toShares));
    }

    public Merger(int id, int accountId, Date date, String fromSymbol, String toSymbol, BigDecimal fromShares,
            BigDecimal toShares) throws TransactionException {
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
    public void apply(Account account) throws TransactionException {
        String from = getSymbol();
        String to = getSymbol2();

        // Take the shares from the from account and determine the book value.
        Asset fromAsset = account.getAsset(from, false);
        if (fromAsset == null)
            throw new TransactionException("Merger from asset that does not exist: '" + from + "'");

        BigDecimal fromShares = getShares();
        if (fromShares == null)
            fromShares = fromAsset.getQuantity();
        if (fromAsset.getQuantity().doubleValue() < fromShares.doubleValue())
            throw new TransactionException("Merger in asset with insufficient shares: '" + from + "'");

        BigDecimal ratio = new BigDecimal(fromShares.doubleValue() / fromAsset.getQuantity().doubleValue());
        BigDecimal bookValue = fromAsset.getBookValue().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP);

        fromAsset.setLastTransactionDate(getTransactionDate());
        fromAsset.subtractQuantity(fromShares);
        fromAsset.subtractBookValue(bookValue);

        // Add the shares to the to account with the calculated book value.
        BigDecimal toShares = getPrice();
        if (toShares == null)
            toShares = fromShares;
        Asset toAsset = account.getAsset(to, true);
        toAsset.setLastTransactionDate(getTransactionDate());
        toAsset.addQuantity(toShares);
        toAsset.addBookValue(bookValue);

        // Investment calculations
        BigDecimal cashReturn = fromAsset.getCashReturn().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP);
        toAsset.addCashReturn(cashReturn);
        fromAsset.subtractCashReturn(cashReturn);

        for (AssetInvestment investment : fromAsset.getInvestments()) {
            BigDecimal proRata = investment.getAmount().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP);
            toAsset.addInvestment(proRata, investment.getDate());
            investment.setAmount(investment.getAmount().subtract(proRata));
        }
    }

    @Override
    public BigDecimal getCashAmount() {
        return null;
    }
}
