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

    public DwrResponse getMarketPrice(final String symbol) {
        AssetInfo assetInfo = BaseDao.assetDao.get(symbol);
        if (assetInfo == null) {
            assetInfo = new AssetInfo();
            assetInfo.setSymbol(symbol);
            assetInfo.setName("");
        }

        final DwrResponse dr = new DwrResponse();

        try {
            Market.populateMarketValue(assetInfo);
            BaseDao.assetDao.save(assetInfo);

            dr.addData("price", assetInfo.getMarketPrice());
            dr.addData("time", assetInfo.getPrettyMarketTime());
        } catch (final Exception e) {
            LOG.error("", e);
            dr.addMessage("Error: " + e.getMessage());
        }

        return dr;
    }

    public DwrResponse getAllMarketPrice() {
        final List<AssetInfo> assetInfos = BaseDao.assetDao.get();

        final DwrResponse dr = new DwrResponse();

        try {
            Market.populateMarketValues(assetInfos);
            for (final AssetInfo assetInfo : assetInfos) {
                if (assetInfo.getMarketPrice() > 0) {
                    BaseDao.assetDao.save(assetInfo);

                    final Map<String, Object> map = new HashMap<>();
                    map.put("price", assetInfo.getMarketPrice());
                    map.put("time", assetInfo.getPrettyMarketTime());
                    dr.addData(assetInfo.getSymbol(), map);
                }
            }
        } catch (final Exception e) {
            LOG.error("", e);
            dr.addMessage("Error: " + e.getMessage());
        }

        return dr;
    }

    public List<Transaction> getTransactions(final int accountId, final String symbol) {
        //        List<Transaction> txs;
        //        if (symbol == null)
        //            txs = BaseDao.transactionDao.get(accountId);
        //        else
        //            txs = BaseDao.transactionDao.get(accountId, symbol);

        // Get all of the transactions for the account.
        List<Transaction> txs = BaseDao.transactionDao.get(accountId);

        // Apply the transactions to the account.
        final Account account = new Account();
        for (final Transaction tx : txs) {
            try {
                tx.apply(account);
            } catch (final TransactionException e) {
                // Ignore
                throw new RuntimeException(e);
            }
            tx.setLastCashBalance(account.getCashBalance());
        }

        if (symbol != null) {
            // Extract just the transactions for the given symbol
            final List<Transaction> symbolTxs = new ArrayList<>();
            for (final Transaction tx : txs) {
                if (symbol.equals(tx.getSymbol()) || symbol.equals(tx.getSymbol2()))
                    symbolTxs.add(tx);
            }
            txs = symbolTxs;
        }

        return txs;
    }

    public void deleteTransaction(final int id) {
        BaseDao.transactionDao.delete(id);
    }

    public Dividend getDividend(final int id) {
        return BaseDao.dividendDao.get(id);
    }

    public String updateDividend(final int id, final String xaDate, final String exDiv, final double shares,
            final double divAmount, final double amount, final String xaType) {
        final Dividend d = BaseDao.dividendDao.get(id);

        try {
            d.setXaDate(Utils.XA_DATE_FORMAT.parse(xaDate));
        } catch (final ParseException e) {
            return e.getMessage();
        }

        try {
            d.setExDivDate(Utils.XA_DATE_FORMAT.parse(exDiv));
        } catch (final ParseException e) {
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
