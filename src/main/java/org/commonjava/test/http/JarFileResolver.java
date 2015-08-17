package org.commonjava.test.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import static org.commonjava.test.http.util.StreamUtils.isJarResource;

/**
 * Created by jdcasey on 8/17/15.
 */
public class JarFileResolver
    implements StreamResolver
{

    private JarFile dir;

    public JarFileResolver( File file )
    {
        if ( !isJarResource( file ) )
        {
            throw new IllegalArgumentException( "JarFileResolver requires a java.io.File that references .jar or .zip file. You supplied: " + file );
        }

        try
        {
            this.dir = new JarFile( file );
        }
        catch ( IOException e )
        {
            throw new IllegalArgumentException( "Invalid jar/zip file: " + file + ". Reason: " + e.getMessage(), e );
        }
    }

    @Override
    public InputStream get( String path )
            throws IOException
    {
        ZipEntry entry = dir.getEntry( path );
        if ( entry == null )
        {
            return null;
        }

        return dir.getInputStream( entry );
    }
}
