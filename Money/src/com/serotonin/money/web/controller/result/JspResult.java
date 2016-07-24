package com.serotonin.money.web.controller.result;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Forward to a JSP
 */
public class JspResult extends ControllerResult {
    private final String page;

    public JspResult(String page) {
        this.page = page;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp" + page + ".jsp").forward(request, response);
    }
}
