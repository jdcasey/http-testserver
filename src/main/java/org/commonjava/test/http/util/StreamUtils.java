package org.commonjava.test.http.util;

import java.io.File;

/**
 * Created by jdcasey on 8/17/15.
 */
public final class StreamUtils
{

    private StreamUtils(){}

    public static boolean isJarResource( String url )
    {
        return url != null && ( url.startsWith( "jar:" ) || url.startsWith( "zip:" ) || url.indexOf( ".jar" ) > 0 || url.indexOf( ".zip" ) > 0 );
    }

    public static boolean isDirectoryResource( String url )
    {
        return url != null && new File( trimProtocol( url ) ).isDirectory();
    }

    public static String trimProtocol(String resource )
    {
        String result = resource;
        if ( result.startsWith("jar:") || result.startsWith( "zip:" ) )
        {
            result = result.substring(4);
        }

        if ( result.startsWith( "file:" ) )
        {
            result = result.substring(5);
        }

        return result;
    }
}
