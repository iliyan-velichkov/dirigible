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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * The Class User.
 */
@Entity
@Table(name = "DIRIGIBLE_USERS", uniqueConstraints = {@UniqueConstraint(columnNames = {"USER_TENANT_ID", "USER_USERNAME"})})
public class User {

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "USER_ID", nullable = false)
    private String id;

    /** The tenant. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_TENANT_ID")
    private Tenant tenant;

    /** The email. */
    @Column(name = "USER_USERNAME", nullable = false)
    private String username;

    /** The password. */
    @Column(name = "USER_PASSWORD", nullable = false)
    private String password;

    /**
     * Instantiates a new user.
     */
    public User() {}

    /**
     * Instantiates a new user.
     *
     * @param tenant the tenant
     * @param username the username
     * @param password the password
     */
    public User(Tenant tenant, String username, String password) {
        this.tenant = tenant;
        this.username = username;
        this.password = password;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the tenant.
     *
     * @return the tenant
     */
    public Tenant getTenant() {
        return tenant;
    }

    /**
     * Sets the tenant.
     *
     * @param tenant the new tenant
     */
    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username the new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", tenant=" + tenant + ", username=" + username + "]";
    }

}
