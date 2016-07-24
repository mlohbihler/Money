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
    private final List<Asset> assets = new ArrayList<Asset>();
    private final List<AssetInvestment> investments = new ArrayList<AssetInvestment>();
    private BigDecimal interest = new BigDecimal(0);

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public Asset getAsset(String symbol, boolean add) {
        for (Asset asset : assets) {
            if (asset.getSymbol().equals(symbol))
                return asset;
        }

        if (!add)
            return null;

        Asset asset = new Asset(symbol);

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

    public void setCashBalance(BigDecimal cashBalance) {
        this.cashBalance = cashBalance.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void addCashBalance(BigDecimal amount) {
        cashBalance = cashBalance.add(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
        //System.out.println("Added " + amount + ", new balance: " + cashBalance);
    }

    public void subtractCashBalance(BigDecimal amount) {
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

    public void addInvestment(BigDecimal amount, Date date) {
        investments.add(new AssetInvestment(amount, date));
    }

    //
    // Cash return
    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void addInterest(BigDecimal amount) {
        interest = interest.add(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void subtractInterest(BigDecimal amount) {
        interest = interest.subtract(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public double getRateOfReturn() {
        return getRateOfReturn(new Date());
    }

    public double getRateOfReturn(Date asOfDate) {
        BigDecimal totalReturn = new BigDecimal(0);
        for (Asset asset : assets)
            totalReturn = totalReturn.add(asset.getReturn());
        //totalReturn = totalReturn.add(cashBalance);
        totalReturn = totalReturn.add(interest);

        List<Investment> list = new ArrayList<Investment>();
        for (AssetInvestment i : investments)
            list.add(new Investment(i.getAmount(), RateOfReturn.differenceInYears(i.getDate(), asOfDate)));
        return RateOfReturn.calculate(totalReturn, list);
    }
}
