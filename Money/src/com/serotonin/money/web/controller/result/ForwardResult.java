package com.serotonin.money.web.controller.result;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Forward back into the controller servlet
 */
public class ForwardResult extends ControllerResult {
    private final String path;

    public ForwardResult(String path) {
        this.path = path;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(path + ".page").forward(request, response);
    }
}
