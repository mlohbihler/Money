package com.serotonin.money.web.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.serotonin.db.pair.StringStringPair;
import com.serotonin.money.dao.BaseDao;
import com.serotonin.money.util.Utils;
import com.serotonin.money.vo.Account;
import com.serotonin.money.vo.tx.Buy;
import com.serotonin.money.vo.tx.BuyGIC;
import com.serotonin.money.vo.tx.CashAdjustment;
import com.serotonin.money.vo.tx.CashDividend;
import com.serotonin.money.vo.tx.Contribution;
import com.serotonin.money.vo.tx.Deposit;
import com.serotonin.money.vo.tx.ExchangeAdjustment;
import com.serotonin.money.vo.tx.Fee;
import com.serotonin.money.vo.tx.FeeRebate;
import com.serotonin.money.vo.tx.Grant;
import com.serotonin.money.vo.tx.Interest;
import com.serotonin.money.vo.tx.ManagementFee;
import com.serotonin.money.vo.tx.Merger;
import com.serotonin.money.vo.tx.ReinvestedDividend;
import com.serotonin.money.vo.tx.Sell;
import com.serotonin.money.vo.tx.Split;
import com.serotonin.money.vo.tx.SplitCash;
import com.serotonin.money.vo.tx.StockDividend;
import com.serotonin.money.vo.tx.Tax;
import com.serotonin.money.vo.tx.Transaction;
import com.serotonin.money.vo.tx.TransactionException;
import com.serotonin.money.vo.tx.TransactionType;
import com.serotonin.money.vo.tx.TransferIn;
import com.serotonin.money.vo.tx.Withdrawal;
import com.serotonin.money.web.controller.result.ControllerResult;
import com.serotonin.money.web.controller.result.RedirectResult;

public class TransactionServlet extends AbstractController {
    private static final long serialVersionUID = 1L;

