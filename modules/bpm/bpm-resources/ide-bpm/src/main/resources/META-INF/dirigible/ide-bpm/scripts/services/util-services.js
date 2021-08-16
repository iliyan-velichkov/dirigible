/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Service with small utility methods
 */
angular.module('flowableModeler').service('UtilityService', [ '$window', '$document', '$timeout', function ($window, $document, $timeout) {

    this.scrollToElement = function(elementId) {
        $timeout(function() {
            var someElement = angular.element(document.getElementById(elementId))[0];
            if (someElement) {
                if (someElement.getBoundingClientRect().top > $window.innerHeight) {
                    $document.scrollToElement(someElement, 0, 1000);
                }
            }
        });
    };

}]);