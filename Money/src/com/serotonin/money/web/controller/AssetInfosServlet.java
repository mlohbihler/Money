package com.serotonin.money.web.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.serotonin.money.dao.BaseDao;
import com.serotonin.money.web.controller.result.ControllerResult;
import com.serotonin.money.web.controller.result.RedirectResult;

public class AssetInfosServlet extends AbstractController {
    private static final long serialVersionUID = 1L;

    @Override
    public ControllerResult handle(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model)
            throws IOException, ServletException {
        String deleteSymbol = request.getParameter("delete");
        if (deleteSymbol != null) {
            BaseDao.assetDao.delete(deleteSymbol);
            return new RedirectResult("/assetInfos");
        }

        model.put("assets", BaseDao.assetDao.get());
        return null;
    }
}