    @Override
    public ControllerResult handle(final HttpServletRequest request, final HttpServletResponse response,
            final Map<String, Object> model) throws IOException, ServletException {
        model.put("accounts", BaseDao.accountDao.get());
        model.put("symbols", BaseDao.assetDao.getSymbols());

        final List<StringStringPair> xaTypes = new ArrayList<>();
        for (final TransactionType type : TransactionType.values())
            xaTypes.add(new StringStringPair(type.name(), type.prettyName));
        model.put("xaTypes", xaTypes);

        if (isGet(request)) {
            if (request.getParameter("editId") != null) {
                final Transaction tx = BaseDao.transactionDao.getById(Integer.parseInt(request.getParameter("editId")));
                if (tx != null) {
                    model.put("id", tx.getId());
                    model.put("accountId", tx.getAccountId());
                    model.put("xaDate", Utils.XA_DATE_FORMAT.format(tx.getTransactionDate()));
                    model.put("xaType", tx.getTransactionType().name());

                    if (tx.getTransactionType() == TransactionType.BUY) {
                        model.put("BUYSymbol", tx.getSymbol());
                        model.put("BUYShares", tx.getShares());
                        model.put("BUYPrice", tx.getPrice());
                        model.put("BUYFx", tx.getForeignExchange());
                        model.put("BUYFee", tx.getFee());
                    } else if (tx.getTransactionType() == TransactionType.BUYGIC) {
                        model.put("BUYGICSymbol", tx.getSymbol());
                        model.put("BUYGICDescription", tx.getSymbol2());
                        model.put("BUYGICAmount", tx.getPrice());
                        model.put("BUYGICRate", tx.getForeignExchange());
                        model.put("BUYGICTerm", tx.getFee());
                    } else if (tx.getTransactionType() == TransactionType.CASHADJ) {
                        model.put("CASHADJAmount", tx.getPrice());
                    } else if (tx.getTransactionType() == TransactionType.CASHDIV) {
                        model.put("CASHDIVSymbol", tx.getSymbol());
                        model.put("CASHDIVAmount", tx.getPrice());
                    } else if (tx.getTransactionType() == TransactionType.CONTRIBUTION) {
                        model.put("CONTRIBUTIONAmount", tx.getPrice());
                    } else if (tx.getTransactionType() == TransactionType.DEPOSIT) {
                        model.put("DEPOSITBene", tx.getSymbol2());
                        model.put("DEPOSITAmount", tx.getPrice());
                    } else if (tx.getTransactionType() == TransactionType.EXCHADJ) {
                        model.put("EXCHADJSymbol", tx.getSymbol());
                        model.put("EXCHADJShares", tx.getShares());
                        model.put("EXCHADJPrice", tx.getPrice());
                    } else if (tx.getTransactionType() == TransactionType.FEE) {
                        model.put("FEEAmount", tx.getFee());
                    } else if (tx.getTransactionType() == TransactionType.FEE_REBATE) {
                        model.put("FEE_REBATESymbol", tx.getSymbol());
                        model.put("FEE_REBATEAmount", tx.getFee());
                    } else if (tx.getTransactionType() == TransactionType.GRANT) {
                        model.put("GRANTBene", tx.getSymbol());
                        model.put("GRANTAmount", tx.getPrice());
                    } else if (tx.getTransactionType() == TransactionType.INTEREST) {
                        model.put("INTERESTAmount", tx.getPrice());
                    } else if (tx.getTransactionType() == TransactionType.MANAGEMENT_FEE) {
                        model.put("MANAGEMENT_FEESymbol", tx.getSymbol());
                        model.put("MANAGEMENT_FEEShares", tx.getShares());
                        model.put("MANAGEMENT_FEEPrice", tx.getPrice());
                    } else if (tx.getTransactionType() == TransactionType.MERGER) {
                        model.put("MERGERFromSymbol", tx.getSymbol());
                        model.put("MERGERFromShares", tx.getShares());
                        model.put("MERGERToSymbol", tx.getSymbol2());
                        model.put("MERGERToShares", tx.getPrice());
                    } else if (tx.getTransactionType() == TransactionType.REINVDIV) {
                        model.put("REINVDIVSymbol", tx.getSymbol());
                        model.put("REINVDIVShares", tx.getShares());
                        model.put("REINVDIVPrice", tx.getPrice());
                    } else if (tx.getTransactionType() == TransactionType.SELL) {
                        model.put("SELLSymbol", tx.getSymbol());
                        model.put("SELLShares", tx.getShares());
                        model.put("SELLPrice", tx.getPrice());
                        model.put("SELLFx", tx.getForeignExchange());
                        model.put("SELLFee", tx.getFee());
                    } else if (tx.getTransactionType() == TransactionType.SPLIT) {
                        model.put("SPLITSymbol", tx.getSymbol());
                        model.put("SPLITShares", tx.getShares());
                    } else if (tx.getTransactionType() == TransactionType.SPLIT_CASH) {
                        model.put("SPLIT_CASHSymbol", tx.getSymbol());
                        model.put("SPLIT_CASHAmount", tx.getPrice());
                    } else if (tx.getTransactionType() == TransactionType.STOCKDIV) {
                        model.put("STOCKDIVSymbol", tx.getSymbol());
                        model.put("STOCKDIVAmount", tx.getPrice());
                    } else if (tx.getTransactionType() == TransactionType.TAX) {
                        model.put("TAXSymbol", tx.getSymbol());
                        model.put("TAXAmount", tx.getFee());
                    } else if (tx.getTransactionType() == TransactionType.TRANSFER_IN) {
                        model.put("TRANSFER_INInKind", tx.getSymbol() != null);
                        model.put("TRANSFER_INAmount", tx.getPrice());
                        model.put("TRANSFER_INSymbol", tx.getSymbol());
                        model.put("TRANSFER_INShares", tx.getShares());
                        model.put("TRANSFER_INPrice", tx.getPrice());
                        model.put("TRANSFER_INBook", tx.getBookValue());
                    } else if (tx.getTransactionType() == TransactionType.WITHDRAWAL) {
                        model.put("WITHDRAWALAmount", tx.getPrice());
                    }
                }
            } else {
                // The url to which to return upon successful save of the transaction
                addParameterToModel(request, "ret", model);
                addParameterToModel(request, "delDiv", model);

                addParameterToModel(request, "accountId", model);
                addParameterToModel(request, "xaDate", model, Utils.XA_DATE_FORMAT.format(new Date()));

                final TransactionType xaType = TransactionType.forString(request.getParameter("xaType"));
                if (xaType != null) {
                    model.put("xaType", xaType);

                    addParameterToModel(request, xaType.name() + "Symbol", model);
                    addParameterToModel(request, xaType.name() + "Shares", model);
                    addParameterToModel(request, xaType.name() + "Price", model);
                    addParameterToModel(request, xaType.name() + "Fx", model);
                    addParameterToModel(request, xaType.name() + "Fee", model);
                    addParameterToModel(request, xaType.name() + "Amount", model);
                    addParameterToModel(request, xaType.name() + "Bene", model);
                    addParameterToModel(request, xaType.name() + "FromShares", model);
                    addParameterToModel(request, xaType.name() + "ToShares", model);
                    addParameterToModel(request, xaType.name() + "InKind", model);
                    addParameterToModel(request, xaType.name() + "Book", model);
                }
            }
        } else if (isPost(request)) {
            final String ret = getAndPutParameter(request, "ret", model);
            final String delDiv = getAndPutParameter(request, "delDiv", model);

            final int id = getAndPutIntParameter(request, "id", -1, model);
            final int accountId = getAndPutIntParameter(request, "accountId", 0, model);
            final String dateStr = getAndPutParameter(request, "xaDate", model);
            final TransactionType type = TransactionType.valueOf(getAndPutParameter(request, "xaType", model));

            Date date = null;
            try {
                date = Utils.XA_DATE_FORMAT.parse(dateStr);
            } catch (final ParseException e) {
                model.put("dateError", e.getMessage());
            }

            final String buySymbol = getAndPutParameter(request, "BUYSymbol", model);
            final double buyShares = getAndPutDoubleParameter(request, "BUYShares", 0, model);
            final double buyPrice = getAndPutDoubleParameter(request, "BUYPrice", 0, model);
            final double buyFx = getAndPutDoubleParameter(request, "BUYFx", 0, model);
            final double buyFee = getAndPutDoubleParameter(request, "BUYFee", 0, model);
            final String buygicSymbol = getAndPutParameter(request, "BUYGICSymbol", model);
            final String buygicDescription = getAndPutParameter(request, "BUYGICDescription", model);
            final double buygicAmount = getAndPutDoubleParameter(request, "BUYGICAmount", 0, model);
            final double buygicRate = getAndPutDoubleParameter(request, "BUYGICRate", 0, model);
            final double buygicTerm = getAndPutDoubleParameter(request, "BUYGICTerm", 5, model);
            final double cashadjAmount = getAndPutDoubleParameter(request, "CASHADJAmount", 0, model);
            final String cashdivSymbol = getAndPutParameter(request, "CASHDIVSymbol", model);
            final double cashdivAmount = getAndPutDoubleParameter(request, "CASHDIVAmount", 0, model);
            final double contributionAmount = getAndPutDoubleParameter(request, "CONTRIBUTIONAmount", 0, model);
            final String depositBene = getAndPutParameter(request, "DEPOSITBene", model);
            final double depositAmount = getAndPutDoubleParameter(request, "DEPOSITAmount", 0, model);
            final String exchadjSymbol = getAndPutParameter(request, "EXCHADJSymbol", model);
            final int exchadjShares = getAndPutIntParameter(request, "EXCHADJShares", 0, model);
            final double exchadjPrice = getAndPutDoubleParameter(request, "EXCHADJPrice", 0, model);
            final double feeAmount = getAndPutDoubleParameter(request, "FEEAmount", 0, model);
            final String feerebateSymbol = getAndPutParameter(request, "FEE_REBATESymbol", model);
            final double feerebateAmount = getAndPutDoubleParameter(request, "FEE_REBATEAmount", 0, model);
            final String grantBene = getAndPutParameter(request, "GRANTBene", model);
            final double grantAmount = getAndPutDoubleParameter(request, "GRANTAmount", 0, model);
            final double interestAmount = getAndPutDoubleParameter(request, "INTERESTAmount", 0, model);
            final String mgfeeSymbol = getAndPutParameter(request, "MANAGEMENT_FEESymbol", model);
            final double mgfeeShares = getAndPutDoubleParameter(request, "MANAGEMENT_FEEShares", 0, model);
            final double mgfeePrice = getAndPutDoubleParameter(request, "MANAGEMENT_FEEPrice", 0, model);
            final String mergerFromSymbol = getAndPutParameter(request, "MERGERFromSymbol", model);
            final double mergerFromShares = getAndPutDoubleParameter(request, "MERGERFromShares", 0, model);
            final String mergerToSymbol = getAndPutParameter(request, "MERGERToSymbol", model);
            final double mergerToShares = getAndPutDoubleParameter(request, "MERGERToShares", 0, model);
            final String reinvdivSymbol = getAndPutParameter(request, "REINVDIVSymbol", model);
            final double reinvdivShares = getAndPutDoubleParameter(request, "REINVDIVShares", 0, model);
            final double reinvdivPrice = getAndPutDoubleParameter(request, "REINVDIVPrice", 0, model);
            final String sellSymbol = getAndPutParameter(request, "SELLSymbol", model);
            final double sellShares = getAndPutDoubleParameter(request, "SELLShares", 0, model);
            final double sellPrice = getAndPutDoubleParameter(request, "SELLPrice", 0, model);
            final double sellFx = getAndPutDoubleParameter(request, "SELLFx", 0, model);
            final double sellFee = getAndPutDoubleParameter(request, "SELLFee", 0, model);
            final String splitSymbol = getAndPutParameter(request, "SPLITSymbol", model);
            final double splitShares = getAndPutDoubleParameter(request, "SPLITShares", 0, model);
            final String splitcashSymbol = getAndPutParameter(request, "SPLIT_CASHSymbol", model);
            final double splitcashAmount = getAndPutDoubleParameter(request, "SPLIT_CASHAmount", 0, model);
            final String stockdivSymbol = getAndPutParameter(request, "STOCKDIVSymbol", model);
            final double stockdivAmount = getAndPutDoubleParameter(request, "STOCKDIVAmount", 0, model);
            final String taxSymbol = getAndPutParameter(request, "TAXSymbol", model);
            final double taxAmount = getAndPutDoubleParameter(request, "TAXAmount", 0, model);
            final boolean transferinInKind = getAndPutBooleanParameter(request, "TRANSFER_INInKind", model);
            final double transferinAmount = getAndPutDoubleParameter(request, "TRANSFER_INAmount", 0, model);
            final String transferinSymbol = getAndPutParameter(request, "TRANSFER_INSymbol", model);
            final double transferinShares = getAndPutDoubleParameter(request, "TRANSFER_INShares", 0, model);
            final double transferinPrice = getAndPutDoubleParameter(request, "TRANSFER_INPrice", 0, model);
            final double transferinBook = getAndPutDoubleParameter(request, "TRANSFER_INBook", 0, model);
            final double withdrawalAmount = getAndPutDoubleParameter(request, "WITHDRAWALAmount", 0, model);

            try {
                Transaction xa = null;
                if (type == TransactionType.BUY)
                    xa = new Buy(id, accountId, date, buySymbol, buyShares, buyPrice, buyFx, buyFee);
                else if (type == TransactionType.BUYGIC)
                    xa = new BuyGIC(id, accountId, date, buygicSymbol, buygicDescription, buygicAmount, buygicRate,
                            buygicTerm);
                else if (type == TransactionType.CASHADJ)
                    xa = new CashAdjustment(id, accountId, date, cashadjAmount);
                else if (type == TransactionType.CASHDIV)
                    xa = new CashDividend(id, accountId, date, cashdivSymbol, cashdivAmount);
                else if (type == TransactionType.CONTRIBUTION)
                    xa = new Contribution(id, accountId, date, contributionAmount);
                else if (type == TransactionType.DEPOSIT)
                    xa = new Deposit(id, accountId, date, depositBene, depositAmount);
                else if (type == TransactionType.EXCHADJ)
                    xa = new ExchangeAdjustment(id, accountId, date, exchadjSymbol, exchadjShares, exchadjPrice);
                else if (type == TransactionType.FEE)
                    xa = new Fee(id, accountId, date, feeAmount);
                else if (type == TransactionType.FEE_REBATE)
                    xa = new FeeRebate(id, accountId, date, feerebateSymbol, feerebateAmount);
                else if (type == TransactionType.GRANT)
                    xa = new Grant(id, accountId, date, grantBene, grantAmount);
                else if (type == TransactionType.INTEREST)
                    xa = new Interest(id, accountId, date, interestAmount);
                else if (type == TransactionType.MANAGEMENT_FEE)
                    xa = new ManagementFee(id, accountId, date, mgfeeSymbol, mgfeeShares, mgfeePrice);
                else if (type == TransactionType.MERGER)
                    xa = new Merger(id, accountId, date, mergerFromSymbol, mergerToSymbol, mergerFromShares,
                            mergerToShares);
                else if (type == TransactionType.REINVDIV)
                    xa = new ReinvestedDividend(id, accountId, date, reinvdivSymbol, reinvdivShares, reinvdivPrice);
                else if (type == TransactionType.SELL)
                    xa = new Sell(id, accountId, date, sellSymbol, sellShares, sellPrice, sellFx, sellFee);
                else if (type == TransactionType.SPLIT)
                    xa = new Split(id, accountId, date, splitSymbol, splitShares);
                else if (type == TransactionType.SPLIT_CASH)
                    xa = new SplitCash(id, accountId, date, splitcashSymbol, splitcashAmount);
                else if (type == TransactionType.STOCKDIV)
                    xa = new StockDividend(id, accountId, date, stockdivSymbol, stockdivAmount);
                else if (type == TransactionType.TAX)
                    xa = new Tax(id, accountId, date, taxSymbol, taxAmount);
                else if (type == TransactionType.TRANSFER_IN) {
                    if (transferinInKind)
                        xa = new TransferIn(id, accountId, date, transferinAmount);
                    else
                        xa = new TransferIn(id, accountId, date, transferinSymbol, transferinShares, transferinPrice,
                                transferinBook);
                } else if (type == TransactionType.WITHDRAWAL)
                    xa = new Withdrawal(id, accountId, date, withdrawalAmount);

                if (date != null && xa != null) {
                    boolean xaApplied = false;
                    final Account account = BaseDao.accountDao.get(accountId);
                    for (final Transaction tx : BaseDao.transactionDao.get(account.getId())) {
                        if (!xaApplied && xa.getTransactionDate().before(tx.getTransactionDate())) {
                            // Time to apply this transaction.
                            xa.apply(account);
                            xa.setLastCashBalance(account.getCashBalance());
                            xaApplied = true;
                        }
                        if (xa.getId() != tx.getId()) {
                            tx.apply(account);
                            tx.setLastCashBalance(account.getCashBalance());
                        }
                    }

                    if (!xaApplied) {
                        xa.apply(account);
                        xa.setLastCashBalance(account.getCashBalance());
                    }

                    // Transaction is ok. Add to database
                    BaseDao.transactionDao.save(xa);

                    if (!org.apache.commons.lang3.StringUtils.isEmpty(delDiv))
                        BaseDao.dividendDao.delete(Integer.parseInt(delDiv));

                    if (!org.apache.commons.lang3.StringUtils.isEmpty(ret))
                        return new RedirectResult(ret);
                }
            } catch (final TransactionException e) {
                model.put("xaException", e.getMessage());
            }
        }

        return null;
    }
}
