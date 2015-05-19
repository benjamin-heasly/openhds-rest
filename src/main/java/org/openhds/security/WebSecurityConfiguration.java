package org.openhds.security;

import org.openhds.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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
}