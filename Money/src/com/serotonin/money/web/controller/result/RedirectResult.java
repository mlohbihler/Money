package com.serotonin.money.web.controller.result;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Send a redirect back to the browser.
 */
public class RedirectResult extends ControllerResult {
    private final String path;

    public RedirectResult(String path) {
        this.path = path;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + path);
    }
}
