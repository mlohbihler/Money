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
    protected void service(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        ControllerResult result = null;

        final Map<String, Object> model = new HashMap<>();
        result = handle(request, response, model);
        for (final Map.Entry<String, Object> entry : model.entrySet())
            request.setAttribute(entry.getKey(), entry.getValue());

        if (result == null) {
            // If no result was provided, default to JSP.
            String path = request.getRequestURI();
            final String ctxPath = request.getContextPath();
            if (ctxPath != null)
                path = path.substring(ctxPath.length());
            result = new JspResult(path);
        }

        result.execute(request, response);
    }

    protected boolean isPost(final HttpServletRequest request) {
        return request.getMethod().equals("POST");
    }

    protected boolean isGet(final HttpServletRequest request) {
        return request.getMethod().equals("GET");
    }

    protected String getAndPutParameter(final HttpServletRequest request, final String key,
            final Map<String, Object> model) {
        final String s = Utils.cleanParameter(request.getParameter(key));
        if (!StringUtils.isEmpty(s))
            model.put(key, s);
        return s;
    }

    protected boolean getAndPutBooleanParameter(final HttpServletRequest request, final String key,
            final Map<String, Object> model) {
        String s = request.getParameter(key);
        s = Utils.cleanParameter(s);
        final boolean b = Boolean.parseBoolean(s);
        model.put(key, b);
        return b;
    }

    protected int getAndPutIntParameter(final HttpServletRequest request, final String key, final int defaultValue,
            final Map<String, Object> model) {
        final int i = getIntParameter(request, key, defaultValue);
        model.put(key, i);
        return i;
    }

    protected int getIntParameter(final HttpServletRequest request, final String key, final int defaultValue) {
        final String str = Utils.cleanParameter(request.getParameter(key));
        try {
            if (str != null)
                return Integer.parseInt(str);
        } catch (final NumberFormatException e) {
            // no op
        }
        return defaultValue;
    }

    protected double getAndPutDoubleParameter(final HttpServletRequest request, final String key,
            final double defaultValue, final Map<String, Object> model) {
        final double d = getDoubleParameter(request, key, defaultValue);
        model.put(key, d);
        return d;
    }

    protected double getDoubleParameter(final HttpServletRequest request, final String key, final double defaultValue) {
        final String str = Utils.cleanParameter(request.getParameter(key));
        try {
            if (str != null)
                return Double.parseDouble(str);
        } catch (final NumberFormatException e) {
            // no op
        }
        return defaultValue;
    }

    protected void addParameterToModel(final HttpServletRequest request, final String key,
            final Map<String, Object> model) {
        addParameterToModel(request, key, model, null);
    }

    protected void addParameterToModel(final HttpServletRequest request, final String key,
            final Map<String, Object> model, final Object defaultValue) {
        if (request.getParameter(key) != null)
            model.put(key, request.getParameter(key));
        else if (defaultValue != null)
            model.put(key, defaultValue);
    }
}
