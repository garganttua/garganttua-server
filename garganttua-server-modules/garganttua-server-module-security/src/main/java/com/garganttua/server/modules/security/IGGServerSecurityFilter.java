package com.garganttua.server.modules.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface IGGServerSecurityFilter {

	HttpSecurity configureFilterChain(HttpSecurity http);

}
