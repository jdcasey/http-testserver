package org.commonjava.test.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jdcasey on 8/17/15.
 */
public interface StreamResolver
{
    InputStream get( String path )
            throws IOException;
}
