package org.openhds;

import org.openhds.bookmarks.Account;
import org.openhds.bookmarks.AccountRepository;
import org.openhds.bookmarks.Bookmark;
import org.openhds.bookmarks.BookmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


@SpringBootApplication
public class OpenhdsRestApplication {

    @Bean
    CommandLineRunner init(AccountRepository accountRepository, BookmarkRepository bookmarkRepository) {
        return (evt) -> Arrays.asList("jhoeller,dsyer,pwebb,ogierke,rwinch,mfisher,mpollack,jlong".split(","))
                .forEach(a -> {
                    Account account = accountRepository.save(new Account(a, "password"));
                    bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/1/" + a, "A description"));
                    bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/2/" + a, "A description"));
                });
    }

    @Bean
    FilterRegistrationBean corsFilter() {
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
        SpringApplication.run(OpenhdsRestApplication.class, args);
    }
}

@RestController
@RequestMapping("/bookmarks")
class BookmarkRestController {

    private final BookmarkRepository bookmarkRepository;

    private final AccountRepository accountRepository;

    @Autowired
    BookmarkRestController(BookmarkRepository bookmarkRepository, AccountRepository accountRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.accountRepository = accountRepository;
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(@NotNull Principal principal, @RequestBody Bookmark input) {
        return this.accountRepository
                .findByUsername(principal.getName())
                .map(account -> {
                    Bookmark result = bookmarkRepository.save(new Bookmark(account, input.uri, input.description));
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                            .buildAndExpand(result.getId()).toUri());
                    return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);})
                .get();
    }

    @RequestMapping(value = "/{bookmarkId}", method = RequestMethod.GET)
    BookmarkResource readBookmark(@NotNull Principal principal, @PathVariable Long bookmarkId) {
        return new BookmarkResource(this.bookmarkRepository.findOne(bookmarkId), principal);
    }

    @RequestMapping(method = RequestMethod.GET)
    Resources<BookmarkResource> readBookmarks(@NotNull Principal principal) {
        String userId = principal.getName();
        List<BookmarkResource> bookmarkResourceList = bookmarkRepository.findByAccountUsername(userId)
                .stream()
                .map(b -> new BookmarkResource(b, principal))
                .collect(Collectors.toList());
        return new Resources<>(bookmarkResourceList);
    }
}

@ControllerAdvice
class BookmarkControllerAdvice {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    VndErrors generalException(Exception ex) {
        return new VndErrors("general error: ", ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    VndErrors userNotFoundExceptionHandler(UsernameNotFoundException ex) {
        return new VndErrors("user not found: ", ex.getMessage());
    }
}

class BookmarkResource extends ResourceSupport {

    private final Bookmark bookmark;

    public BookmarkResource(Bookmark bookmark, Principal principal) {
        String username = bookmark.getAccount().getUsername();
        this.bookmark = bookmark;
        this.add(new Link(bookmark.getUri(), "bookmark-uri"));
        this.add(linkTo(BookmarkRestController.class, username).withRel("bookmarks"));
        this.add(linkTo(methodOn(BookmarkRestController.class, username).readBookmark(principal, bookmark.getId())).withSelfRel());
    }

    public Bookmark getBookmark() {
        return bookmark;
    }
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    AccountRepository accountRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
    }

    @Bean
    UserDetailsService userDetailsService() {
        return (username) -> accountRepository.findByUsername(username)
                .map((Account a) -> new User(a.username, a.password, true, true, true, true,
                        AuthorityUtils.createAuthorityList("ROLE_USER", "write")))
                .orElseThrow(() -> new UsernameNotFoundException("could not find the user '" + username + "'"));
    }
}
