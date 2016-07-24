package com.serotonin.money.web.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.serotonin.money.util.Utils;
import com.serotonin.money.vo.tx.TransactionException;
import com.serotonin.money.web.controller.result.ControllerResult;

public class AccountsServlet extends AbstractController {
    private static final long serialVersionUID = 1L;

    @Override
    public ControllerResult handle(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model)
            throws IOException, ServletException {
        try {
            model.put("accounts", Utils.accountsWithTransactions());
        }
        catch (TransactionException e) {
            throw new ServletException(e);
        }
        return null;
    }
}
