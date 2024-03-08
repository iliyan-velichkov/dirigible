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
package org.eclipse.dirigible.components.openapi.service;

import org.eclipse.dirigible.components.base.artefact.BaseArtefactService;
import org.eclipse.dirigible.components.openapi.domain.OpenAPI;
import org.eclipse.dirigible.components.openapi.repository.OpenAPIRepository;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class OpenAPIService.
 */

@Service
@Transactional
public class OpenAPIService extends BaseArtefactService<OpenAPI, Long> {

    private final IRepository iRepo;

    public OpenAPIService(OpenAPIRepository openAPIRepository, IRepository iRepo) {
        super(openAPIRepository);
        this.iRepo = iRepo;
    }

    /**
     * Gets the repository.
     *
     * @return the repository
     */
    protected IRepository getRepository() {
        return iRepo;
    }

    /**
     * Gets the resource.
     *
     * @param path the path
     * @return the resource
     */
    public IResource getResource(String path) {
        return getRepository().getResource(path);
    }

}
