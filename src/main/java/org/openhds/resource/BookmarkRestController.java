package org.openhds.resource;

import org.openhds.domain.Bookmark;
import org.openhds.repository.AccountRepository;
import org.openhds.repository.BookmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Ben on 5/18/15.
 */
@RestController
@RequestMapping("/bookmarks")
class BookmarkRestController {

    private final BookmarkRepository bookmarkRepository;

    private final AccountRepository accountRepository;

    @Autowired
    public BookmarkRestController(BookmarkRepository bookmarkRepository, AccountRepository accountRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.accountRepository = accountRepository;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> add(@NotNull Principal principal, @RequestBody Bookmark input) {
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
    public BookmarkHateoasSupport readBookmark(@NotNull Principal principal, @PathVariable Long bookmarkId) {
        return new BookmarkHateoasSupport(this.bookmarkRepository.findOne(bookmarkId), principal);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Resources<BookmarkHateoasSupport> readBookmarks(@NotNull Principal principal) {
        String userId = principal.getName();
        List<BookmarkHateoasSupport> bookmarkHateoasSupportList = bookmarkRepository.findByAccountUsername(userId)
                .stream()
                .map(b -> new BookmarkHateoasSupport(b, principal))
                .collect(Collectors.toList());
        return new Resources<>(bookmarkHateoasSupportList);
    }
}
