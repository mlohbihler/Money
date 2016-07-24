package com.serotonin.money.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.serotonin.money.vo.tx.Transaction;
import com.serotonin.money.vo.tx.TransactionType;

public class TransactionDao extends BaseDao {
    private static final String SELECT = "SELECT id, accountId, xaDate, xaType, symbol, symbol2, shares, price, " //
            + "  exchange, fee, book " //
            + "FROM Transactions";

    public List<Transaction> get(int accountId) {
        return ejt.query(SELECT + " WHERE accountId=? ORDER BY xaDate", new Object[] { accountId }, xaRM);
    }

    public List<Transaction> get(int accountId, String symbol) {
        if (symbol == null)
            return ejt.query(SELECT + " WHERE accountId=? AND symbol IS NULL AND symbol2 IS NULL ORDER BY xaDate",
                    new Object[] { accountId }, xaRM);

        return ejt.query(SELECT + " WHERE accountId=? AND (symbol=? OR symbol2=?) ORDER BY xaDate", new Object[] {
                accountId, symbol, symbol }, xaRM);
    }

    public Transaction getById(int id) {
        return ejt.queryForObject(SELECT + " WHERE id=?", new Object[] { id }, xaRM, null);
    }

    public Date getLatestDividendDate(int accountId, String symbol) {
        return ejt.queryForObject("SELECT max(xaDate) FROM Transactions " //
                + "WHERE accountId=? AND symbol=? AND (xaType='CASHDIV' OR xaType='STOCKDIV' OR xaType='REINVDIV')",
                new Object[] { accountId, symbol }, Date.class, null);
    }

    static XaRM xaRM = new XaRM();

    static class XaRM implements RowMapper<Transaction> {
        @Override
        public Transaction mapRow(ResultSet rs, int index) throws SQLException {
            int i = 0;
            int id = rs.getInt(++i);
            int accountId = rs.getInt(++i);
            Date date = rs.getDate(++i);
            TransactionType type = TransactionType.valueOf(rs.getString(++i));
            String symbol = rs.getString(++i);
            String symbol2 = rs.getString(++i);
            Double shares = getDouble(rs, ++i);
            Double price = getDouble(rs, ++i);
            Double fx = getDouble(rs, ++i);
            Double fee = getDouble(rs, ++i);
            Double book = getDouble(rs, ++i);

            return Transaction.createTransaction(id, accountId, date, type, symbol, symbol2, shares, price, fx, fee,
                    book);
        }
    }

    public void save(Transaction xa) {
        if (xa.getId() == -1)
            ejt.update("INSERT INTO Transactions (accountId, xaDate, xaType, symbol, symbol2, shares, price, " //
                    + "exchange, fee, book) VALUES (?,?,?,?,?,?,?,?,?,?)", xa.getAccountId(), xa.getTransactionDate(),
                    xa.getTransactionType().name(), xa.getSymbol(), xa.getSymbol2(), xa.getShares(), xa.getPrice(),
                    xa.getForeignExchange(), xa.getFee(), xa.getBookValue());
        else
            ejt.update("UPDATE Transactions SET accountId=?, xaDate=?, xaType=?, symbol=?, symbol2=?, shares=?, " //
                    + "price=?, exchange=?, fee=?, book=? WHERE id=?", xa.getAccountId(), xa.getTransactionDate(), xa
                    .getTransactionType().name(), xa.getSymbol(), xa.getSymbol2(), xa.getShares(), xa.getPrice(), xa
                    .getForeignExchange(), xa.getFee(), xa.getBookValue(), xa.getId());
    }

    public void delete(int id) {
        ejt.update("DELETE FROM Transactions WHERE id=?", id);
    }
}
