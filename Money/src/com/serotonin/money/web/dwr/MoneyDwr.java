package com.serotonin.money.web.dwr;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.money.dao.BaseDao;
import com.serotonin.money.util.Market;
import com.serotonin.money.util.Utils;
import com.serotonin.money.vo.Account;
import com.serotonin.money.vo.AssetInfo;
import com.serotonin.money.vo.Dividend;
import com.serotonin.money.vo.tx.Transaction;
import com.serotonin.money.vo.tx.TransactionException;
import com.serotonin.money.vo.tx.TransactionType;
import com.serotonin.web.dwr.DwrResponse;

public class MoneyDwr {
    private static final Log LOG = LogFactory.getLog(MoneyDwr.class);

    public DwrResponse getMarketPrice(String symbol) {
        AssetInfo assetInfo = BaseDao.assetDao.get(symbol);
        if (assetInfo == null) {
            assetInfo = new AssetInfo();
            assetInfo.setSymbol(symbol);
            assetInfo.setName("");
        }

        DwrResponse dr = new DwrResponse();

        try {
            Market.populateMarketValue(assetInfo);
            BaseDao.assetDao.save(assetInfo);

            dr.addData("price", assetInfo.getMarketPrice());
            dr.addData("time", assetInfo.getPrettyMarketTime());
        }
        catch (Exception e) {
            LOG.error("", e);
            dr.addMessage("Error: " + e.getMessage());
        }

        return dr;
    }

    public DwrResponse getAllMarketPrice() {
        List<AssetInfo> assetInfos = BaseDao.assetDao.get();

        DwrResponse dr = new DwrResponse();

        try {
            Market.populateMarketValues(assetInfos);
            for (AssetInfo assetInfo : assetInfos) {
                if (assetInfo.getMarketPrice() > 0) {
                    BaseDao.assetDao.save(assetInfo);

                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("price", assetInfo.getMarketPrice());
                    map.put("time", assetInfo.getPrettyMarketTime());
                    dr.addData(assetInfo.getSymbol(), map);
                }
            }
        }
        catch (Exception e) {
            LOG.error("", e);
            dr.addMessage("Error: " + e.getMessage());
        }

        return dr;
    }

    public List<Transaction> getTransactions(int accountId, String symbol) {
        //        List<Transaction> txs;
        //        if (symbol == null)
        //            txs = BaseDao.transactionDao.get(accountId);
        //        else
        //            txs = BaseDao.transactionDao.get(accountId, symbol);

        // Get all of the transactions for the account.
        List<Transaction> txs = BaseDao.transactionDao.get(accountId);

        // Apply the transactions to the account.
        Account account = new Account();
        for (Transaction tx : txs) {
            try {
                tx.apply(account);
            }
            catch (TransactionException e) {
                // Ignore
                throw new RuntimeException(e);
            }
            tx.setLastCashBalance(account.getCashBalance());
        }

        if (symbol != null) {
            // Extract just the transactions for the given symbol
            List<Transaction> symbolTxs = new ArrayList<Transaction>();
            for (Transaction tx : txs) {
                if (symbol.equals(tx.getSymbol()) || symbol.equals(tx.getSymbol2()))
                    symbolTxs.add(tx);
            }
            txs = symbolTxs;
        }

        return txs;
    }

    public void deleteTransaction(int id) {
        BaseDao.transactionDao.delete(id);
    }

    public Dividend getDividend(int id) {
        return BaseDao.dividendDao.get(id);
    }

    public String updateDividend(int id, String xaDate, String exDiv, double shares, double divAmount, double amount,
            String xaType) {
        Dividend d = BaseDao.dividendDao.get(id);

        try {
            d.setXaDate(Utils.XA_DATE_FORMAT.parse(xaDate));
        }
        catch (ParseException e) {
            return e.getMessage();
        }

        try {
            d.setExDivDate(Utils.XA_DATE_FORMAT.parse(exDiv));
        }
        catch (ParseException e) {
            // ignore
        }

        d.setShares(shares);
        d.setDivAmount(divAmount);
        d.setAmount(amount);
        d.setXaType(TransactionType.forString(xaType));

        BaseDao.dividendDao.save(d);

        return null;
    }
}
