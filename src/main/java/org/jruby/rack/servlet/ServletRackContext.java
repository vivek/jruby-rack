/*
 * Copyright 2007-2009 Sun Microsystems, Inc.
 * This source code is available under the MIT license.
 * See the file LICENSE.txt for details.
 */

package org.jruby.rack.servlet;

import org.jruby.rack.input.RackRewindableInput;
import org.jruby.rack.*;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import java.util.Map;
import java.util.EventListener;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.FilterRegistration;
import javax.servlet.Filter;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.jruby.util.SafePropertyAccessor;
import static java.lang.System.out;

/**
 *
 * @author nicksieger
 */
public class ServletRackContext implements RackContext, ServletContext {
    private ServletContext context;
    private RackLogger logger;

    private class ServletContextLogger implements RackLogger {
        public void log(String message) {
            context.log(message);
        }

        public void log(String message, Throwable ex) {
            context.log(message,ex);
        }
    }

    private static class StandardOutLogger implements RackLogger {
        public void log(String message) {
            out.println(message);
            out.flush();
        }

        public void log(String message, Throwable ex) {
            out.println(message);
            ex.printStackTrace(out);
            out.flush();
        }
    }

    public ServletRackContext(ServletContext context) {
        this.context = context;
        if (SafePropertyAccessor.getProperty("jruby.rack.logging", "servlet_context").equals("servlet_context")) {
            this.logger = new ServletContextLogger();
        } else {
            this.logger = new StandardOutLogger();
        }
        RackRewindableInput.setDefaultThreshold(
                SafePropertyAccessor.getInt("jruby.rack.request.size.threshold.bytes",
                RackRewindableInput.getDefaultThreshold()));
    }

    public String getInitParameter(String key) {
        return context.getInitParameter(key);
    }

    public void log(String message) {
        logger.log(message);

    }

    public void log(String message, Throwable ex) {
        logger.log(message, ex);
    }

    public String getRealPath(String path) {
        String realPath = context.getRealPath(path);
        if (realPath == null) { // some servers don't like getRealPath, e.g. w/o exploded war
            URL u = null;
            try {
                u = context.getResource(path);
            } catch (MalformedURLException ex) {}
            if (u != null) {
                realPath = u.getPath();
            }
        }
        return realPath;
    }

    public RackApplicationFactory getRackFactory() {
        return (RackApplicationFactory) context.getAttribute(RackServletContextListener.FACTORY_KEY);
    }

    public String getContextPath() {
        return context.getContextPath();
    }

    public ServletContext getContext(String path) {
        return context.getContext(path);
    }

    public int getMajorVersion() {
        return context.getMajorVersion();
    }

    public int getMinorVersion() {
        return context.getMinorVersion();
    }

    public int getEffectiveMajorVersion() {
        return context.getEffectiveMajorVersion();
    }

    public int getEffectiveMinorVersion() {
        return context.getEffectiveMinorVersion();
    }

    public String getMimeType(String file) {
        return context.getMimeType(file);
    }

    public Set getResourcePaths(String path) {
        return context.getResourcePaths(path);
    }

    public URL getResource(String path) throws MalformedURLException {
        return context.getResource(path);
    }

    public InputStream getResourceAsStream(String path) {
        return context.getResourceAsStream(path);
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return context.getRequestDispatcher(path);
    }

    public RequestDispatcher getNamedDispatcher(String name) {
        return context.getNamedDispatcher(name);
    }

    @Deprecated
    public Servlet getServlet(String name) throws ServletException {
        return context.getServlet(name);
    }

    @Deprecated
    public Enumeration getServlets() {
        return context.getServlets();
    }

    @Deprecated
    public Enumeration getServletNames() {
        return context.getServletNames();
    }

    @Deprecated
    public void log(Exception ex, String msg) {
        context.log(ex, msg);
    }

    public String getServerInfo() {
        return context.getServerInfo();
    }

    public Enumeration getInitParameterNames() {
        return context.getInitParameterNames();
    }

    public boolean setInitParameter(String name, String value) {
        return context.setInitParameter(name, value);
    }

    public Object getAttribute(String key) {
        return context.getAttribute(key);
    }

    public Enumeration getAttributeNames() {
        return context.getAttributeNames();
    }

    public void setAttribute(String key, Object val) {
        context.setAttribute(key, val);
    }

    public void removeAttribute(String key) {
        context.removeAttribute(key);
    }

    public String getServletContextName() {
        return context.getServletContextName();
    }

    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        return context.addServlet(servletName, className);
    }

    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        return context.addServlet(servletName, servlet);
    }

    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        return context.addServlet(servletName, servletClass);
    }

    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
        return context.createServlet(clazz);
    }

    public ServletRegistration getServletRegistration(String servletName) {
        return context.getServletRegistration(servletName);
    }

    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return context.getServletRegistrations();
    }

    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        return context.addFilter(filterName, className);
    }

    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        return context.addFilter(filterName, filter);
    }

    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        return context.addFilter(filterName, filterClass);
    }

    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
        return context.createFilter(clazz);
    }

    public FilterRegistration getFilterRegistration(String filterName) {
        return context.getFilterRegistration(filterName);
    }

    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return context.getFilterRegistrations();
    }

    public SessionCookieConfig getSessionCookieConfig() {
        return context.getSessionCookieConfig();
    }

    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return context.getDefaultSessionTrackingModes();
    }

    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return context.getEffectiveSessionTrackingModes();
    }

    public void addListener(String className) {
        context.addListener(className);
    }

    public <T extends EventListener> void addListener(T t) {
        context.addListener(t);
    }

    public void addListener(Class<? extends EventListener> listenerClass) {
        context.addListener(listenerClass);
    }

    public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
        return context.createListener(clazz);
    }

    public JspConfigDescriptor getJspConfigDescriptor() {
        return context.getJspConfigDescriptor();
    }

    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
        context.setSessionTrackingModes(sessionTrackingModes);
    }

    public ClassLoader getClassLoader() {
        return context.getClassLoader();
    }
}
