package org.openhds.security;

import org.apache.catalina.filters.CorsFilter;
import org.openhds.SimpleCORSFilter;
import org.openhds.repository.concrete.UserRepository;
import org.openhds.security.model.UserDetailsWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Ben on 5/18/15.
 */
@Configuration
public class WebSecurityConfiguration {

    @Bean
    PasswordEncoder passwordEncoder() {
        // strong, slow hashing in production
        return new BCryptPasswordEncoder(12);
    }

    @Configuration
    public static class UserDetailsServiceConfig extends GlobalAuthenticationConfigurerAdapter {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService())
                    .passwordEncoder(passwordEncoder);
        }

        @Bean
        public UserDetailsService userDetailsService() {
            return (username) -> userRepository.findByUsername(username)
                    .map(UserDetailsWrapper::new)
                    .orElseThrow(() -> new UsernameNotFoundException("No such user: " + username));
        }
    }

    @Configuration
    public static class RequestSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            httpSecurity.authorizeRequests()
                    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .and()
                    .addFilterBefore(new SimpleCORSFilter(), ChannelProcessingFilter.class)
                    .authorizeRequests()
                    .and()
                    .authorizeRequests()
                    .antMatchers("/**").hasRole("USER")
                    .and()
                    .httpBasic()
                    .and()
                    .csrf().disable();
        }
    }

    @Configuration
    public static class FilterRegistrationConfig {

        @Value("${permitted.urls}")
        String permittedUrls;

        //TODO: This needs tests
        private boolean isAllowed(String origin) {
            // Allow from everywhere if not specified
            return permittedUrls == null
                    || origin != null
                    && Arrays.stream(permittedUrls.split(","))
                    .filter(
                            addr -> addr.trim().toLowerCase().equals(origin.toLowerCase())
                    ).findFirst()
                    .isPresent();
        }

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

                    final String origin = ((HttpServletRequest) servletRequest).getHeader("Origin");
                    if(isAllowed(origin)) {
                        response.setHeader("Access-Control-Allow-Origin", origin);
                    } else {
                        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
                    }

                    // TODO: are these the verbs and headers we really want?
                    response.setHeader("Access-Control-Allow-Methods", "POST,PUT,GET,OPTIONS,DELETE");
                    response.setHeader("Access-Control-Max-Age", Long.toString(60 * 60));
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                    response.setHeader("Access-Control-Allow-Headers",
                            "Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization");

                    // always allow OPTIONS but filter other verbs
                    if ("OPTIONS" .equals(request.getMethod())) {
                        response.setStatus(200);
                    }
                    filterChain.doFilter(servletRequest, servletResponse);
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