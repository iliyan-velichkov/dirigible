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
package org.eclipse.dirigible.engine.odata2.sql.test.util;

import static org.apache.olingo.odata2.api.commons.ODataHttpMethod.GET;
import static org.apache.olingo.odata2.api.commons.ODataHttpMethod.POST;
import static org.apache.olingo.odata2.api.commons.ODataHttpMethod.PUT;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.apache.cxf.jaxrs.impl.PathSegmentImpl;
import org.apache.cxf.jaxrs.utils.HttpUtils;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.commons.ODataHttpMethod;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.core.rest.ODataSubLocator;
import org.apache.olingo.odata2.core.rest.SubLocatorParameter;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.easymock.IAnswer;

/**
 * Base class for OData API tests, which can be used to simulate calls to the OData API without having to use a servlet.
 * 
 */
public abstract class MockRequestBuilder {

    private static final String PROTOCOL = "http";
    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private static final String RELATIVE_SERVICE_ROOT = "/api/v1";

    //
    private final List<String> pathSegmentStringList = new ArrayList<>();
    private final MultivaluedMap<String, String> queryParams = new MetadataMap<>();
    private String accept = "*/*";
    private int contentSize = 1024 * 4;

    public MockRequestBuilder segments(final String... pathSegmentStrings) {
        pathSegmentStringList.addAll(Arrays.asList(pathSegmentStrings));
        return this;
    }

    public MockRequestBuilder param(final String name, final String value) {
        queryParams.add(name, value);
        return this;
    }

    public MockRequestBuilder accept(final String accept) {
        this.accept = accept;
        return this;
    }

    public MockRequestBuilder contentSize(final int contentSize) {
        this.contentSize = contentSize;
        return this;
    }

    public Response executeRequest() throws IOException, ODataException {
        return executeRequest(GET);
    }

    public Response executeRequest(final ODataHttpMethod method) throws IOException, ODataException {

        final EasyMockSupport easyMockSupport = new EasyMockSupport();

        final SubLocatorParameter subLocatorParam = new SubLocatorParameter();
        subLocatorParam.setServiceFactory(getServiceFactoryClass());
        subLocatorParam.setPathSegments(createPathSegments(pathSegmentStringList));
        final HttpHeaders httpHeaders = easyMockSupport.createMock(HttpHeaders.class);
        expect(httpHeaders.getAcceptableLanguages()).andReturn(Collections.singletonList(Locale.US));
        final MultivaluedMap<String, String> headersMap = new MetadataMap<>();
        headersMap.add("Accept", accept);
        headersMap.addAll("accept-encoding", Arrays.asList("gzip", "deflate", "sdch"));
        headersMap.add("accept-language", "en-US,en;q=0.8");
        headersMap.add("Authorization", "Basic dG9tY2F0OnRvbWNhdA==");
        headersMap.add("cache-control", "no-cache");
        headersMap.add("connection", "keep-alive");
        headersMap.add("Content-Type", "application/json");
        headersMap.add("host", HOST + "" + PORT);
        headersMap.add("user-agent", "Mozilla/5.0");
        expect(httpHeaders.getRequestHeaders()).andReturn(headersMap);
        final Capture<String> nameCapture = new Capture<>();
        final IAnswer<List<String>> getRequestHeaderAnswer = new IAnswer<List<String>>() {
            @Override
            public List<String> answer() throws Throwable {
                return headersMap.get(nameCapture.getValue());
            }
        };
        //
        expect(httpHeaders.getRequestHeader(capture(nameCapture))).andAnswer(getRequestHeaderAnswer).anyTimes();
        subLocatorParam.setHttpHeaders(httpHeaders);

        ServletContext servletContext = easyMockSupport.createMock(ServletContext.class);
        enrichServletContextMock(servletContext);

        final HttpServletRequest servletRequest = easyMockSupport.createMock(HttpServletRequest.class);

        expect(servletRequest.getMethod()).andReturn(method.name());
        expect(servletRequest.getServerName()).andReturn(HOST);
        expect(servletRequest.getServerPort()).andReturn(PORT);
        expect(servletRequest.getScheme()).andReturn(PROTOCOL);
        expect(servletRequest.getRemoteUser()).andReturn("RemoteUser").anyTimes();

        expect(servletRequest.getHeader("x-forwarded-for")).andReturn("127.0.0.1").anyTimes();
        StringBuilder pathSegmentsString = new StringBuilder();
        for (String pathSegment : pathSegmentStringList) {
            pathSegmentsString.append('/').append(pathSegment);
        }
        expect(servletRequest.getRequestURL()).andReturn(new StringBuffer(absoluteServiceRoot() + pathSegmentsString));
        expect(servletRequest.getRequestURI()).andReturn(RELATIVE_SERVICE_ROOT + pathSegmentsString).anyTimes();
        expect(servletRequest.getQueryString()).andReturn(createQueryString(queryParams));
        getServletInputStream(method, easyMockSupport, servletRequest);
        expect(servletRequest.getServletContext()).andReturn(servletContext).anyTimes();
        enrichServletRequestMock(servletRequest);
        subLocatorParam.setServletRequest(servletRequest);

        final UriInfo uriInfo = easyMockSupport.createMock(UriInfo.class);
        expect(uriInfo.getBaseUri()).andReturn(URI.create(absoluteServiceRoot()));
        expect(uriInfo.getQueryParameters()).andReturn(queryParams);
        subLocatorParam.setUriInfo(uriInfo);

        easyMockSupport.replayAll();

        final ODataSubLocator subLocator = ODataSubLocator.create(subLocatorParam);
        Response response = null;

        switch (method) {
        case GET:
            response = subLocator.handleGet();
            break;
        case PUT:
            response = subLocator.handlePut();
            break;
        case POST:
            //Note: As of now this class does not cover x-http-method. If needed handlePost needs
            // to be invoked by the proper value for @HeaderParam("X-HTTP-Method")
            response = subLocator.handlePost(null);
            break;
        case DELETE:
            response = subLocator.handleDelete();
            break;
        case PATCH:
            response = subLocator.handlePatch();
            break;
        case MERGE:
            response = subLocator.handleMerge();
            break;
        }

        easyMockSupport.verifyAll();
        return response;
    }

