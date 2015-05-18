package org.openhds.security;

import org.openhds.domain.Account;
import org.openhds.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Created by Ben on 5/18/15.
 */
@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return (username) -> accountRepository.findByUsername(username)
                .map((Account a) -> new User(a.username, a.password, true, true, true, true,
                        AuthorityUtils.createAuthorityList("ROLE_USER", "write")))
                .orElseThrow(() -> new UsernameNotFoundException("could not find the user '" + username + "'"));
    }
}
