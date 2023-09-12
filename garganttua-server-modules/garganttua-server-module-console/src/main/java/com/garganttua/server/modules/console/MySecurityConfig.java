package com.garganttua.server.modules.console;

/**
 *
 * @author anand
 */
//@EnableWebSecurity
//class MySecurityConfig {
//
//    private UserDetailsService userDetailsService() {
//        return username -> new UserDetails() {
//            private static final long serialVersionUID = 1L;
//
//            @Override
//            public Collection<? extends GrantedAuthority> getAuthorities() {
//                Collection<GrantedAuthority> authorities = new HashSet<>();
//                authorities.add(new SimpleGrantedAuthority("USER"));
//                if (username.equals("admin")) {
//                    authorities.add(new SimpleGrantedAuthority("ADMIN"));
//                }
//                return authorities;
//            }
//
//            @Override
//            public String getPassword() {
//                return username;
//            }
//
//            @Override
//            public String getUsername() {
//                return username;
//            }
//
//            @Override
//            public boolean isAccountNonExpired() {
//                return true;
//            }
//
//            @Override
//            public boolean isAccountNonLocked() {
//                return true;
//            }
//
//            @Override
//            public boolean isCredentialsNonExpired() {
//                return true;
//            }
//
//            @Override
//            public boolean isEnabled() {
//                return true;
//            }
//        };
//    }
//
//    @Bean
//    public AuthenticationProvider authProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService());
//        authProvider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
//        return authProvider;
//    }
//
//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .requestMatchers("/login").permitAll()
//                .anyRequest().authenticated()
//                .and().formLogin().defaultSuccessUrl("/test", true);
//        return http.build();
//    }
//}
