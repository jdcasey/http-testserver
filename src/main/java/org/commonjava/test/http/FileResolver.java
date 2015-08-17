package org.commonjava.test.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.commonjava.test.http.util.StreamUtils.isDirectoryResource;

/**
 * Created by jdcasey on 8/17/15.
 */
public class FileResolver
    implements StreamResolver
{

    private File dir;

    public FileResolver( File file )
    {
        if ( !isDirectoryResource( file ) )
        {
            throw new IllegalArgumentException( "FileResolver requires a java.io.File that references directory. You supplied: " + file );
        }

        this.dir = file;
    }

    @Override
    public InputStream get( String path )
            throws IOException
    {
        File f = new File( dir, path );
        if ( f.exists() && !f.isDirectory() )
        {
            return new FileInputStream( f );
        }

        return null;
    }
}
