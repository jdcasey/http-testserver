package org.commonjava.test.http.expect;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by jdcasey on 11/11/15.
 */
public interface ExpectationHandler
{

    void handle( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException;
}
