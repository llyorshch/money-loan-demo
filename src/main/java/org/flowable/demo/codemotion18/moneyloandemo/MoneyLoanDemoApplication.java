package org.flowable.demo.codemotion18.moneyloandemo;

import org.flowable.rest.security.BasicAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
public class MoneyLoanDemoApplication {

	private static final Logger logger = LoggerFactory.getLogger(MoneyLoanDemoApplication.class);
	private final MoneyLoanSetupService moneyLoanService;

	@Autowired
	public MoneyLoanDemoApplication(MoneyLoanSetupService moneyLoanSetupService) {
		this.moneyLoanService = moneyLoanSetupService;
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext applicationContext) {
		return args -> {
			logger.info("Starting Money Loan Demo application configuration process.");
			moneyLoanService.setupMoneyLoanApplication();
		};
	}

	@Order(99)
    @Configuration
    static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
					.antMatcher("/api/**")
					.csrf().disable()
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .httpBasic();
        }

        @Bean
        public FilterRegistrationBean corsFilter() {
           UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
           CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
           config.addAllowedMethod("*");
           source.registerCorsConfiguration("/**", config);
           FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
           bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
           return bean;
        }
	}
	
	@Configuration
    @EnableWebSecurity
    public static class SecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Bean
        public AuthenticationProvider authenticationProvider() {
            return new BasicAuthenticationProvider();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .authenticationProvider(authenticationProvider())
                .csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .httpBasic();
        }
    }

	public static void main(String[] args) {
		SpringApplication.run(MoneyLoanDemoApplication.class, args);
	}

}