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
package org.eclipse.dirigible.components.data.store.service;

import org.eclipse.dirigible.components.base.artefact.BaseArtefactService;
import org.eclipse.dirigible.components.data.store.domain.Entity;
import org.eclipse.dirigible.components.data.store.repository.EntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EntityService extends BaseArtefactService<Entity, Long> {

    public EntityService(EntityRepository repository) {
        super(repository);
    }

}
