package com.serotonin.money.vo;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.serotonin.money.util.Utils;
import com.serotonin.money.vo.tx.TransactionType;

public class AssetInfo {
    // The symbol used in itrade
    private String symbol;
    private String name;
    private double marketPrice;
    private long marketTime;
    // The symbol used in yahoo finance
    private String marketSymbol;
    private double divAmount;
    private int divDay;
    private int divMonth;
    private int divPerYear;
    private TransactionType divXaType;
    private Country divCountry;
    private int divSymbolId;
    private String notes;

    public String getExchange() {
        return Utils.getExchange(symbol);
    }

    public String getSymbolSansExchange() {
        return Utils.getSymbolSansExchange(symbol);
    }

    public String getPrettyMarketTime() {
        return new SimpleDateFormat("MMM dd HH:mm:ss").format(new Date(marketTime));
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public long getMarketTime() {
        return marketTime;
    }

    public void setMarketTime(long marketTime) {
        this.marketTime = marketTime;
    }

    public String getMarketSymbol() {
        return marketSymbol;
    }

    public void setMarketSymbol(String marketSymbol) {
        this.marketSymbol = marketSymbol;
    }

    public double getDivAmount() {
        return divAmount;
    }

    public void setDivAmount(double divAmount) {
        this.divAmount = divAmount;
    }

    public int getDivDay() {
        return divDay;
    }

    public void setDivDay(int divDay) {
        this.divDay = divDay;
    }

    public int getDivMonth() {
        return divMonth;
    }

    public void setDivMonth(int divMonth) {
        this.divMonth = divMonth;
    }

    public int getDivPerYear() {
        return divPerYear;
    }

    public void setDivPerYear(int divPerYear) {
        this.divPerYear = divPerYear;
    }

    public TransactionType getDivXaType() {
        return divXaType;
    }

    public void setDivXaType(TransactionType divXaType) {
        this.divXaType = divXaType;
    }

    public Country getDivCountry() {
        return divCountry;
    }

    public void setDivCountry(Country divCountry) {
        this.divCountry = divCountry;
    }

    public int getDivSymbolId() {
        return divSymbolId;
    }

    public void setDivSymbolId(int divSymbolId) {
        this.divSymbolId = divSymbolId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
