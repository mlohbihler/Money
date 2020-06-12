package com.serotonin.money.vo.tx;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.serotonin.money.vo.Account;
import com.serotonin.money.vo.Asset;

public class GICRedeemed extends Transaction {
    public GICRedeemed() {
        // no op
    }

    public GICRedeemed(final int id, final int accountId, final Date transactionDate, final String symbol,
            final double amount) throws TransactionException {
        this(id, accountId, transactionDate, symbol, new BigDecimal(amount));
    }

    public GICRedeemed(final int id, final int accountId, final Date transactionDate, final String symbol,
            final BigDecimal amount) throws TransactionException {
        if (StringUtils.isEmpty(symbol))
            throw new TransactionException("Bad symbol");
        if (!isGTZero(amount))
            throw new TransactionException("Bad amount");
        data(id, accountId, transactionDate, symbol, null, null, amount, null, null, null);
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.GIC_REDEEMED;
    }

    @Override
    public void apply(final Account account) throws TransactionException {
        final Asset asset = account.getAsset(getSymbol(), false);
        if (asset == null) {
            throw new TransactionException("GIC redeemed in asset that does not exist: '" + getSymbol() + "'");
        }
        if (asset.getQuantity().doubleValue() <= 0) {
            throw new TransactionException("GIC redeemed in asset with zero quantity: '" + getSymbol() + "'");
        }
        if (asset.getGicPurchase() == null) {
            throw new TransactionException("GIC redeemed in asset without a GIC purchase: '" + getSymbol() + "'");
        }
        if (!asset.getGicPurchase().getPrice().equals(getPrice())) {
            throw new TransactionException(
                    "GIC redeemed amount does not match GIC purchase amount: '" + getSymbol() + "'");
        }

        account.addCashBalance(getPrice());

        asset.setLastTransactionDate(getTransactionDate());
        asset.subtractQuantity(BigDecimal.ONE);
        asset.subtractBookValue(getPrice());
        asset.addCashReturn(getPrice());
        asset.addInvestment(getPrice().negate(), getTransactionDate());
    }

    @Override
    public BigDecimal getCashAmount() {
        return getPrice();
    }
}
