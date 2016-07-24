package com.serotonin.money.web.controller.result;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NullResult extends ControllerResult {
    public static final NullResult INSTANCE = new NullResult();

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Don't do anything.
    }
}
