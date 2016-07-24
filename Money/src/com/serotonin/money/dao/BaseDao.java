package com.serotonin.money.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.serotonin.db.DaoUtils;
import com.serotonin.db.spring.ExtendedJdbcTemplate;
import com.serotonin.provider.Providers;
import com.serotonin.provider.impl.MySQLDataSourceProvider;

public class BaseDao extends DaoUtils {
    public static final String BEAN_NAME = "dataSource";

    public static AccountDao accountDao;
    public static AssetDao assetDao;
    public static DividendDao dividendDao;
    public static TransactionDao transactionDao;

    private static DataSource dataSource;
    ExtendedJdbcTemplate ejt;

    public static void initialize() {
        dataSource = Providers.get(MySQLDataSourceProvider.class).getDataSource();

        accountDao = new AccountDao();
        assetDao = new AssetDao();
        dividendDao = new DividendDao();
        transactionDao = new TransactionDao();
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public BaseDao() {
        super(dataSource);
        ejt = super.ejt;
    }

    public static Double getDouble(ResultSet rs, int index) throws SQLException {
        Double d = rs.getDouble(index);
        if (rs.wasNull())
            return null;
        return d;
    }

    public static BigDecimal getBigDecimal(ResultSet rs, int index) throws SQLException {
        Double d = rs.getDouble(index);
        if (rs.wasNull())
            return null;
        return new BigDecimal(d);
    }
}
