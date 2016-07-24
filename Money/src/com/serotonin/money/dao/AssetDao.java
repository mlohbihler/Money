package com.serotonin.money.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import com.serotonin.money.vo.Asset;
import com.serotonin.money.vo.AssetInfo;
import com.serotonin.money.vo.Country;
import com.serotonin.money.vo.tx.TransactionType;

public class AssetDao extends BaseDao {
    private static final String SELECT = "SELECT symbol, name, marketPrice, marketTime, marketSymbol, divAmount, "
            + "divDay, divMonth, divPerYear, divXaType, divCountry, divSymbolId, notes FROM Assets";

    public void populate(List<Asset> assets) {
        // Copy the assets to a lookup map
        final Map<String, Asset> map = new HashMap<String, Asset>();
        for (Asset asset : assets)
            map.put(asset.getSymbol(), asset);

        ejt.query(SELECT, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                AssetInfo ai = rm.mapRow(rs, 0);
                Asset asset = map.get(ai.getSymbol());
                if (asset != null)
                    asset.setAssetInfo(ai);
            }
        });
    }

    public List<AssetInfo> get() {
        return ejt.query(SELECT + " ORDER BY symbol", rm);
    }

    public AssetInfo get(String symbol) {
        return ejt.queryForObject(SELECT + " WHERE symbol=?", new Object[] { symbol }, rm, null);
    }

    public List<String> getSymbols() {
        return ejt.queryForList("SELECT symbol FROM Assets", String.class);
    }

    static AssetInfoRM rm = new AssetInfoRM();

    static class AssetInfoRM implements RowMapper<AssetInfo> {
        @Override
        public AssetInfo mapRow(ResultSet rs, int index) throws SQLException {
            int i = 0;
            AssetInfo a = new AssetInfo();
            a.setSymbol(rs.getString(++i));
            a.setName(rs.getString(++i));
            a.setMarketPrice(rs.getDouble(++i));
            a.setMarketTime(rs.getLong(++i));
            a.setMarketSymbol(rs.getString(++i));
            a.setDivAmount(rs.getDouble(++i));
            a.setDivDay(rs.getInt(++i));
            a.setDivMonth(rs.getInt(++i));
            a.setDivPerYear(rs.getInt(++i));

            try {
                a.setDivXaType(TransactionType.valueOf(rs.getString(++i)));
            }
            catch (IllegalArgumentException e) {
                a.setDivXaType(null);
            }

            a.setDivCountry(Country.valueOf(rs.getString(++i)));
            a.setDivSymbolId(rs.getInt(++i));
            a.setNotes(rs.getString(++i));
            return a;
        }
    }

    public void save(AssetInfo assetInfo) {
        String divXaType = assetInfo.getDivXaType() == null ? "" : assetInfo.getDivXaType().name();

        ejt.update(
                "INSERT INTO Assets " //
                        + "  (symbol, name, marketPrice, marketTime, marketSymbol, divAmount, divDay, divMonth, divPerYear, divXaType, " //
                        + "   divCountry, divSymbolId, notes) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)" //
                        + "ON DUPLICATE KEY UPDATE " //
                        + "  name=?, marketPrice=?, marketTime=?, marketSymbol=?, divAmount=?, divDay=?, divMonth=?, " //
                        + "  divPerYear=?, divXaType=?, divCountry=?, divSymbolId=?, notes=?", assetInfo.getSymbol(),
                assetInfo.getName(), assetInfo.getMarketPrice(), assetInfo.getMarketTime(),
                assetInfo.getMarketSymbol(), assetInfo.getDivAmount(), assetInfo.getDivDay(), assetInfo.getDivMonth(),
                assetInfo.getDivPerYear(), divXaType, assetInfo.getDivCountry().name(), assetInfo.getDivSymbolId(),
                assetInfo.getNotes(), assetInfo.getName(), assetInfo.getMarketPrice(), assetInfo.getMarketTime(),
                assetInfo.getMarketSymbol(), assetInfo.getDivAmount(), assetInfo.getDivDay(), assetInfo.getDivMonth(),
                assetInfo.getDivPerYear(), divXaType, assetInfo.getDivCountry().name(), assetInfo.getDivSymbolId(),
                assetInfo.getNotes());
    }

    public void delete(String symbol) {
        ejt.update("DELETE FROM Assets WHERE symbol=?", symbol);
    }
}
