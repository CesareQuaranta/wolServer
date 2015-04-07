package wol.server;

import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;

//@EnableWebMvc
public class MvcConfiguration {//extends WebMvcConfigurerAdapter
	
	public MvcConfiguration() {
		// TODO Auto-generated constructor stub
	}
	/*
	 @Override
	    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
	      configurer.favorPathExtension(false).
	              favorParameter(true).
	              parameterName("mediaType").
	              ignoreAcceptHeader(true).
	              useJaf(false).
	              defaultContentType(MediaType.APPLICATION_JSON).
	              mediaType("xml", MediaType.APPLICATION_XML).
	              mediaType("json", MediaType.APPLICATION_JSON);
	    }*/
	/* @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
 
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix(VIEW_RESOLVER_PREFIX);
        viewResolver.setSuffix(VIEW_RESOLVER_SUFFIX);
 
        return viewResolver;
    }*/

}
