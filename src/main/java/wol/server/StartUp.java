package wol.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

@Component
public class StartUp implements ApplicationListener<ApplicationContextEvent> {
	@Autowired 
	private WolContainerImpl<?,?> wolContainer;
	private Thread wolThread;
	
	@Override
	public void onApplicationEvent(ApplicationContextEvent event) {
		if(event instanceof ContextStartedEvent || event instanceof ContextRefreshedEvent){
			try {
				wolContainer.init();
				wolThread=new Thread(wolContainer,"WolThread");
				wolThread.setContextClassLoader(ClassLoader.getSystemClassLoader());
				wolThread.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		}
		
	}
   
}
