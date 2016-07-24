package com.serotonin.money.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.serotonin.money.util.Investment;
import com.serotonin.money.util.RateOfReturn;
import com.serotonin.money.util.Utils;

public class Asset implements Comparable<Asset> {
    private String symbol;
    private BigDecimal quantity = new BigDecimal(0);
    private BigDecimal bookValue = new BigDecimal(0);
    private BigDecimal cashReturn = new BigDecimal(0);
    private final List<AssetInvestment> investments = new ArrayList<AssetInvestment>();
    private Date lastTransactionDate;

    // From the Assets table.
    private AssetInfo assetInfo;

    private boolean pastCutoff;

    public Asset(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Date getLastTransactionDate() {
        return lastTransactionDate;
    }

    public void setLastTransactionDate(Date lastTransactionDate) {
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

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public void addQuantity(BigDecimal amount) {
        quantity = quantity.add(amount);
    }

    public void subtractQuantity(BigDecimal amount) {
        quantity = quantity.subtract(amount);
    }

    //
    // Book value
    public BigDecimal getBookValue() {
        return bookValue;
    }

    public void setBookValue(BigDecimal bookValue) {
        this.bookValue = bookValue.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void addBookValue(BigDecimal amount) {
        bookValue = bookValue.add(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void subtractBookValue(BigDecimal amount) {
        bookValue = bookValue.subtract(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    //
    // Cash return
    public BigDecimal getCashReturn() {
        return cashReturn;
    }

    public void setCashReturn(BigDecimal cashReturn) {
        this.cashReturn = cashReturn.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void addCashReturn(BigDecimal amount) {
        cashReturn = cashReturn.add(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void subtractCashReturn(BigDecimal amount) {
        cashReturn = cashReturn.subtract(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    //
    // Investments
    public List<AssetInvestment> getInvestments() {
        return investments;
    }

    public void addInvestment(BigDecimal amount, Date date) {
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
        double averageCost = bookValue.doubleValue() / quantity.doubleValue();
        double gl = getMarketValue().doubleValue() / averageCost;
        return (gl - 1);
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

    public double getRateOfReturn(Date asOfDate) {
        List<Investment> list = new ArrayList<Investment>();
        for (AssetInvestment i : investments)
            list.add(new Investment(i.getAmount(), RateOfReturn.differenceInYears(i.getDate(), asOfDate)));
        return RateOfReturn.calculate(getReturn(), list);
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public boolean isPastCutoff() {
        return pastCutoff;
    }

    public void setPastCutoff(boolean pastCutoff) {
        this.pastCutoff = pastCutoff;
    }

    @Override
    public String toString() {
        return "Asset [symbol=" + symbol + ", quantity=" + quantity + ", bookValue=" + bookValue + ", cashReturn="
                + cashReturn + "]";
    }

    @Override
    public int compareTo(Asset that) {
        return symbol.compareTo(that.symbol);
    }
}
