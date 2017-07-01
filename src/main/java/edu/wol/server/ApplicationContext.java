package edu.wol.server;
import java.util.Properties;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import edu.wol.dom.WolContainer;
import edu.wol.dom.space.Planetoid;
import edu.wol.starsystem.SolarSystem;
import edu.wol.starsystem.StarDial;
 
/**
 * An application context Java configuration class. The usage of Java configuration
 * requires Spring Framework 3.0 or higher with following exceptions:
 * <ul>
 *     <li>@EnableWebMvc annotation requires Spring Framework 3.1</li>
 * </ul>
 * @author Petri Kainulainen
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {"edu.wol.server"},
useDefaultFilters = false,
includeFilters = { @ComponentScan.Filter(type = FilterType.ANNOTATION, value=WebListener.class)},
excludeFilters = { @ComponentScan.Filter( Configuration.class ) })
//@ImportResource("classpath:applicationContext.xml")
@PropertySource("classpath:application.properties")
public class ApplicationContext {
     
    private static final String VIEW_RESOLVER_PREFIX = "/WEB-INF/jsp/";
    private static final String VIEW_RESOLVER_SUFFIX = ".jsp";
   
    private static final String PROPERTY_NAME_NODE_ID = "node.id";
    
    private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";
    private static final String PROPERTY_NAME_DATABASE_URL = "db.url";
    private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";
    private static final String PROPERTY_NAME_DATABASE_PASSWORD = "db.password";
    private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String PROPERTY_NAME_HIBERNATE_DDL_STRATEGY = "hibernate.hbm2ddl.auto";
    private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
    private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
 
    private static final String PROPERTY_NAME_MESSAGESOURCE_BASENAME = "message.source.basename";
    private static final String PROPERTY_NAME_MESSAGESOURCE_USE_CODE_AS_DEFAULT_MESSAGE = "message.source.use.code.as.default.message";
 
    private static final String PROPERTY_NAME_REPOSITORY_PATH = "repository.path";
    
    final static Logger logger = LoggerFactory.getLogger(ApplicationContext.class);
    @Resource
    private Environment environment;
 
    @Bean
    public WolContainer<SolarSystem,Planetoid> wolContainer(){
    	String nodeID=environment.getRequiredProperty(PROPERTY_NAME_NODE_ID);
    	if(nodeID==null){
    		logger.warn("No Property "+PROPERTY_NAME_NODE_ID+" found, using default");
    		nodeID="DEFAULT-NODE-ID";
    	}
    	WolContainerImpl<SolarSystem,Planetoid> wolContainer=new WolContainerImpl<SolarSystem,Planetoid>(SolarSystem.class,nodeID,1,1);
    	return wolContainer;
    }
    
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
    }
    @Bean
    public WolRepository<StarDial,iPlanetoid> jpaRepository(){
    	WolRepository<StarDial,iPlanetoid> repository=new JPARepository<StarDial,iPlanetoid>(StarDial.class);
    	return repository;
    }*/
    
    @Bean(name="entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
       LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
       em.setDataSource(dataSource());
       em.setPackagesToScan(new String[] { "edu.wol" });
  
       JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
       em.setJpaVendorAdapter(vendorAdapter);
       em.setJpaProperties(additionalProperties());
  
       return em;
    }
  
    @Bean
    public DataSource dataSource(){
       DriverManagerDataSource dataSource = new DriverManagerDataSource();
       dataSource.setDriverClassName(environment.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
       dataSource.setUrl(environment.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
       dataSource.setUsername( environment.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME));
       dataSource.setPassword( environment.getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD));
       return dataSource;
    }
  
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf){
       JpaTransactionManager transactionManager = new JpaTransactionManager();
       transactionManager.setEntityManagerFactory(emf);
       return transactionManager;
    }
  
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
       return new PersistenceExceptionTranslationPostProcessor();
    }
  
    Properties additionalProperties() {
       Properties properties = new Properties();
       properties.setProperty(PROPERTY_NAME_HIBERNATE_DDL_STRATEGY,environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DDL_STRATEGY));
       properties.setProperty("hibernate.dialect", environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
       return properties;
    }
    
   
}
