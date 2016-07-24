package com.serotonin.money.web.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.money.dao.BaseDao;
import com.serotonin.money.util.Market;
import com.serotonin.money.vo.AssetInfo;
import com.serotonin.money.vo.Country;
import com.serotonin.money.vo.tx.TransactionType;
import com.serotonin.money.web.controller.result.ControllerResult;
import com.serotonin.money.web.controller.result.JspResult;
import com.serotonin.money.web.controller.result.RedirectResult;

public class AssetInfoServlet extends AbstractController {
    private static final long serialVersionUID = 1L;
    private static final Log LOG = LogFactory.getLog(AssetInfoServlet.class);

    @Override
    public ControllerResult handle(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model)
            throws IOException, ServletException {
        String path = request.getRequestURI();
        String symbol = path.substring(path.lastIndexOf('/') + 1);
        AssetInfo assetInfo = BaseDao.assetDao.get(symbol);

        if (assetInfo == null) {
            assetInfo = new AssetInfo();
            assetInfo.setDivDay(1);
            assetInfo.setDivMonth(1);
            assetInfo.setDivPerYear(12);
            assetInfo.setDivCountry(Country.Canada);
        }

        if (isGet(request)) {
            model.put("symbol", assetInfo.getSymbol());
            model.put("name", assetInfo.getName());
            model.put("marketSymbol", assetInfo.getMarketSymbol());
            model.put("divAmount", assetInfo.getDivAmount());
            model.put("divDay", assetInfo.getDivDay());
            model.put("divMonth", assetInfo.getDivMonth());
            model.put("divPerYear", assetInfo.getDivPerYear());
            model.put("divXaType", assetInfo.getDivXaType());
            model.put("divCountry", assetInfo.getDivCountry().name());
            model.put("divSymbolId", assetInfo.getDivSymbolId());
            model.put("notes", assetInfo.getNotes());
        }
        else if (isPost(request)) {
            if (assetInfo.getSymbol() == null) {
                assetInfo.setSymbol(getAndPutParameter(request, "symbol", model));
                try {
                    Market.populateMarketValue(assetInfo);
                }
                catch (Exception e) {
                    // This doesn't do anything because of the redirect.
                    // model.put("priceError", e.getMessage());
                    LOG.error("Market price error", e);
                }
            }

            String name = getAndPutParameter(request, "name", model);
            String marketSymbol = getAndPutParameter(request, "marketSymbol", model);
            double divAmount = getAndPutDoubleParameter(request, "divAmount", 0, model);
            int divDay = getAndPutIntParameter(request, "divDay", 1, model);
            int divMonth = getAndPutIntParameter(request, "divMonth", 1, model);
            int divPerYear = getAndPutIntParameter(request, "divPerYear", 12, model);
            String divXaType = getAndPutParameter(request, "divXaType", model);
            Country divCountry = Country.valueOf(getAndPutParameter(request, "divCountry", model));
            int divSymbolId = getAndPutIntParameter(request, "divSymbolId", 0, model);
            String notes = getAndPutParameter(request, "notes", model);

            // Validation?

            assetInfo.setName(name);
            assetInfo.setMarketSymbol(marketSymbol);
            assetInfo.setDivAmount(divAmount);
            assetInfo.setDivDay(divDay);
            assetInfo.setDivMonth(divMonth);
            assetInfo.setDivPerYear(divPerYear);
            assetInfo.setDivXaType(TransactionType.valueOf(divXaType));
            assetInfo.setDivCountry(divCountry);
            assetInfo.setDivSymbolId(divSymbolId);
            assetInfo.setNotes(notes);
            BaseDao.assetDao.save(assetInfo);

            return new RedirectResult("/assetInfos");
        }

        return new JspResult("/assetInfo");
    }
}