    protected void getServletInputStream(final ODataHttpMethod method, final EasyMockSupport easyMockSupport,
            final HttpServletRequest servletRequest) throws IOException {
        final ServletInputStream contentInputStream = easyMockSupport.createMock(ServletInputStream.class);
        if (method.equals(POST) || method.equals(PUT)) {
            expect(contentInputStream.available()).andReturn(0).anyTimes();
            if (contentSize > 0) {
                expect(contentInputStream.read((byte[]) EasyMock.anyObject())).andReturn(contentSize).times(1).andReturn(-1).times(1)
                        .andReturn(0).anyTimes();
            } else {
                expect(contentInputStream.read((byte[]) EasyMock.anyObject())).andReturn(contentSize).times(1);
            }
        }
        expect(servletRequest.getInputStream()).andReturn(contentInputStream).atLeastOnce();
    }

    public MockRequestBuilder content(final String content) {
        return this;
    }

    protected void enrichServletContextMock(final ServletContext servletContext) throws ODataException {
    }

    protected void enrichServletRequestMock(final ServletRequest servletRequest) {
    }

    protected abstract ODataServiceFactory getServiceFactoryClass();

    private String createQueryString(final MultivaluedMap<String, String> queryParams) {
        StringBuilder queryString = new StringBuilder();
        for (Entry<String, List<String>> entry : queryParams.entrySet()) {
            for (String paramValue : entry.getValue()) {
                if (queryString.length() > 0) {
                    queryString.append('&');
                }
                queryString.append(entry.getKey() + '=' + paramValue);
            }
        }
        return HttpUtils.pathEncode(queryString.toString());
    }

    private List<PathSegment> createPathSegments(final Collection<String> segmentStrings) {
        final ArrayList<PathSegment> pathSegments = new ArrayList<>();
        if (segmentStrings != null) {
            for (String segmentString : segmentStrings) {
                pathSegments.add(new PathSegmentImpl(segmentString));
            }
        }
        return pathSegments;
    }

    private String absoluteServiceRoot() {
        return PROTOCOL + "://" + HOST + ":" + PORT + RELATIVE_SERVICE_ROOT;
    }

}