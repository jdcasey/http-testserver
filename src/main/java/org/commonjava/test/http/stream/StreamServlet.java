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
package org.commonjava.test.http.stream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.commonjava.test.http.common.CommonMethod;
import org.commonjava.test.http.expect.ContentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public final class StreamServlet
        extends HttpServlet
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private static final long serialVersionUID = 1L;

    private final Map<String, Integer> accessesByPath = new HashMap<>();

    private StreamResolver resolver;

    public StreamServlet()
    {
        throw new IllegalArgumentException( "You cannot use the default constructor for StreamServlet. "
                                                    + "It is designed to be passed into the StreamServer Undertow "
                                                    + "instance using an ImmediateInstanceFactory." );
    }

    public StreamServlet( StreamResolver resolver )
    {
        this.resolver = resolver;
    }

    public Map<String, Integer> getAccessesByPath()
    {
        return accessesByPath;
    }

    public String getAccessKey( final String method, final String path )
    {
        return method.toUpperCase() + " " + path;
    }

    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse resp )
            throws ServletException, IOException
    {
        String wholePath;
        try
        {
            wholePath = new URI( req.getRequestURI() ).getPath();
        }
        catch ( final URISyntaxException e )
        {
            throw new ServletException( "Cannot parse request URI", e );
        }

        String path = wholePath;
        if ( path.length() > 1 )
        {
            path = path.substring( 1 );
        }

        final String key = getAccessKey( req.getMethod(), wholePath );

        logger.info( "Request: {}", key );
        final Integer i = accessesByPath.get( key );
        if ( i == null )
        {
            accessesByPath.put( key, 1 );
        }
        else
        {
            accessesByPath.put( key, i + 1 );
        }

        logger.info( "Looking for resource: '{}'", path );
        try (InputStream in = resolver.get( path );
             OutputStream out = resp.getOutputStream())
        {
            if ( in != null )
            {
                logger.info("Found: {}", in);
                resp.setStatus( 200 );
                IOUtils.copy( in, out );
            }
            else
            {
                logger.info("Not found: {}", path);
                resp.setStatus( 404 );
            }
        }
        catch ( Exception e )
        {
            logger.info( "Error retrieving: " + path + ". Reason: " + e.getMessage(), e );

            resp.setStatus( 500 );
            resp.getWriter().write( StringUtils.join( e.getStackTrace(), "\n" ) );
        }
    }

    public String getAccessKey( final CommonMethod method, final String path )
    {
        return getAccessKey( method.name(), path );
    }

    public Integer getAccessesFor( final String path )
    {
        return accessesByPath.get( getAccessKey( CommonMethod.GET, path ) );
    }

    public Integer getAccessesFor( final String method, final String path )
    {
        return accessesByPath.get( getAccessKey( method, path ) );
    }

}