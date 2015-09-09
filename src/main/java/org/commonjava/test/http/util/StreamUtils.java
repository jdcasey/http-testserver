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
