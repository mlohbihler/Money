package com.serotonin.money.web.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.serotonin.money.dao.BaseDao;
import com.serotonin.money.util.Utils;
import com.serotonin.money.vo.Account;
import com.serotonin.money.vo.Asset;
import com.serotonin.money.vo.AssetInvestment;
import com.serotonin.money.vo.tx.TransactionException;
import com.serotonin.money.web.controller.result.ControllerResult;
import com.serotonin.money.web.controller.result.JspResult;
import com.serotonin.money.web.controller.result.RedirectResult;

public class AccountServlet extends AbstractController {
    private static final long serialVersionUID = 1L;

    @Override
    public ControllerResult handle(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model)
            throws IOException, ServletException {
        String path = request.getRequestURI();
        String accountName = path.substring(path.lastIndexOf('/') + 1);
        Account account = BaseDao.accountDao.get(accountName);

        if (account == null)
            return new RedirectResult("/accounts");

        // List of accounts for the header.
        model.put("accounts", BaseDao.accountDao.get());

        // Add transactions.
        try {
            Utils.addTransactions(account);
        }
        catch (TransactionException e) {
            // Ignore
            throw new ServletException(e);
        }

        // Get the cutoff date for assets with a zero balance.
        GregorianCalendar gc = new GregorianCalendar();
        gc.add(Calendar.DATE, -7);
        Date zeroCutoff = gc.getTime();

        // Fold in market values.
        BaseDao.assetDao.populate(account.getAssets());

        // Total gain
        BigDecimal gain = new BigDecimal(0);
        BigDecimal market = new BigDecimal(0);
        for (Asset asset : account.getAssets()) {
            gain = gain.add(asset.getReturn());

            if (asset.getMarketValue() != null)
                market = market.add(asset.getQuantity().multiply(asset.getMarketValue()));

            if (asset.getQuantity().doubleValue() < 0.000001 && !asset.getLastTransactionDate().after(zeroCutoff))
                asset.setPastCutoff(true);
        }

        // Total invested and market value
        BigDecimal invested = new BigDecimal(0);
        for (AssetInvestment ai : account.getInvestments())
            invested = invested.add(ai.getAmount());

        model.put("account", account);
        model.put("totalGain", gain);
        model.put("totalInvested", invested);
        model.put("market", market);

        return new JspResult("/account");
    }
}
