package dev.hv.rest.server;

import java.net.URI;
import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class Server {
    static final String pack = "dev.hv.rest.resource";
    static HttpServer server;
    
    public static void startServer(String url) {
        System.out.println("Start server");
        System.out.println(url);
        //final ResourceConfig rc = new ResourceConfig().packages(pack).register(AuthenticationFilter.class);
        final ResourceConfig rc = new ResourceConfig().packages(pack);
        server = JdkHttpServerFactory.createHttpServer(URI.create(url), rc);
        System.out.println("Ready for Requests....");

    }

    public static void stopServer() {
        server.stop(0);
        System.out.println("Stop server");

    }

    public static void main(String [] args) {
        startServer("http://127.0.0.1:8080/rest");
    }
}
