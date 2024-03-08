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
package org.eclipse.dirigible.components.extensions.service;

import java.util.List;
import org.eclipse.dirigible.components.base.artefact.BaseArtefactService;
import org.eclipse.dirigible.components.extensions.domain.Extension;
import org.eclipse.dirigible.components.extensions.repository.ExtensionRepository;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Processing the Extensions Service incoming requests.
 */
@Service
@Transactional
public class ExtensionService extends BaseArtefactService<Extension, Long> {

    public ExtensionService(ExtensionRepository repository) {
        super(repository);
    }

    /**
     * Find by extension point.
     *
     * @param extensionPoint the extension point
     * @return the extension
     */
    @Transactional(readOnly = true)
    public List<Extension> findByExtensionPoint(String extensionPoint) {
        Extension filter = new Extension();
        filter.setExtensionPoint(extensionPoint);
        Example<Extension> example = Example.of(filter);
        return getRepo().findAll(example);
    }

}
