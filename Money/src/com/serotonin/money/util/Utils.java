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

    public static String getExchange(final String symbol) {
        final int colon = symbol.indexOf(":");
        if (colon == -1)
            return null;
        return symbol.substring(0, colon);
    }

    public static String getSymbolSansExchange(final String symbol) {
        final int colon = symbol.indexOf(":");
        if (colon == -1)
            return symbol;
        return symbol.substring(colon + 1);
    }

    public static String urlEncode(final String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new ShouldNeverHappenException(e);
        }
    }

    public static String urlDecode(final String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new ShouldNeverHappenException(e);
        }
    }

    public static String cleanParameter(final String s) {
        if (s == null)
            return null;
        String ss = s.replaceAll("<", "&lt;");
        ss = ss.replaceAll(">", "&gt;");
        ss = ss.replaceAll("script", "");
        ss = ss.replaceAll("iframe", "");
        return ss;
    }

    public static List<Account> accountsWithTransactions() throws TransactionException {
        final List<Account> accounts = BaseDao.accountDao.get();
        accountsWithTransactions(accounts);
        return accounts;
    }

    public static void accountsWithTransactions(final List<Account> accounts) throws TransactionException {
        for (final Account account : accounts)
            addTransactions(account);
    }

    public static void addTransactions(final Account account) throws TransactionException {
        for (final Transaction tx : BaseDao.transactionDao.get(account.getId())) {
            tx.apply(account);
            tx.setLastCashBalance(account.getCashBalance());
        }
    }
}
