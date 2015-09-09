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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.commonjava.test.http.util.StreamUtils.isDirectoryResource;
import static org.commonjava.test.http.util.StreamUtils.trimProtocol;

/**
 * Created by jdcasey on 8/17/15.
 */
public class FileResolver
    implements StreamResolver
{

    private File dir;

    public FileResolver( String resource )
    {
        if ( !isDirectoryResource( resource ) )
        {
            throw new IllegalArgumentException( "FileResolver requires a java.io.File that references directory. You supplied: " + resource );
        }

        String path = trimProtocol( resource );
        this.dir = new File( path );
    }

    @Override
    public InputStream get( String path )
            throws IOException
    {
        File f = new File( dir, path );
        Logger logger = LoggerFactory.getLogger( getClass() );
        logger.info("Looking for file: {}", f);
        if ( f.exists() && !f.isDirectory() )
        {
            logger.info("Opening file: {}", f);
            return new FileInputStream( f );
        }

        logger.info("No file available for: {}", path);
        return null;
    }
}
