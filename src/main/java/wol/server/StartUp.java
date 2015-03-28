package wol.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

import wol.dom.WolContainer;
import wol.dom.space.iPlanetoid;
import wol.server.WolContainerImpl;
import wol.starsystem.StarsContainer;

@Component
public class StartUp implements ApplicationListener<ApplicationContextEvent> {
	@Autowired 
	private WolContainer<StarsContainer,iPlanetoid> wolContainer;
	private Thread wolThread;
	
	@Override
	public void onApplicationEvent(ApplicationContextEvent event) {
		if(event instanceof ContextStartedEvent || event instanceof ContextRefreshedEvent){
			try {
				((WolContainerImpl<?,?>)wolContainer).init();
				wolThread=new Thread(wolContainer,"WolThread");
				wolThread.setContextClassLoader(ClassLoader.getSystemClassLoader());
				wolThread.start();
				System.out.print("Wol Started");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		}
		
	}
   
}
