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
package org.commonjava.test.http.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public final class UrlUtils
{

    private UrlUtils()
    {
    }

    public static String stringQueryParameter( final Object value )
    {
        final String base = String.valueOf( value );
        return "%22" + base + "%22";
    }

    public static String buildUrl( final String baseUrl, final String... parts )
        throws MalformedURLException
    {
        return buildUrl( baseUrl, null, null, parts );
    }

    public static String buildUrl( final String baseUrl, final String basePath, final String[] parts )
        throws MalformedURLException
    {
        return buildUrl( baseUrl, basePath, null, parts );
    }

    public static String buildUrl( final String baseUrl, final Map<String, String> params, final String... parts )
        throws MalformedURLException
    {
        return buildUrl( baseUrl, null, params, parts );
    }

    public static String buildUrl( final String baseUrl, final String basePath, final Map<String, String> params,
                                   final String... parts )
        throws MalformedURLException
    {
        return new URL( buildPath( baseUrl, basePath, params, parts ) ).toExternalForm();
    }

    public static String buildPath( final String rootPath, final String... parts )
        throws MalformedURLException
    {
        return buildPath( rootPath, null, null, parts );
    }

    public static String buildPath( final String rootPath, final String baseSubPath, final String[] parts )
        throws MalformedURLException
    {
        return buildPath( rootPath, baseSubPath, null, parts );
    }

    public static String buildPath( final String rootPath, final Map<String, String> params, final String... parts )
        throws MalformedURLException
    {
        return buildPath( rootPath, null, params, parts );
    }

    public static String buildPath( final String rootPath, final String baseSubPath, final Map<String, String> params,
                                    final String[] parts )
    {
        if ( parts == null || parts.length < 1 )
        {
            return rootPath;
        }

        final StringBuilder pathBuilder = new StringBuilder();

        if ( parts[0] == null || !parts[0].startsWith( rootPath ) )
        {
            pathBuilder.append( rootPath );
        }

        if ( baseSubPath != null && baseSubPath.length() > 0 )
        {
            appendPartTo( pathBuilder, baseSubPath );
        }

        for ( final String part : parts )
        {
            appendPartTo( pathBuilder, part );
        }

        if ( params != null && !params.isEmpty() )
        {
            pathBuilder.append( "?" );
            boolean first = true;
            for ( final Map.Entry<String, String> param : params.entrySet() )
            {
                if ( first )
                {
                    first = false;
                }
                else
                {
                    pathBuilder.append( "&" );
                }

                pathBuilder.append( param.getKey() )
                          .append( "=" )
                          .append( param.getValue() );
            }
        }

        return pathBuilder.toString();
    }

    private static void appendPartTo( final StringBuilder pathBuilder, String part )
    {
        if ( part == null || part.trim()
                                 .length() < 1 )
        {
            return;
        }

        if ( part.startsWith( "/" ) )
        {
            part = part.substring( 1 );
        }

        if ( pathBuilder.length() > 0 && pathBuilder.charAt( pathBuilder.length() - 1 ) != '/' )
        {
            pathBuilder.append( "/" );
        }

        pathBuilder.append( part );
    }

}
