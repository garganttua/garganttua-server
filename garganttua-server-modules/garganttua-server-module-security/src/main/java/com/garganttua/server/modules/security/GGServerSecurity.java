package com.garganttua.server.modules.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;


@Service
public class GGServerSecurity {
	
	@Value("${com.garganttua.server.security}")
	private boolean securityEnabled;
	
	@Autowired(required = false)
	private List<IGGServerSecurityFilter> filters = new ArrayList<IGGServerSecurityFilter>();

	@Bean
	public SecurityFilterChain configureSecurity(HttpSecurity http) throws Exception {
		
		if( this.securityEnabled) {
			http
			.authorizeHttpRequests()
			.requestMatchers("/swagger-ui.html","/swagger-ui/**").permitAll().and()
			.authorizeHttpRequests();
			
			this.filters.forEach(filter -> {
				filter.configureFilterChain(http);
			});
		} else {
			http.authorizeHttpRequests()
			.requestMatchers("/**").permitAll().and()
			.authorizeHttpRequests();
		}
		
		return http.build();
	}
}
