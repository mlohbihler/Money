package com.serotonin.money.web.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.serotonin.db.pair.StringStringPair;
import com.serotonin.money.dao.BaseDao;
import com.serotonin.money.util.Utils;
import com.serotonin.money.vo.Account;
import com.serotonin.money.vo.Asset;
import com.serotonin.money.vo.Dividend;
import com.serotonin.money.vo.tx.Transaction;
import com.serotonin.money.vo.tx.TransactionException;
import com.serotonin.money.vo.tx.TransactionType;
import com.serotonin.money.web.controller.result.ControllerResult;

public class DividendServlet extends AbstractController {
    private static final long serialVersionUID = 1L;

    @Override
    public ControllerResult handle(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model)
            throws IOException, ServletException {
        if (isPost(request)) {
            if (request.getParameter("generate") != null) {
                // Cutoff date
                GregorianCalendar gc = new GregorianCalendar();
                final int lastYear = gc.get(Calendar.YEAR) - 1;
                gc.add(Calendar.YEAR, 1);
                final Date cutoff = new Date(gc.getTimeInMillis());

                // Accounts
                final List<Account> accounts = BaseDao.accountDao.get();
                for (final Account account : accounts) {
                    // Add transactions.
                    for (final Transaction tx : BaseDao.transactionDao.get(account.getId())) {
                        try {
                            tx.apply(account);
                        }
                        catch (final TransactionException e) {
                            // Ignore
                            throw new ServletException(e);
                        }
                        tx.setLastCashBalance(account.getCashBalance());
                    }
                    BaseDao.assetDao.populate(account.getAssets());
                }

                gc = new GregorianCalendar();
                gc.add(Calendar.DATE, -5);
                final Date nowish = gc.getTime();

                // Generate dividends up to the cutoff for all held assets
                for (final Account account : accounts) {
                    for (final Asset asset : account.getAssets()) {
                        if (asset.getQuantity().doubleValue() > 0.000001 && asset.getAssetInfo() != null
                                && asset.getAssetInfo().getDivPerYear() > 0) {
                            Date latest = BaseDao.dividendDao.getLatestDividendDate(account.getId(), asset.getSymbol());
                            if (latest == null)
                                latest = BaseDao.transactionDao.getLatestDividendDate(account.getId(),
                                        asset.getSymbol());
                            if (latest == null || latest.before(nowish))
                                latest = nowish;

                            final MonthYear monthYear = new MonthYear();
                            monthYear.month = asset.getAssetInfo().getDivMonth();
                            monthYear.year = lastYear;

                            BigDecimal amountBD = asset.getQuantity();
                            amountBD = amountBD.multiply(new BigDecimal(asset.getAssetInfo().getDivAmount()));
                            final double amount = amountBD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                            if (amount > 0) {
                                while (true) {
                                    gc = new GregorianCalendar(monthYear.year, monthYear.month - 1, 1);
                                    int day = asset.getAssetInfo().getDivDay();
                                    if (day > gc.getActualMaximum(Calendar.DATE))
                                        day = gc.getActualMaximum(Calendar.DATE);
                                    gc.set(Calendar.DATE, day);

                                    final Date divDate = gc.getTime();
                                    if (cutoff.before(divDate))
                                        break;

                                    gc.add(Calendar.DATE, -5);
                                    if (latest.before(gc.getTime())) {
                                        final Dividend dividend = new Dividend();
                                        dividend.setAccountId(account.getId());
                                        dividend.setXaDate(divDate);
                                        dividend.setSymbol(asset.getSymbol());
                                        dividend.setXaType(asset.getAssetInfo().getDivXaType());
                                        dividend.setShares(asset.getQuantity().doubleValue());
                                        dividend.setDivAmount(asset.getAssetInfo().getDivAmount());
                                        dividend.setAmount(amount);
                                        BaseDao.dividendDao.save(dividend);
                                    }

                                    monthYear.add(12 / asset.getAssetInfo().getDivPerYear());
                                }
                            }
                        }
                    }
                }
            }
            else if (request.getParameter("delete") != null) {
                final int id = getIntParameter(request, "id", 0);
                BaseDao.dividendDao.delete(id);
            }
        }

        try {
            model.put("accounts", Utils.accountsWithTransactions());
        }
        catch (final TransactionException e) {
            throw new ServletException(e);
        }

        final List<StringStringPair> xaTypes = new ArrayList<StringStringPair>();
        for (final TransactionType type : TransactionType.values()) {
            if (type.dividend)
                xaTypes.add(new StringStringPair(type.name(), type.prettyName));
        }
        model.put("xaTypes", xaTypes);
        model.put("dividends", BaseDao.dividendDao.get());

        return null;
    }

    static class MonthYear {
        int month;
        int year;

        void add(int months) {
            month += months;
            if (month > 12) {
                year++;
                month %= 12;
            }
        }
    }
}
