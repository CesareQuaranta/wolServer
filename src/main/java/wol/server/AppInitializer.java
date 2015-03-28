package wol.server;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.ws.rs.core.Application;

//import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

//import com.sun.jersey.spi.container.servlet.ServletContainer;
//import com.sun.jersey.spi.spring.container.servlet.SpringServlet;

public class AppInitializer implements WebApplicationInitializer  {

	@Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        WebApplicationContext context = getContext();
        servletContext.addListener(new ContextLoaderListener(context));
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("DispatcherServlet", new DispatcherServlet(context));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/*");
        
        /*ServletContainer jerseyServlet=new ServletContainer((Application)new ApplicationConfig());
        ServletRegistration.Dynamic jersey = servletContext.addServlet("JerseyServlet", jerseyServlet);
        jersey.setLoadOnStartup(1);
        jersey.addMapping("/rs/*");*/
	}
	
	private AnnotationConfigWebApplicationContext getContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation("wol.server");
        return context;
    }

}
