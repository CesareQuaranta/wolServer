package wol.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import wol.dom.WolContainer;

@Component
public class StartUp implements ApplicationListener<ApplicationContextEvent> {
	@Autowired 
	private WolContainer wolContainer;
	private Thread wolThread;
	private WebApplicationContext ctx=null;
	
	@Override
	public void onApplicationEvent(ApplicationContextEvent event) {
		if(event instanceof ContextStartedEvent || event instanceof ContextRefreshedEvent){
			try {
				ctx = (WebApplicationContext) event.getApplicationContext();
				((WolContainerImpl<?,?>)wolContainer).init();
				wolThread=new Thread(wolContainer,"WolThread");
				wolThread.setContextClassLoader(ClassLoader.getSystemClassLoader());
				wolThread.start();
				System.out.println("Wol Started");
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		}
		
	}
}
