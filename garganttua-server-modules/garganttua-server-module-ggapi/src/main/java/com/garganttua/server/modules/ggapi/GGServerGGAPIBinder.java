package com.garganttua.server.modules.ggapi;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Service;

import com.garganttua.api.security.IGGAPISecurityHelper;
import com.garganttua.server.modules.security.IGGServerSecurityFilter;

@Service
@ConditionalOnProperty(name = "com.garganttua.api.security", havingValue = "enabled", matchIfMissing = true)
public class GGServerGGAPIBinder implements IGGServerSecurityFilter {

	@Autowired
	private Optional<IGGAPISecurityHelper> securityHelper;

	@Override
	public HttpSecurity configureFilterChain(HttpSecurity http) {

		this.securityHelper.ifPresent(s -> {
			s.configureFilterChain(null);
		});
		return http;
	}
}
