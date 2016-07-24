package com.serotonin.money.dao;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.serotonin.money.vo.Dividend;
import com.serotonin.money.vo.tx.TransactionType;

public class DividendDao extends BaseDao {
    private static final String SELECT = "SELECT d.id, d.accountId, a.name, d.xaDate, d.exDivDate, " //
            + "  d.symbol, i.name, d.xaType, d.shares, d.divAmount, d.amount " //
            + "FROM DividendProjections d " //
            + "JOIN Accounts a ON d.accountId=a.id " //
            + "LEFT JOIN Assets i ON d.symbol=i.symbol";

    public List<Dividend> get() {
        return ejt.query(SELECT + " ORDER BY d.xaDate, d.accountId, d.symbol", divRM);
    }

    //    public List<Transaction> get(int accountId) {
    //        return ejt.query(SELECT + " WHERE accountId=? ORDER BY xaDate", new Object[] { accountId }, xaRM);
    //    }
    //
    //    public List<Transaction> get(int accountId, String symbol) {
    //        if (symbol == null)
    //            return ejt.query(SELECT + " WHERE accountId=? AND symbol IS NULL AND symbol2 IS NULL ORDER BY xaDate",
    //                    new Object[] { accountId }, xaRM);
    //
    //        return ejt.query(SELECT + " WHERE accountId=? AND (symbol=? OR symbol2=?) ORDER BY xaDate", new Object[] {
    //                accountId, symbol, symbol }, xaRM);
    //    }

    public Dividend get(int id) {
        return ejt.queryForObject(SELECT + " WHERE d.id=?", new Object[] { id }, divRM, null);
    }

    public Date getLatestDividendDate(int accountId, String symbol) {
        return ejt.queryForObject("SELECT MAX(xaDate) FROM DividendProjections WHERE accountId=? AND symbol=?",
                new Object[] { accountId, symbol }, Date.class, null);
    }

    static DivRM divRM = new DivRM();

    static class DivRM implements RowMapper<Dividend> {
        @Override
        public Dividend mapRow(ResultSet rs, int index) throws SQLException {
            int i = 0;
            Dividend d = new Dividend();
            d.setId(rs.getInt(++i));
            d.setAccountId(rs.getInt(++i));
            d.setAccountName(rs.getString(++i));
            d.setXaDate(rs.getDate(++i));
            d.setExDivDate(rs.getDate(++i));
            d.setSymbol(rs.getString(++i));
            d.setAssetName(rs.getString(++i));

            String type = rs.getString(++i);
            if (type != null)
                d.setXaType(TransactionType.valueOf(type));

            d.setShares(rs.getDouble(++i));
            d.setDivAmount(rs.getDouble(++i));
            d.setAmount(rs.getDouble(++i));
            return d;
        }
    }

    public void save(Dividend d) {
        String type = d.getXaType() == null ? null : d.getXaType().name();

        if (d.getId() == -1)
            ejt.update("INSERT INTO DividendProjections (accountId, xaDate, exDivDate, symbol, xaType, shares, " //
                    + "divAmount, amount) VALUES (?,?,?,?,?,?,?,?)", d.getAccountId(), d.getXaDate(), d.getExDivDate(),
                    d.getSymbol(), type, d.getShares(), d.getDivAmount(), d.getAmount());
        else
            ejt.update("UPDATE DividendProjections SET accountId=?, xaDate=?, exDivDate=?, symbol=?, xaType=?, " //
                    + "shares=?, divAmount=?, amount=? WHERE id=?", d.getAccountId(), d.getXaDate(), d.getExDivDate(),
                    d.getSymbol(), type, d.getShares(), d.getDivAmount(), d.getAmount(), d.getId());
    }

    public void delete(int id) {
        ejt.update("DELETE FROM DividendProjections WHERE id=?", id);
    }
}
