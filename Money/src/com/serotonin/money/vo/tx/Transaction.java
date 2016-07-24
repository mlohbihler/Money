package com.serotonin.money.vo.tx;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.serotonin.money.vo.Account;

abstract public class Transaction {
    public static Transaction createTransaction(int id, int accountId, Date xaDate, TransactionType xaType,
            String symbol, String symbol2, Double shares, Double price, Double fx, Double fee, Double book) {
        Transaction tx = xaType.createTransaction();
        tx.data(id, accountId, xaDate, symbol, symbol2, bd(shares), bd(price), bd(fx), bd(fee), bd(book));
        return tx;
    }

    private static BigDecimal bd(Double d) {
        if (d == null)
            return null;
        return new BigDecimal(d);
    }

    private int id;
    private int accountId;
    private Date transactionDate;
    private String symbol;
    private String symbol2;
    private BigDecimal shares;
    private BigDecimal price;
    private BigDecimal foreignExchange;
    private BigDecimal fee;
    private BigDecimal bookValue;
    private BigDecimal lastCashBalance;

    protected void data(int id, int accountId, Date transactionDate, String symbol, String symbol2, BigDecimal shares,
            BigDecimal price, BigDecimal foreignExchange, BigDecimal fee, BigDecimal bookValue) {
        this.id = id;
        this.accountId = accountId;
        this.transactionDate = transactionDate;
        this.symbol = symbol;
        this.symbol2 = symbol2;
        this.shares = shares;
        this.price = price;
        this.foreignExchange = foreignExchange;
        this.fee = fee;
        this.bookValue = bookValue;
    }

    abstract public TransactionType getTransactionType();

    public String getPrettyTransactionType() {
        return getTransactionType().prettyName;
    }

    abstract public void apply(Account account) throws TransactionException;

    abstract public BigDecimal getCashAmount();

    public String getPrettyCashAmount() {
        BigDecimal cash = getCashAmount();
        if (cash == null)
            return "";
        return new DecimalFormat("$0.00").format(cash);
    }

    public String getPrettyLastCashBalance() {
        if (lastCashBalance == null)
            return "";
        return new DecimalFormat("$0.00").format(lastCashBalance);
    }

    public int getId() {
        return id;
    }

    public int getAccountId() {
        return accountId;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public String getPrettyTransactionDate() {
        return new SimpleDateFormat("yyyy/MM/dd").format(transactionDate);
    }

    public boolean isEmpty(BigDecimal d) {
        return d == null || d.doubleValue() == 0;
    }

    public boolean isGTZero(BigDecimal d) {
        return d != null && d.doubleValue() > 0;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getSymbol2() {
        return symbol2;
    }

    public BigDecimal getShares() {
        return shares;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getForeignExchange() {
        return foreignExchange;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public BigDecimal getBookValue() {
        return bookValue;
    }

    public void setBookValue(BigDecimal bookValue) {
        this.bookValue = bookValue;
    }

    public BigDecimal getLastCashBalance() {
        return lastCashBalance;
    }

    public void setLastCashBalance(BigDecimal lastCashBalance) {
        this.lastCashBalance = lastCashBalance;
    }

    @Override
    public String toString() {
        return getTransactionType() + "[transactionDate=" + transactionDate + ", symbol=" + symbol + ", symbol2="
                + symbol2 + ", shares=" + shares + ", price=" + price + ", foreignExchange=" + foreignExchange
                + ", fee=" + fee + ", bookValue=" + bookValue + "]";
    }
}
