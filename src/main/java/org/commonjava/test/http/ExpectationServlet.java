package org.commonjava.test.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExpectationServlet
    extends HttpServlet
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private static final long serialVersionUID = 1L;

    private final String baseResource;

    private final Map<String, ContentResponse> expectations = new HashMap<>();

    private final Map<String, Integer> accessesByPath = new HashMap<>();

    private final Map<String, ContentResponse> errors = new HashMap<>();

    public ExpectationServlet()
    {
        logger.error( "Default constructor not actually supported!!!" );
        this.baseResource = "/";
    }

    public ExpectationServlet( final String baseResource )
    {
        this.baseResource = baseResource == null ? "/" : baseResource;
    }

    public Map<String, Integer> getAccessesByPath()
    {
        return accessesByPath;
    }

    public Map<String, ContentResponse> getRegisteredErrors()
    {
        return errors;
    }

    public String getBaseResource()
    {
        return baseResource;
    }

    public void registerException( final String method, final String path, final int code, final String error )
    {
        final String realPath = getPath( path );
        final String key = getAccessKey( method, realPath );
        logger.info( "Registering error: {}, code: {}, body:\n{}", key, code, error );
        this.errors.put( key, new ContentResponse( method, realPath, code, error ) );
    }

    public String getAccessKey( final String method, final String path )
    {
        return method.toUpperCase() + " " + path;
    }

    private String getPath( final String path )
    {
        String realPath = path;
        try
        {
            final URL u = new URL( path );
            realPath = u.getPath();
        }
        catch ( final MalformedURLException e )
        {
        }

        return realPath;
    }

    public void expect( final String method, final String testUrl, final int responseCode, final String body )
        throws Exception
    {
        final String path = getPath( testUrl );
        final String key = getAccessKey( method, path );
        logger.info( "Registering expectation: {}, code: {}, body:\n{}", key, responseCode, body );
        expectations.put( key, new ContentResponse( method, path, responseCode, body ) );
    }

    public void expect( final String method, final String testUrl, final int responseCode,
                        final InputStream bodyStream )
        throws Exception
    {
        final String path = getPath( testUrl );

        final String key = getAccessKey( method, path );
        logger.info( "Registering expectation: {}, code: {}, body stream:\n{}", key, responseCode, bodyStream );
        expectations.put( key, new ContentResponse( method, path, responseCode,
                                                                                  bodyStream ) );
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

        logger.info( "Looking up expectation for: {}", key );

        final Integer i = accessesByPath.get( key );
        if ( i == null )
        {
            accessesByPath.put( key, 1 );
        }
        else
        {
            accessesByPath.put( key, i + 1 );
        }

        logger.info( "Looking for error: '{}' in:\n{}", key, errors );
        if ( errors.containsKey( key ) )
        {
            final ContentResponse error = errors.get( key );
            logger.error( "Returning registered error: {}", error );
            resp.sendError( error.code() );

            if ( error.body() != null )
            {
                resp.getWriter()
                    .write( error.body() );
            }

            return;
        }

        logger.info( "Looking for expectation: '{}'", key );
        final ContentResponse expectation = expectations.get( key );
        if ( expectation != null )
        {
            logger.info( "Responding via registered expectation: {}", expectation );

            resp.setStatus( expectation.code() );

            if ( expectation.body() != null )
            {
                resp.getWriter()
                    .write( expectation.body() );
            }
            else if ( expectation.bodyStream() != null )
            {
                IOUtils.copy( expectation.bodyStream(), resp.getOutputStream() );
            }

            return;
        }

        resp.setStatus( 404 );
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