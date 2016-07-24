package com.serotonin.money.web.controller.result;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

abstract public class ControllerResult {
    abstract public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException;
}
