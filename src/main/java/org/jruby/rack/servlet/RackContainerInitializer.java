/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.jruby.rack.servlet;

import org.jruby.rack.RackFilter;
import org.jruby.rack.rails.RailsServletContextListener;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;

/**
 * @author Vivek Pandey
 */
public class RackContainerInitializer implements ServletContainerInitializer{
    private final static String ENV_NAME="environment";
    private final static String PUBLIC_ROOT_NAME="public.root";
    private final static String JRUBY_MAX_RUNTIMES_NAME="jruby.max.runtimes";
    private final static String RACK_FILTER_URL_PATTERN_NAME="rack.filter.urlpattern";

    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {

        String env = "development";
        String publicRoot = "/";
        String jrubyMaxRuntimes = "1";
        String urlPattern = "/*";

        InputStream is = ctx.getResourceAsStream("/WEB-INF/config/warble.yml");

        if(is == null){
            ctx.log("No warble.yml, will assume defaults. Loading with defauls:\n");
            ctx.log(ENV_NAME+ env+"\n");
            ctx.log(PUBLIC_ROOT_NAME+": "+ publicRoot+"\n");
            ctx.log(JRUBY_MAX_RUNTIMES_NAME+": "+ jrubyMaxRuntimes+"\n");
            ctx.log(RACK_FILTER_URL_PATTERN_NAME+": "+ urlPattern+"\n");
        }else{
            Yaml yml = new Yaml();
            Map config = (Map) yml.load(new InputStreamReader(is));
            if(config.get(ENV_NAME) != null){
                env = (String) config.get(ENV_NAME);
            }

            if(config.get(PUBLIC_ROOT_NAME) != null){
                publicRoot = (String) config.get(PUBLIC_ROOT_NAME);
            }

            if(config.get(JRUBY_MAX_RUNTIMES_NAME) != null){
                jrubyMaxRuntimes = (String) config.get(JRUBY_MAX_RUNTIMES_NAME);
            }

            if(config.get(RACK_FILTER_URL_PATTERN_NAME) != null){
                urlPattern = (String) config.get(RACK_FILTER_URL_PATTERN_NAME);
            }
        }
        ctx.setInitParameter(ENV_NAME, env);
        ctx.setInitParameter(PUBLIC_ROOT_NAME, publicRoot);
        ctx.setInitParameter(JRUBY_MAX_RUNTIMES_NAME, jrubyMaxRuntimes);

        //Create and configure the RackFilter
        RackFilter filter = ctx.createFilter(RackFilter.class);
        FilterRegistration.Dynamic filterConfig = ctx.addFilter("RackFilter", filter);
        filterConfig.addMappingForUrlPatterns(null, true, urlPattern);

        //Create and add RackListener
        RailsServletContextListener listener = ctx.createListener(RailsServletContextListener.class);
        ctx.addListener(listener);
    }
}
