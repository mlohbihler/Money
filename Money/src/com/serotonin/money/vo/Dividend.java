package com.serotonin.money.vo;

import java.util.Date;

import com.serotonin.money.util.Utils;
import com.serotonin.money.vo.tx.TransactionType;

public class Dividend {
    private int id = -1;
    private int accountId;
    private String accountName;
    private Date xaDate;
    private Date exDivDate;
    private String symbol;
    private String assetName;
    private TransactionType xaType;
    private double shares;
    private double divAmount;
    private double amount;

    public String getPrettyXaDate() {
        return Utils.XA_DATE_FORMAT.format(xaDate);
    }

    public String getPrettyExDivDate() {
        if (exDivDate == null)
            return "";
        return Utils.XA_DATE_FORMAT.format(exDivDate);
    }

    public String getPrettyAmount() {
        return Utils.AMOUNT_FORMAT.format(amount);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Date getXaDate() {
        return xaDate;
    }

    public void setXaDate(Date xaDate) {
        this.xaDate = xaDate;
    }

    public Date getExDivDate() {
        return exDivDate;
    }

    public void setExDivDate(Date exDivDate) {
        this.exDivDate = exDivDate;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public TransactionType getXaType() {
        return xaType;
    }

    public void setXaType(TransactionType xaType) {
        this.xaType = xaType;
    }

    public double getShares() {
        return shares;
    }

    public void setShares(double shares) {
        this.shares = shares;
    }

    public double getDivAmount() {
        return divAmount;
    }

    public void setDivAmount(double divAmount) {
        this.divAmount = divAmount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
