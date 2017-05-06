package wol.server;
import java.io.File;

import javax.annotation.Resource;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebListener;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;

import wol.dom.WolContainer;
import wol.dom.space.iPlanetoid;
import wol.server.connector.ws.WebSocketInitializer;
import wol.server.repository.KryoRepository;
import wol.server.repository.WolRepository;
import wol.starsystem.StarsContainer;
 
/**
 * An application context Java configuration class. The usage of Java configuration
 * requires Spring Framework 3.0 or higher with following exceptions:
 * <ul>
 *     <li>@EnableWebMvc annotation requires Spring Framework 3.1</li>
 * </ul>
 * @author Petri Kainulainen
 */
@Configuration
@ComponentScan(basePackages = {"wol.server"},
useDefaultFilters = false,
includeFilters = { @ComponentScan.Filter(type = FilterType.ANNOTATION, value=WebListener.class)},
excludeFilters = { @ComponentScan.Filter( Configuration.class ) })
//@ImportResource("classpath:applicationContext.xml")
@PropertySource("classpath:application.properties")
public class ApplicationContext {
     
    private static final String VIEW_RESOLVER_PREFIX = "/WEB-INF/jsp/";
    private static final String VIEW_RESOLVER_SUFFIX = ".jsp";
 
    private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";
    private static final String PROPERTY_NAME_DATABASE_PASSWORD = "db.password";
    private static final String PROPERTY_NAME_DATABASE_URL = "db.url";
    private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";
 
    private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
    private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
    private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN = "entitymanager.packages.to.scan";
 
    private static final String PROPERTY_NAME_MESSAGESOURCE_BASENAME = "message.source.basename";
    private static final String PROPERTY_NAME_MESSAGESOURCE_USE_CODE_AS_DEFAULT_MESSAGE = "message.source.use.code.as.default.message";
 
    private static final String PROPERTY_NAME_REPOSITORY_PATH = "repository.path";
    
    @Resource
    private Environment environment;
 
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
 
        messageSource.setBasename(environment.getRequiredProperty(PROPERTY_NAME_MESSAGESOURCE_BASENAME));
        messageSource.setUseCodeAsDefaultMessage(Boolean.parseBoolean(environment.getRequiredProperty(PROPERTY_NAME_MESSAGESOURCE_USE_CODE_AS_DEFAULT_MESSAGE)));
 
        return messageSource;
    }
    
   
    /*
    @Bean
    public WolRepository<StarsContainer,iPlanetoid> kryoRepository(){
    	File repoPath=new File(environment.getRequiredProperty(PROPERTY_NAME_REPOSITORY_PATH));
    	if(!repoPath.exists()){
    		repoPath.mkdir();
    	}
    	WolRepository<StarsContainer,iPlanetoid> repository=new KryoRepository<StarsContainer,iPlanetoid>(repoPath, StarsContainer.class);
    	return repository;
    }*/
    
    @Bean
    public WolContainer wolContainer(){
    	WolContainerImpl<StarsContainer,iPlanetoid> wolContainer=new WolContainerImpl<StarsContainer,iPlanetoid>(StarsContainer.class,1,1);
    	return wolContainer;
    }
}
