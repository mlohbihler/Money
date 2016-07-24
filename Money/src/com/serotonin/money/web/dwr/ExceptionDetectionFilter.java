package com.serotonin.money.web.dwr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.AjaxFilter;
import org.directwebremoting.AjaxFilterChain;

/**
 * @author Matthew Lohbihler
 */
public class ExceptionDetectionFilter implements AjaxFilter {
    private static final Log LOG = LogFactory.getLog(ExceptionDetectionFilter.class);

    @Override
    public Object doFilter(Object obj, Method method, Object[] params, AjaxFilterChain chain) throws Exception {
        try {
            return chain.doFilter(obj, method, params);
        }
        catch (Exception e) {
            Throwable e2 = e;
            if (e2 instanceof InvocationTargetException)
                e2 = ((InvocationTargetException) e).getTargetException();
            LOG.error("DWR invocation exception", e2);

            throw e;
        }
    }
}
