package com.serotonin.money.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.money.dao.BaseDao;
import com.serotonin.money.vo.Account;
import com.serotonin.money.vo.tx.Transaction;
import com.serotonin.money.vo.tx.TransactionException;

public class Utils {
    public static final SimpleDateFormat XA_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
    public static final DecimalFormat AMOUNT_FORMAT = new DecimalFormat("0.00");

    static {
        XA_DATE_FORMAT.setLenient(false);
    }

    public static String getExchange(String symbol) {
        int colon = symbol.indexOf(":");
        if (colon == -1)
            return null;
        return symbol.substring(0, colon);
    }

    public static String getSymbolSansExchange(String symbol) {
        int colon = symbol.indexOf(":");
        if (colon == -1)
            return symbol;
        return symbol.substring(colon + 1);
    }

    public static String urlEncode(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new ShouldNeverHappenException(e);
        }
    }

    public static String urlDecode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new ShouldNeverHappenException(e);
        }
    }

    public static String cleanParameter(String s) {
        if (s == null)
            return null;
        s = s.replaceAll("<", "&lt;");
        s = s.replaceAll(">", "&gt;");
        s = s.replaceAll("script", "");
        s = s.replaceAll("iframe", "");
        return s;
    }

    public static List<Account> accountsWithTransactions() throws TransactionException {
        List<Account> accounts = BaseDao.accountDao.get();
        accountsWithTransactions(accounts);
        return accounts;
    }

    public static void accountsWithTransactions(List<Account> accounts) throws TransactionException {
        for (Account account : accounts)
            addTransactions(account);
    }

    public static void addTransactions(Account account) throws TransactionException {
        for (Transaction tx : BaseDao.transactionDao.get(account.getId())) {
            tx.apply(account);
            tx.setLastCashBalance(account.getCashBalance());
        }
    }
}
