package org.openhds.security;

import org.openhds.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Ben on 5/18/15.
 */
@Configuration
public class WebSecurityConfiguration {

    @Configuration
    public static class UserDetailsServiceConfig extends GlobalAuthenticationConfigurerAdapter {

        @Autowired
        private UserRepository userRepository;

        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService());
        }

        @Bean
        public UserDetailsService userDetailsService() {
            return (username) -> userRepository.findByUsername(username)
                    .map((org.openhds.security.model.User u) -> new User(
                            u.getUsername(),
                            u.getPassword(),
                            true, true, true, true,
                            AuthorityUtils.createAuthorityList(u.getPrivilegeNames().toArray(new String[0]))))
                    .orElseThrow(() -> new UsernameNotFoundException("No such user: " + username));
        }
    }

    @Configuration
    public static class RequestSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            httpSecurity.authorizeRequests()
                    .antMatchers("/**").hasRole("USER")
                    .and()
                    .httpBasic()
                    .and()
                    .csrf().disable();
        }
    }


    @Configuration
    public static class FilterRegistrationConfig {

        @Bean
        public FilterRegistrationBean corsFilter() {
            return new FilterRegistrationBean(new Filter() {
                @Override
                public void doFilter(ServletRequest servletRequest,
                                     ServletResponse servletResponse,
                                     FilterChain filterChain) throws IOException, ServletException {
                    // always assume HTTP
                    HttpServletRequest request = (HttpServletRequest) servletRequest;
                    HttpServletResponse response = (HttpServletResponse) servletResponse;

                    // TODO: for now, allow all origins
                    response.setHeader("Access-Control-Allow-Origin", "*");

                    // TODO: are these the verbs and headers we really want?
                    response.setHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,DELETE");
                    response.setHeader("Access-Control-Max-Age", Long.toString(60 * 60));
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                    response.setHeader("Access-Control-Allow-Headers",
                            "Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization");

                    // always allow OPTIONS but filter other verbs
                    if ("OPTIONS".equals(request.getMethod())) {
                        response.setStatus(HttpStatus.OK.value());
                    } else {
                        filterChain.doFilter(servletRequest, servletResponse);
                    }
                }

                @Override
                public void init(FilterConfig filterConfig) {
                }

                @Override
                public void destroy() {
                }
            });
        }
    }
}