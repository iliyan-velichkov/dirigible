/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.data.source.snowpark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import org.eclipse.dirigible.components.data.sources.domain.DataSource;
import org.eclipse.dirigible.components.data.sources.manager.DataSourceInitializerContributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceInitializerContributorSnowpark implements DataSourceInitializerContributor {
    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(DataSourceInitializerContributorSnowpark.class);

    @Override
    public void contribute(DataSource dataSource, Properties properties) {
        if (!dataSource.getName()
                       .startsWith("SNOWFLAKE")) {
            return;
        }
        Map<String, String> env = System.getenv();
        try {
            String url;
            if (Files.exists(Paths.get("/snowflake/session/token"))) {
                properties.put("dataSource.CLIENT_SESSION_KEEP_ALIVE", true);
                properties.put("dataSource.account", env.get("SNOWFLAKE_ACCOUNT"));
                properties.put("dataSource.authenticator", "OAUTH");
                properties.put("dataSource.token", new String(Files.readAllBytes(Paths.get("/snowflake/session/token"))));
                properties.put("dataSource.warehouse", env.getOrDefault("SNOWFLAKE_WAREHOUSE", ""));
                properties.put("dataSource.db", env.get("SNOWFLAKE_DATABASE"));
                properties.put("dataSource.schema", env.get("SNOWFLAKE_SCHEMA"));
                properties.put("dataSource.insecureMode", true);
                url = "jdbc:snowflake://" + env.get("SNOWFLAKE_HOST") + ":" + env.get("SNOWFLAKE_PORT");
                properties.put("jdbcUrl", url);
                properties.put("dataSource.url", url);
            } else {
                properties.put("dataSource.CLIENT_SESSION_KEEP_ALIVE", true);
                properties.put("dataSource.account", env.getOrDefault("SNOWFLAKE_ACCOUNT", ""));
                properties.put("dataSource.user", env.getOrDefault("SNOWFLAKE_USERNAME", dataSource.getUsername()));
                properties.put("dataSource.password", env.getOrDefault("SNOWFLAKE_PASSWORD", dataSource.getPassword()));
                properties.put("dataSource.warehouse", env.getOrDefault("SNOWFLAKE_WAREHOUSE", ""));
                properties.put("dataSource.db", env.getOrDefault("SNOWFLAKE_DATABASE", ""));
                properties.put("dataSource.schema", env.getOrDefault("SNOWFLAKE_SCHEMA", ""));
                url = env.getOrDefault("SNOWFLAKE_URL", dataSource.getUrl());
                properties.put("jdbcUrl", url);
                properties.put("dataSource.url", url);
            }
        } catch (IOException ex) {
            logger.error("Invalid configuration for the datasource: [{}]", dataSource.getName(), ex);
        }
    }
}
