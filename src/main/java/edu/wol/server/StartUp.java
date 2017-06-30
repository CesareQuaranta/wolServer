package edu.wol.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import edu.wol.dom.WolContainer;
import edu.wol.dom.WorldContainer;
import edu.wol.dom.space.Planetoid;
import edu.wol.starsystem.SolarSystem;

@Component
public class StartUp implements ApplicationListener<ApplicationContextEvent> {
	final static Logger logger = LoggerFactory.getLogger(StartUp.class);
	@Autowired 
	private WolContainer<SolarSystem,Planetoid> wolContainer;
	private Thread wolThread;
	private WebApplicationContext ctx=null;
	
	@Override
	public void onApplicationEvent(ApplicationContextEvent event) {
		if(event instanceof ContextStartedEvent || event instanceof ContextRefreshedEvent){
			try {
				ctx = (WebApplicationContext) event.getApplicationContext();
				wolContainer.init();
				wolThread=new Thread(wolContainer,"WolThread");
				wolThread.setContextClassLoader(ClassLoader.getSystemClassLoader());
				wolThread.start();
				logger.info("Wol Started");
				
			} catch (Exception e) {
				logger.error("Startup Error:", e);
			} 
		}
		
	}
}
