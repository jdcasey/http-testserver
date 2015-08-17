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
package org.commonjava.test.http;

import io.undertow.Undertow;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.util.ImmediateInstanceFactory;
import org.commonjava.test.http.util.PortFinder;
import org.commonjava.test.http.util.UrlUtils;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

import static org.commonjava.test.http.util.StreamUtils.isDirectoryResource;
import static org.commonjava.test.http.util.StreamUtils.isJarResource;

public class StreamServer
    extends ExternalResource
    implements HttpServerFixture<StreamServer>
{

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private Integer port;

    private final StreamServlet servlet;

    private Undertow server;

    public StreamServer( final StreamResolver resolver )
    {
        servlet = new StreamServlet( resolver );
    }

    public StreamServer( final File resourceBase )
    {
        StreamResolver resolver;
        if ( isJarResource( resourceBase ))
        {
            resolver = new JarFileResolver( resourceBase );
        }
        else if ( isDirectoryResource( resourceBase ))
        {
            resolver = new FileResolver( resourceBase );
        }
        else
        {
            throw new IllegalArgumentException( "Cannot serve resources from: " + resourceBase + ". It it neither jar/zip archive nor directory." );
        }

        servlet = new StreamServlet( resolver );
    }

    public int getPort()
    {
        return port;
    }

    @Override
    public void after()
    {
        stop();
    }

    public void stop()
    {
        if ( server != null )
        {
            server.stop();
            logger.info( "STOPPED Test HTTP Server on 127.0.0.1:" + port );
        }
    }

    @Override
    public void before()
        throws Exception
    {
        start();
    }

    public StreamServer start() throws IOException
    {
        final ServletInfo si = Servlets.servlet( "TEST", StreamServlet.class )
                                       .addMapping( "*" )
                                       .addMapping( "/*" )
                                       .setLoadOnStartup( 1 );

        si.setInstanceFactory( new ImmediateInstanceFactory<Servlet>( servlet ) );

        final DeploymentInfo di = new DeploymentInfo().addServlet( si )
                                                      .setDeploymentName( "TEST" )
                                                      .setContextPath( "/" )
                                                      .setClassLoader( Thread.currentThread()
                                                                             .getContextClassLoader() );

        final DeploymentManager dm = Servlets.defaultContainer()
                                             .addDeployment( di );
        dm.deploy();

        port = PortFinder.findOpenPort( 16 );
        try
        {
            server = Undertow.builder()
                             .setHandler( dm.start() )
                             .addHttpListener( port, "127.0.0.1" )
                             .build();
        }
        catch ( ServletException e )
        {
            throw new IOException( "Failed to start: " + e.getMessage(), e );
        }

        server.start();
        logger.info( "STARTED Test HTTP Server on 127.0.0.1:" + port );

        return this;
    }

    public String formatUrl( final String... subpath )
    {
        try
        {
            return UrlUtils.buildUrl( "http://127.0.0.1:" + port, subpath );
        }
        catch ( final MalformedURLException e )
        {
            throw new IllegalArgumentException( "Failed to build url to: " + Arrays.toString( subpath ), e );
        }
    }

    public String formatPath( final String... subpath )
    {
        try
        {
            return UrlUtils.buildPath( "/", subpath );
        }
        catch ( final MalformedURLException e )
        {
            throw new IllegalArgumentException( "Failed to build url to: " + Arrays.toString( subpath ), e );
        }
    }

    public String getBaseUri()
    {
        return "http://127.0.0.1:" + port;
    }

    public String getUrlPath( final String url )
        throws MalformedURLException
    {
        return new URL( url ).getPath();
    }

    public Map<String, Integer> getAccessesByPathKey()
    {
        return servlet.getAccessesByPath();
    }

    public Map<String, ContentResponse> getRegisteredErrors()
    {
        return servlet.getRegisteredErrors();
    }

    public String getAccessKey( final CommonMethod method, final String path )
    {
        return servlet.getAccessKey( method, path );
    }

    public String getAccessKey( final String method, final String path )
    {
        return servlet.getAccessKey( method, path );
    }

    public Integer getAccessesFor( final String path )
    {
        return servlet.getAccessesFor( path );
    }

    public Integer getAccessesFor( final String method, final String path )
    {
        return servlet.getAccessesFor( method, path );
    }
}
