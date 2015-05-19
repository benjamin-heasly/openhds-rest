package org.openhds.resource;

import org.openhds.domain.Bookmark;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import java.security.Principal;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by Ben on 5/18/15.
 */
class BookmarkLinkInfo extends ResourceSupport {

    private final Bookmark bookmark;

    public BookmarkLinkInfo(Bookmark bookmark, Principal principal) {
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
