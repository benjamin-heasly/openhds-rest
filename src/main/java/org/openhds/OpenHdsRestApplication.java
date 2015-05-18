package org.openhds;

import org.openhds.domain.Account;
import org.openhds.domain.Bookmark;
import org.openhds.repository.AccountRepository;
import org.openhds.repository.BookmarkRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Ben on 5/4/15.
 */
@SpringBootApplication
@EnableJpaRepositories
public class OpenHdsRestApplication {

    @Bean
    public CommandLineRunner initBookmarks(AccountRepository accountRepository, BookmarkRepository bookmarkRepository) {
        return (evt) -> Arrays.asList("jhoeller,dsyer,pwebb,ogierke,rwinch,mfisher,mpollack,jlong".split(","))
                .forEach(a -> {
                    Account account = accountRepository.save(new Account(a, "password"));
                    bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/1/" + a, "A description"));
                    bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/2/" + a, "A description"));
                });
    }

    @Bean
    public CommandLineRunner initOpenHDS() {
        return new ProofOfConceptInitializer();
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

    public static void main(String[] args) {
        SpringApplication.run(OpenHdsRestApplication.class, args);
    }
}

