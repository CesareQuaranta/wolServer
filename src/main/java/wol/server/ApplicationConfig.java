package wol.server;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import wol.server.connector.jaxrs.resource.Login;
import wol.server.connector.jaxrs.resource.View;

public class ApplicationConfig extends Application {
    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>(Arrays.asList(View.class, Login.class));
    }
}