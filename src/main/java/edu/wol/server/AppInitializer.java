package edu.wol.server;

import java.lang.annotation.Annotation;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import org.apache.felix.http.proxy.ProxyListener;
import org.apache.felix.http.proxy.ProxyServlet;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
//import wol.server.connector.ws.ViewEndpoint;

@Component
public class AppInitializer implements WebApplicationInitializer,ServletContainerInitializer  {
	private static final String WEB_SERVLET_ANNOTATION_TYPE = "javax.servlet.annotation.WebServlet";
	private static final String WEB_FILTER_ANNOTATION_TYPE = "javax.servlet.annotation.WebFilter";
	private static final String WEB_LISTENER_ANNOTATION_TYPE = "javax.servlet.annotation.WebListener";

	private static final Set<String> SUPPORTED_WEB_ANNOTATION_TYPES = new LinkedHashSet<>(Arrays.asList(
			WEB_SERVLET_ANNOTATION_TYPE, WEB_FILTER_ANNOTATION_TYPE, WEB_LISTENER_ANNOTATION_TYPE));

/**
 * Servlet 3.0+ environments to configure the ServletContext programmatically 
 * as opposed to (or possibly in conjunction with) the traditional web.xml-based approach.
 */
	@Override
    public void onStartup(ServletContext servletContext) throws ServletException {
		AnnotationConfigWebApplicationContext ctx = getContext();//WebApplicationContextUtils.getWebApplicationContext(servletContext);
		servletContext.setInitParameter("spring.profiles.default", "production");

		ContextLoaderListener loaderListener = new ContextLoaderListener(ctx);
		try {
		// Make context listens for servlet events
	   servletContext.addListener(loaderListener);
	   servletContext.addFilter("HibernataLazyFilter", OpenEntityManagerInViewFilter.class);
	   //Search for WebListners
	   for (Map.Entry<String, Object> entry : ctx.getBeansWithAnnotation(WebListener.class).entrySet()){
		   System.out.println("Registry WebListner  "+entry.getKey());
		   servletContext.addListener((ServletContextListener)entry.getValue());
	   }
	  
	    // Make context know about the servletContext
	    ctx.setServletContext(servletContext);
	    
	    //servletContext.addListener("org.springframework.web.context.request.RequestContextListener");
	    
	    /*
	   ServletRegistration.Dynamic jerseyServlet = servletContext.addServlet("JerseyServlet", new JerseyServlet());
	    jerseyServlet.setInitParameter("javax.ws.rs.Application", "wol.server.connector.jaxrs.JerseyConfig");
	    jerseyServlet.setLoadOnStartup(1);
	    jerseyServlet.addMapping("/testRest/*");*/
	    //initOSGI(servletContext);
	    //final ServerContainer serverContainer = (ServerContainer) servletContext.getAttribute("javax.websocket.server.ServerContainer");
	    //serverContainer.addEndpoint(ViewEndpoint.class);
		}catch (Exception e) {
			e.printStackTrace();
		}
	    
       // servletContext.addListener(new ContextLoaderListener(context));
        //ServletRegistration.Dynamic dispatcher = servletContext.addServlet("DispatcherServlet", new DispatcherServlet(context));
        //dispatcher.setLoadOnStartup(1);
        //dispatcher.addMapping("/*");
        
        /*ServletContainer jerseyServlet=new ServletContainer((Application)new ApplicationConfig());
        ServletRegistration.Dynamic jersey = servletContext.addServlet("JerseyServlet", jerseyServlet);
        jersey.setLoadOnStartup(1);
        jersey.addMapping("/rs/*");
     
		WolContainer<StarsContainer,iPlanetoid> wolContainer=ctx.getBean(WolContainer.class);
		Thread wolThread;
		/*try {
			((WolContainerImpl<?,?>)wolContainer).init();
			wolThread=new Thread(wolContainer,"WolThread");
			wolThread.setContextClassLoader(ClassLoader.getSystemClassLoader());
			wolThread.start();
			System.out.print("Wol Started");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
	}
	
	private AnnotationConfigWebApplicationContext getContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation("edu.wol.server");
        context.register(ApplicationContext.class);  
        context.scan("edu.wol.server");
        context.refresh();
        context.registerShutdownHook(); // add a shutdown hook for the above context...
        return context;
    }

	@SuppressWarnings("unchecked")
	private void configureIncludeFilters(ClassPathScanningCandidateComponentProvider provider, ClassLoader cl) {

		for (String annotationType : SUPPORTED_WEB_ANNOTATION_TYPES) {
			try {
				AnnotationTypeFilter filter = new AnnotationTypeFilter((Class<? extends Annotation>) ClassUtils.forName(annotationType, cl));
				provider.addIncludeFilter(filter);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void registerJeeComponents(ConfigurableListableBeanFactory beanFactory, ClassLoader cl,
			Set<BeanDefinition> candiates) {

		for (BeanDefinition bd : candiates) {

			try {
				Class<?> beanClass = ClassUtils.forName(bd.getBeanClassName(), cl);
				WebServlet webServlet = beanClass.getDeclaredAnnotation(WebServlet.class);
				WebFilter webFilter = beanClass.getDeclaredAnnotation(WebFilter.class);
				WebListener webListener = beanClass.getDeclaredAnnotation(WebListener.class);

				DefaultListableBeanFactory targetBeanFactory = (DefaultListableBeanFactory) beanFactory;

				if (webServlet != null) {
					//createAndRegisterServletBean(targetBeanFactory, bd, beanClass, webServlet);
				} else if (webFilter != null) {
					//createAndRegisterServletFilterBean(targetBeanFactory, bd, beanClass, webFilter);
				} else if (webListener != null) {
					//createAndRegisterWebListenerBean(targetBeanFactory, bd, beanClass, webListener);
				}

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void initOSGI(ServletContext servletContext) throws Exception{
		ServiceLoader<FrameworkFactory> loader = ServiceLoader.load(FrameworkFactory.class);
		if(loader.iterator().hasNext()){
			FrameworkFactory frameworkFactory=loader.iterator().next();
			Map<String, String> config = new HashMap<String, String>();

			    // make sure the cache is cleaned
			config.put(Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
			//config.put(Constants.FRAMEWORK_SYSTEMPACKAGES, "javax.servlet;javax.servlet.http;version=2.6;org.osgi.service.http");
			//config.put(Constants.FRAMEWORK_SYSTEMPACKAGES, "org.osgi.service.http");
			    // more properties available at: http://felix.apache.org/documentation/subprojects/apache-felix-service-component-runtime.html
			config.put("ds.showtrace", "true");
			config.put("ds.showerrors", "true");
			//config.put("felix.auto.deploy.dir", "./bundle");

			   
			Framework framework = frameworkFactory.newFramework(config);
			System.out.println("Starting OSGi Framework");
		    framework.start();
		    
		    servletContext.setAttribute(BundleContext.class.getName(),framework.getBundleContext());
		    ServletRegistration.Dynamic osgiProxyServlet=servletContext.addServlet("osgiProxyServlet", ProxyServlet.class);
		    osgiProxyServlet.setLoadOnStartup(1);
		    osgiProxyServlet.addMapping("/*");
		    
		    servletContext.addListener(ProxyListener.class);
		   
		    // declarative services dependency is necessary, otherwise they won't be picked up!
		    loadScrBundle(framework);
		    loadAllBundles(framework,servletContext);
		}else{
			System.out.println("No OSGI Framework found");
		}
	}
	private void loadScrBundle(Framework framework) throws URISyntaxException, BundleException {
	    URL url = getClass().getClassLoader().getResource("org/apache/felix/scr/ScrService.class");
	    if (url == null)
	        throw new RuntimeException("Could not find the class org.apache.felix.scr.ScrService");
	    String jarPath = url.toURI().getSchemeSpecificPart().replaceAll("!.*", "");
	    System.out.println("Found declarative services implementation: " + jarPath);
	    framework.getBundleContext().installBundle(jarPath).start();
	}
	
	
	private void loadAllBundles(Framework framework,ServletContext servletContext) throws Exception {
		List<URL> bundletsUrls=findBundles(servletContext);
		if(bundletsUrls!=null){
			 for(URL curUrl:bundletsUrls){
				String jarPath="file:"+servletContext.getRealPath(curUrl.toExternalForm()).replaceAll("jndi:/localhost/wolServer/", "");
				Bundle curBundle=framework.getBundleContext().installBundle(jarPath);
				  System.out.println("Bundle: " + curBundle.getSymbolicName()+" installed.");
				  curBundle.start(Bundle.START_ACTIVATION_POLICY);
				  //curBundle.start();
				  System.out.println("Bundle: " + curBundle.getSymbolicName()+" started.");
			    }
		} 
		/*
	    ServiceReference<?> reference = framework.getBundleContext().getServiceReference("my.Interface");
	   if(reference!=null)
		   System.out.println(framework.getBundleContext().getService(reference));

	    for (Bundle bundle : framework.getBundleContext().getBundles()) {
	        System.out.println("Bundle: " + bundle.getSymbolicName());
	        if (bundle.getRegisteredServices() != null) {
	            for (ServiceReference<?> serviceReference : bundle.getRegisteredServices())
	                System.out.println("\tRegistered service: " + serviceReference);
	        }
	    }*/
	}
	
	private List<URL> findBundles(ServletContext servletContext) throws Exception
	    {
	        ArrayList<URL> list = new ArrayList<URL>();
	        Set<String> bundleCandidates=servletContext.getResourcePaths("/WEB-INF/bundles/");
	        if(bundleCandidates!=null && !bundleCandidates.isEmpty()){
	        	for (Object o : bundleCandidates) {
		            String name = (String)o;
		            if (name.endsWith(".jar")) {
		                URL url = servletContext.getResource(name);
		                if (url != null) {
		                    list.add(url);
		                }
		            }
		        }
	        }
	   
	        return list;
	    }

	@Override
	public void onStartup(Set<Class<?>> c, ServletContext ctx)
			throws ServletException {
		 onStartup(ctx);
		
	}
}
