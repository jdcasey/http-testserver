package org.commonjava.test.http.stream;

import org.commonjava.test.http.util.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import static org.commonjava.test.http.util.StreamUtils.isJarResource;
import static org.commonjava.test.http.util.StreamUtils.trimProtocol;

/**
 * Created by jdcasey on 8/17/15.
 */
public class JarFileResolver
        implements StreamResolver
{

    private final String archive;

    private JarFile dir;

    private String basePath;

    public JarFileResolver( String resource )
    {
        if ( !isJarResource( resource ) )
        {
            throw new IllegalArgumentException(
                    "JarFileResolver requires a reference to a .jar or .zip file. You supplied: " + resource );
        }

        String path = trimProtocol( resource );
        String[] parts = path.split( "!" );
        try
        {
            this.archive  = parts[0];
            this.dir = new JarFile( parts[0] );
            if ( parts.length > 1 && parts[1].length() > 0 )
            {
                String bp = parts[1];
                if ( bp.startsWith( "/" ) )
                {
                    if ( bp.length() < 2 )
                    {
                        bp = null;
                    }
                    else
                    {
                        bp = bp.substring( 1 );
                    }
                }
                basePath = bp;
            }
            else
            {
                basePath = null;
            }
        }
        catch ( IOException e )
        {
            throw new IllegalArgumentException( "Invalid jar/zip file: " + resource + "(file part: " + parts[0] + "). Reason: " + e.getMessage(),
                                                e );
        }
    }

    @Override
    public InputStream get( String path )
            throws IOException
    {
        String realPath = UrlUtils.buildPath( basePath, path );
        Logger logger = LoggerFactory.getLogger( getClass() );
        logger.info("Looking for: {} in archive: {}", realPath, archive);

        ZipEntry entry = dir.getEntry( realPath );
        if ( entry == null )
        {
            logger.info("Not found: {} (basePath: {}, requested path: {})", realPath, basePath, path);
            return null;
        }

        logger.info("Returning stream for: {} (basePath: {}, requested path: {})", realPath, basePath, path);
        return dir.getInputStream( entry );
    }
}
