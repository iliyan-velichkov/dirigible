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
package org.eclipse.dirigible.components.engine.bpm.flowable.service;

import org.eclipse.dirigible.components.base.artefact.BaseArtefactService;
import org.eclipse.dirigible.components.engine.bpm.flowable.domain.Bpmn;
import org.eclipse.dirigible.components.engine.bpm.flowable.repository.BpmnRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class BpmnService.
 */
@Service
@Transactional
public class BpmnService extends BaseArtefactService<Bpmn, Long> {

    public BpmnService(BpmnRepository repository) {
        super(repository);
    }

}
