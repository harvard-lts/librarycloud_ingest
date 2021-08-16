package edu.harvard.libcomm.test;

import java.net.URLStreamHandler;
import java.net.URL;
import java.net.URLConnection;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link URLStreamHandler} that allows us to control the {@link URLConnection URLConnections} that are returned
 * by {@link URL URLs} in the code under test.
 */
public class HttpUrlStreamHandler extends URLStreamHandler {

    private Map<URL, URLConnection> connections = new HashMap();

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        return connections.get(url);
    }

    public void resetConnections() {
        connections = new HashMap();
    }

    public HttpUrlStreamHandler addConnection(URL url, URLConnection urlConnection) {
        connections.put(url, urlConnection);
        return this;
    }
}
