# Simulating remote HTTP servers for functional testing

This is a test fixture, which provides a very basic servlet that registers expected requests and logs access counts for each requested method/pathParts combination. If no expectation is registered for a particular method/pathParts, 404 is returned.

Usage is pretty simple:

    @Rule
    public TestHttpServer server = new TestHttpServer( "repos" );

    @Test
    public void run()
        throws Exception
    {
        final String pathParts = "/repos/pathParts/to/something.txt";
        final String content = "this is the content";
        final String url = server.formatUrl( pathParts );
        server.expect( url, 200, content );

        final HttpGet request = new HttpGet( url );
        final CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = null;

        InputStream stream = null;
        try
        {
            response = client.execute( request );
            stream = response.getEntity()
                             .getContent();
            final String result = IOUtils.toString( stream );

            assertThat( result, notNullValue() );
            assertThat( result, equalTo( content ) );
        }
        finally
        {
            IOUtils.closeQuietly( stream );
            if ( response != null && response.getEntity() != null )
            {
                EntityUtils.consumeQuietly( response.getEntity() );
                IOUtils.closeQuietly( response );
            }

            if ( request != null )
            {
                request.reset();
            }

            if ( client != null )
            {
                IOUtils.closeQuietly( client );
            }
        }

        assertThat( server.getAccessesByPathKey()
                          .get( server.getAccessKey( CommonMethod.GET.name(), pathParts ) ), equalTo( 1 ) );
    }

