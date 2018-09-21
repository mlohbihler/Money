package com.serotonin.money.vo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.serotonin.money.util.Investment;
import com.serotonin.money.util.RateOfReturn;
import com.serotonin.money.util.Utils;
import com.serotonin.money.vo.tx.BuyGIC;

public class Asset implements Comparable<Asset> {
    private final String symbol;
    private final BuyGIC gicPurchase;
    private BigDecimal quantity = new BigDecimal(0);
    private BigDecimal bookValue = new BigDecimal(0);
    private BigDecimal cashReturn = new BigDecimal(0);
    private final List<AssetInvestment> investments = new ArrayList<>();
    private Date lastTransactionDate;

    // From the Assets table.
    private AssetInfo assetInfo;

    private boolean pastCutoff;

    public Asset(final String symbol, final BuyGIC gicPurchase) {
        this.symbol = symbol;
        this.gicPurchase = gicPurchase;
    }

    public String getSymbol() {
        return symbol;
    }

    public BuyGIC getGicPurchase() {
        return gicPurchase;
    }

    public Date getLastTransactionDate() {
        return lastTransactionDate;
    }

    public void setLastTransactionDate(final Date lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    public BigDecimal getMarketValue() {
        if (assetInfo == null)
            return null;
        return new BigDecimal(assetInfo.getMarketPrice());
    }

    public String getExchange() {
        return Utils.getExchange(symbol);
    }

    public String getSymbolSansExchange() {
        return Utils.getSymbolSansExchange(symbol);
    }

    //
    // Quantity
    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
    }

    public void addQuantity(final BigDecimal amount) {
        quantity = quantity.add(amount);
    }

    public void subtractQuantity(final BigDecimal amount) {
        quantity = quantity.subtract(amount);
    }

    //
    // Book value
    public BigDecimal getBookValue() {
        return bookValue;
    }

    public void setBookValue(final BigDecimal bookValue) {
        this.bookValue = bookValue.setScale(2, RoundingMode.HALF_UP);
    }

    public void addBookValue(final BigDecimal amount) {
        bookValue = bookValue.add(amount).setScale(2, RoundingMode.HALF_UP);
    }

    public void subtractBookValue(final BigDecimal amount) {
        bookValue = bookValue.subtract(amount).setScale(2, RoundingMode.HALF_UP);
    }

    //
    // Cash return
    public BigDecimal getCashReturn() {
        return cashReturn;
    }

    public void setCashReturn(final BigDecimal cashReturn) {
        this.cashReturn = cashReturn.setScale(2, RoundingMode.HALF_UP);
    }

    public void addCashReturn(final BigDecimal amount) {
        cashReturn = cashReturn.add(amount).setScale(2, RoundingMode.HALF_UP);
    }

    public void subtractCashReturn(final BigDecimal amount) {
        cashReturn = cashReturn.subtract(amount).setScale(2, RoundingMode.HALF_UP);
    }

    //
    // Investments
    public List<AssetInvestment> getInvestments() {
        return investments;
    }

    public void addInvestment(final BigDecimal amount, final Date date) {
        investments.add(new AssetInvestment(amount, date));
    }

    //
    // Misc
    /**
     * Cash return plus book value.
     */
    public BigDecimal getReturn() {
        return cashReturn.add(bookValue);
    }

    public BigDecimal getAverageCost() {
        if (quantity.doubleValue() == 0)
            return new BigDecimal(0);
        return new BigDecimal(bookValue.doubleValue() / quantity.doubleValue());
    }

    public double getCapitalGainLoss() {
        final double averageCost = bookValue.doubleValue() / quantity.doubleValue();
        final double gl = getMarketValue().doubleValue() / averageCost;
        return gl - 1;
    }

    /**
     * Cash return plus market value.
     */
    public BigDecimal getDollarGainLoss() {
        return cashReturn.add(getMarketValue().multiply(quantity));
    }

    public double getRateOfReturn() {
        return getRateOfReturn(new Date());
    }

    public double getRateOfReturn(final Date asOfDate) {
        final List<Investment> list = new ArrayList<>();
        for (final AssetInvestment i : investments)
            list.add(new Investment(i.getAmount(), RateOfReturn.differenceInYears(i.getDate(), asOfDate)));
        return RateOfReturn.calculate(getReturn(), list);
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(final AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public boolean isPastCutoff() {
        return pastCutoff;
    }

    public void setPastCutoff(final boolean pastCutoff) {
        this.pastCutoff = pastCutoff;
    }

    @Override
    public String toString() {
        return "Asset [symbol=" + symbol + ", quantity=" + quantity + ", bookValue=" + bookValue + ", cashReturn="
                + cashReturn + "]";
    }

    @Override
    public int compareTo(final Asset that) {
        return symbol.compareTo(that.symbol);
    }
}
