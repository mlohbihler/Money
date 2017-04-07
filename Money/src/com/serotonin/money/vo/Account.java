package com.serotonin.money.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.serotonin.money.util.Investment;
import com.serotonin.money.util.RateOfReturn;

public class Account {
    private int id;
    private String name;
    private String notes;
    private String colour;

    private BigDecimal cashBalance = new BigDecimal(0);
    private final List<Asset> assets = new ArrayList<>();
    private final List<AssetInvestment> investments = new ArrayList<>();
    private BigDecimal interest = new BigDecimal(0);

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(final String notes) {
        this.notes = notes;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(final String colour) {
        this.colour = colour;
    }

    /**
     * Find the asset with the given symbol
     *
     * @param symbol
     * @param add
     *            Whether to add the symbol if it is not found. Only use true if the symbol is not a GIC.
     * @return
     */
    public Asset getAsset(final String symbol, final boolean add) {
        for (final Asset asset : assets) {
            if (asset.getSymbol().equals(symbol))
                return asset;
        }

        if (!add)
            return null;

        final Asset asset = new Asset(symbol, null);

        int index = Collections.binarySearch(assets, asset);
        if (index < 0)
            index = -index - 1;
        assets.add(index, asset);
        return asset;
    }

    public void resortAssets() {
        Collections.sort(assets);
    }

    public BigDecimal getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(final BigDecimal cashBalance) {
        this.cashBalance = cashBalance.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void addCashBalance(final BigDecimal amount) {
        cashBalance = cashBalance.add(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
        //System.out.println("Added " + amount + ", new balance: " + cashBalance);
    }

    public void subtractCashBalance(final BigDecimal amount) {
        cashBalance = cashBalance.subtract(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
        //System.out.println("Subtracted " + amount + ", new balance: " + cashBalance);
    }

    public List<Asset> getAssets() {
        return assets;
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
    // Cash return
    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(final BigDecimal interest) {
        this.interest = interest.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void addInterest(final BigDecimal amount) {
        interest = interest.add(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void subtractInterest(final BigDecimal amount) {
        interest = interest.subtract(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public double getRateOfReturn() {
        return getRateOfReturn(new Date());
    }

    public double getRateOfReturn(final Date asOfDate) {
        BigDecimal totalReturn = new BigDecimal(0);
        for (final Asset asset : assets)
            totalReturn = totalReturn.add(asset.getReturn());
        //totalReturn = totalReturn.add(cashBalance);
        totalReturn = totalReturn.add(interest);

        final List<Investment> list = new ArrayList<>();
        for (final AssetInvestment i : investments)
            list.add(new Investment(i.getAmount(), RateOfReturn.differenceInYears(i.getDate(), asOfDate)));
        return RateOfReturn.calculate(totalReturn, list);
    }
}
