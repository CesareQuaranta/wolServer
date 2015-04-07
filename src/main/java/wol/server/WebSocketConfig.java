package wol.server;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;

//import wol.server.connector.ws.ViewEndpoint;


public class WebSocketConfig{// implements ServerApplicationConfig {
/*
	@Override
	public Set<ServerEndpointConfig> getEndpointConfigs(
			Set<Class<? extends Endpoint>> endpointClasses) {
	       //return new HashSet<ServerEndpointConfig>(Arrays.asList(ServerEndpointConfig.Builder.create(MyEndpointProgrammatic.class, "/echoProgrammatic").configurator(new MyServerConfigurator()).build()));
			return null;
	}

	@Override
	public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
	       return new HashSet<Class<?>>(Arrays.asList(ViewEndpoint.class));
	}
*/
}
