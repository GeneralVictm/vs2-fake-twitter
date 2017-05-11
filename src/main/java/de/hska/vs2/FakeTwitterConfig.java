package de.hska.vs2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
@ComponentScan(basePackages = { "de.hska.vs2" })
public class FakeTwitterConfig extends WebMvcConfigurerAdapter {

	public FakeTwitterConfig() {
		super();
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
