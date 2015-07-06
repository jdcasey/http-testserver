# Simulating remote HTTP servers for functional testing

This is a test fixture, which provides a very basic servlet that registers expected requests and logs access counts for each requested method/path combination. If no expectation is registered for a particular method/path, 404 is returned.

Usage is pretty simple:

    @Rule
    public TestHttpServer server = new TestHttpServer( "repos" );

    @Test
    public void run()
        throws Exception
    {
        String path = "/repos/path/to/something.txt";
        String content = "this is the content";
        final String url = server.formatUrl( path );
        server.expect( url, 200, content );

        final HttpGet get = new HttpGet( url );
        final CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = null;

        InputStream stream = null;
        try
        {
            response = client.execute( get );
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
	            IOUtil.closeQuietly( response );
	        }

	        if ( request != null )
	        {
	            if ( request instanceof AbstractExecutionAwareRequest )
	            {
	                ( (AbstractExecutionAwareRequest) request ).reset();
	            }
	        }

	        if ( client != null )
	        {
	            IOUtil.closeQuietly( client );
	        }
        }

        assertThat( server.getAccessesByPath().get(path), equalTo( 1 ) );
    }
