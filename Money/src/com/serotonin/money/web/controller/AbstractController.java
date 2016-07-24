package com.serotonin.money.web.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.serotonin.money.util.Utils;
import com.serotonin.money.web.controller.result.ControllerResult;
import com.serotonin.money.web.controller.result.JspResult;

abstract public class AbstractController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    abstract public ControllerResult handle(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> model) throws IOException, ServletException;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        ControllerResult result = null;

        Map<String, Object> model = new HashMap<String, Object>();
        result = handle(request, response, model);
        for (Map.Entry<String, Object> entry : model.entrySet())
            request.setAttribute(entry.getKey(), entry.getValue());

        if (result == null) {
            // If no result was provided, default to JSP.
            String path = request.getRequestURI();
            String ctxPath = request.getContextPath();
            if (ctxPath != null)
                path = path.substring(ctxPath.length());
            result = new JspResult(path);
        }

        result.execute(request, response);
    }

    protected boolean isPost(HttpServletRequest request) {
        return request.getMethod().equals("POST");
    }

    protected boolean isGet(HttpServletRequest request) {
        return request.getMethod().equals("GET");
    }

    protected String getAndPutParameter(HttpServletRequest request, String key, Map<String, Object> model) {
        String s = Utils.cleanParameter(request.getParameter(key));
        if (!StringUtils.isEmpty(s))
            model.put(key, s);
        return s;
    }

    protected boolean getAndPutBooleanParameter(HttpServletRequest request, String key, Map<String, Object> model) {
        String s = request.getParameter(key);
        s = Utils.cleanParameter(s);
        boolean b = Boolean.parseBoolean(s);
        model.put(key, b);
        return b;
    }

    protected int getAndPutIntParameter(HttpServletRequest request, String key, int defaultValue,
            Map<String, Object> model) {
        int i = getIntParameter(request, key, defaultValue);
        model.put(key, i);
        return i;
    }

    protected int getIntParameter(HttpServletRequest request, String key, int defaultValue) {
        String str = Utils.cleanParameter(request.getParameter(key));
        try {
            if (str != null)
                return Integer.parseInt(str);
        }
        catch (NumberFormatException e) {
            // no op
        }
        return defaultValue;
    }

    protected double getAndPutDoubleParameter(HttpServletRequest request, String key, double defaultValue,
            Map<String, Object> model) {
        double d = getDoubleParameter(request, key, defaultValue);
        model.put(key, d);
        return d;
    }

    protected double getDoubleParameter(HttpServletRequest request, String key, double defaultValue) {
        String str = Utils.cleanParameter(request.getParameter(key));
        try {
            if (str != null)
                return Double.parseDouble(str);
        }
        catch (NumberFormatException e) {
            // no op
        }
        return defaultValue;
    }

    protected void addParameterToModel(HttpServletRequest request, String key, Map<String, Object> model) {
        addParameterToModel(request, key, model, null);
    }

    protected void addParameterToModel(HttpServletRequest request, String key, Map<String, Object> model,
            Object defaultValue) {
        if (request.getParameter(key) != null)
            model.put(key, request.getParameter(key));
        else if (defaultValue != null)
            model.put(key, defaultValue);
    }
}
