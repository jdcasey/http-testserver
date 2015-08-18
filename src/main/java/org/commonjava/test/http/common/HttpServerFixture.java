package org.commonjava.test.http.common;

import java.io.IOException;

/**
 * Created by jdcasey on 8/17/15.
 */
public interface HttpServerFixture<T extends HttpServerFixture<T>>
{
    T start() throws IOException;
    void stop();
}
