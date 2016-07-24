package com.serotonin.money.web.listener;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.money.dao.BaseDao;
import com.serotonin.provider.PropertiesProvider;
import com.serotonin.provider.Providers;
import com.serotonin.provider.impl.MySQLDataSourceProvider;
import com.serotonin.provider.impl.ReloadingPropertiesProvider;
import com.serotonin.util.properties.ReloadingProperties;

public class LifecycleListener implements ServletContextListener {
    private static final Log LOG = LogFactory.getLog(LifecycleListener.class);

    private MySQLDataSourceProvider mysql;

    @Override
    public void contextInitialized(ServletContextEvent ctx) {
        try {
            //        WebApplicationContext wactx = WebApplicationContextUtils.getRequiredWebApplicationContext(ctx
            //                .getServletContext());

            String realPath = ctx.getServletContext().getRealPath("");

            File file = new File(realPath, "WEB-INF/env.properties");
            ReloadingProperties props = new ReloadingProperties(file);
            Providers.add(PropertiesProvider.class, new ReloadingPropertiesProvider(props));

            mysql = new MySQLDataSourceProvider();
            mysql.initialize();
            Providers.add(MySQLDataSourceProvider.class, mysql);

            BaseDao.initialize();

            LOG.info("Money initialized");
        }
        catch (RuntimeException e) {
            LOG.error("", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent ctx) {
        mysql.terminate();

        LOG.info("Money terminated");
    }
}
