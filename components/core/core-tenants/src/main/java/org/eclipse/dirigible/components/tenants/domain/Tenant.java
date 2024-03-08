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
package org.eclipse.dirigible.components.tenants.domain;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A tenant owns/maintains a sub-section of the web application. Can be thought of as a website
 * within the application. Users can register for multiple tenant without them knowing that each
 * separate tenant is part of one and the same application. So the uniqueness of user accounts is
 * determined by the combination of the user's unique ID (= email) combined with the tenant ID.
 */
@Entity
@Table(name = "DIRIGIBLE_TENANTS")
@ConditionalOnProperty(name = "tenants.enabled", havingValue = "true")
public class Tenant {

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "TENANT_ID", nullable = false)
    private String id;

    /** The slug. */
    @Column(name = "TENANT_SUBDOMAIN", unique = true, nullable = false)
    private String subdomain;

    /** The name. */
    @Column(name = "TENANT_NAME", nullable = false)
    private String name;

    /**
     * Instantiates a new tenant.
     */
    public Tenant() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
}
