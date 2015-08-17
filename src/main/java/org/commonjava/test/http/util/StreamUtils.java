package org.commonjava.test.http.util;

import java.io.File;

/**
 * Created by jdcasey on 8/17/15.
 */
public final class StreamUtils
{

    private StreamUtils(){}

    public static boolean isJarResource( File file )
    {
        return !( file == null || file.isDirectory() ) && (file.getName().endsWith( ".jar" ) || file.getName().endsWith( ".zip" ) );
    }

    public static boolean isDirectoryResource( File file )
    {
        return file != null && file.isDirectory();
    }
}
