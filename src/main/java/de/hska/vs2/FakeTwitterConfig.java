package de.hska.vs2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "de.hska.vs2" })
public class FakeTwitterConfig extends WebMvcConfigurerAdapter {

	//TODO: Klasse ist so noch nicht vollständig, für die Template-Methoden fehlt in dieser Form noch weiterer Input, ggfs. auch weitere Imports!!!
	// siehe hierzu Kapitel 4.3 des Thymeleaf-Tutorials

	@Bean
	public SpringResourceTemplateResolver templateResolver() {
	    // SpringResourceTemplateResolver automatically integrates with Spring's own
	    // resource resolution infrastructure, which is highly recommended.
	    SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
	    templateResolver.setApplicationContext(this.applicationContext);
	    templateResolver.setPrefix("/WEB-INF/templates/");
	    templateResolver.setSuffix(".html");
	    // HTML is the default value, added here for the sake of clarity.
	    templateResolver.setTemplateMode("HTML5");
	    // Template cache is true by default. Set to false if you want
	    // templates to be automatically updated when modified.
	    templateResolver.setCacheable(true);
	    return templateResolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine() {
	    // SpringTemplateEngine automatically applies SpringStandardDialect and
	    // enables Spring's own MessageSource message resolution mechanisms.
	    SpringTemplateEngine templateEngine = new SpringTemplateEngine();
	    templateEngine.setTemplateResolver(templateResolver());
	    // Enabling the SpringEL compiler with Spring 4.2.4 or newer can
	    // speed up execution in most scenarios, but might be incompatible
	    // with specific cases when expressions in one template are reused
	    // across different data types, so this flag is "false" by default
	    // for safer backwards compatibility.
	    templateEngine.setEnableSpringELCompiler(true);
	    return templateEngine;
	}
	
	@Bean
	public ThymeleafViewResolver viewResolver(){
	    ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
	    viewResolver.setTemplateEngine(templateEngine());
	    // NOTE 'order' and 'viewNames' are optional
	    viewResolver.setOrder(1);
	    viewResolver.setViewNames(new String[] {".html", ".xhtml"});
	    return viewResolver;
	}

	/**
	 * JedisConnectionFactory getConnectionFactory()
	 *
	 * creates a standard jedis connection for working with a local redis
	 */
	@Bean
	public JedisConnectionFactory getConnectionFactory() {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		jedisConnectionFactory.setHostName("localhost");
		jedisConnectionFactory.setPort(6379);
		jedisConnectionFactory.setPassword("");
		return jedisConnectionFactory;
	}

	/**
	 * defines base redis template
	 *
	 * @return template
	 */
	@Bean(name= "redisTemplate")
	RedisTemplate< String, Object > redisTemplate() {
		final RedisTemplate< String, Object > template = new RedisTemplate< String, Object >();
		template.setConnectionFactory( getConnectionFactory() );
		return template;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LocaleChangeInterceptor());
		registry.addInterceptor(new SessionInterceptor()).addPathPatterns("/auth/**");
	}

}
