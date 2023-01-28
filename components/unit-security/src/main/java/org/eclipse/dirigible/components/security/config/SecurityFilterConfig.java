/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.security.config;

import java.util.Arrays;

import org.eclipse.dirigible.components.security.filter.SecurityFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;

/**
 * The Class SecurityFilterConfig.
 */
@Configuration
public class SecurityFilterConfig {

    /**
     * Security filter registration bean.
     *
     * @param securityFilter the security filter
     * @return the filter registration bean
     */
    @Bean
    public FilterRegistrationBean<SecurityFilter> securityFilterRegistrationBean(SecurityFilter securityFilter) {
        FilterRegistrationBean<SecurityFilter> filterRegistrationBean = new FilterRegistrationBean<>(securityFilter);

        filterRegistrationBean.setFilter(securityFilter);
        filterRegistrationBean.addUrlPatterns(
                "/services/js/*",
                "/services/public/*",
                "/services/web/*",
                "/services/wiki/*",
                "/services/command/*",

                "/public/v8/js/*",
                "/public/v8/public/*",
                "/public/v8/web/*",
                "/public/v8/wiki/*",
                "/public/v8/command/*",

                "/odata/v2/*"
        );

        return filterRegistrationBean;
    }
    
//    @Bean
//    public FilterRegistrationBean<HiddenHttpMethodFilter> hiddenFilterRegistrationBean() {
//        FilterRegistrationBean<HiddenHttpMethodFilter> filterRegistrationBean = new FilterRegistrationBean<>(new HiddenHttpMethodFilter());
//
//        filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));
//
//        return filterRegistrationBean;
//    }
}
