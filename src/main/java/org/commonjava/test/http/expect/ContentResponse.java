/**
 * Copyright (C) 2015 Red Hat, Inc. (jdcasey@commonjava.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.commonjava.test.http.expect;

import java.io.InputStream;

public final class ContentResponse
{
    private final Integer code;

    private final InputStream bodyStream;

    private final String body;

    private final String path;

    private final ExpectationHandler handler;

    private final String method;

    ContentResponse( final String method, final String path, final int code, final String body )
    {
        this.method = method;
        this.path = path;
        this.code = code;
        this.body = body;
        this.bodyStream = null;
        handler = null;
    }

    ContentResponse( final String method, final String path, final int code, final InputStream bodyStream )
    {
        this.method = method;
        this.path = path;
        this.code = code;
        this.body = null;
        handler = null;
        this.bodyStream = bodyStream;
    }

    ContentResponse( String method, String path, ExpectationHandler handler )
    {
        this.method = method;
        this.path = path;
        this.handler = handler;
        this.body = null;
        this.bodyStream = null;
        code = null;
    }

    public String method()
    {
        return method;
    }

    public String path()
    {
        return path;
    }

    public Integer code()
    {
        return code;
    }

    public String body()
    {
        return body;
    }

    public InputStream bodyStream()
    {
        return bodyStream;
    }

    public ExpectationHandler handler()
    {
        return handler;
    }

    @Override
    public String toString()
    {
        return "ContentResponse{" +
                "\n\tcode=" + code +
                "\n\tbodyStream=" + bodyStream +
                "\n\tbody='" + body + '\'' +
                "\n\tpath='" + path + '\'' +
                "\n\thandler=" + handler +
                "\n\tmethod='" + method + '\'' +
                "\n}";
    }
}