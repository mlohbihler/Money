package com.serotonin.money.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.serotonin.money.vo.Account;

public class AccountDao extends BaseDao {
    private static final String SELECT = "SELECT id, name, notes, colour FROM Accounts ";

    public List<Account> get() {
        return ejt.query(SELECT + "ORDER BY name", accountRM);
    }

    public Account get(int id) {
        return ejt.queryForObject(SELECT + "WHERE id=?", new Object[] { id }, accountRM, null);
    }

    public Account get(String name) {
        return ejt.queryForObject(SELECT + "WHERE name=?", new Object[] { name }, accountRM, null);
    }

    static AccountRM accountRM = new AccountRM();

    static class AccountRM implements RowMapper<Account> {
        @Override
        public Account mapRow(ResultSet rs, int index) throws SQLException {
            int i = 0;
            Account account = new Account();
            account.setId(rs.getInt(++i));
            account.setName(rs.getString(++i));
            account.setNotes(rs.getString(++i));
            account.setColour(rs.getString(++i));
            return account;
        }
    }
}
