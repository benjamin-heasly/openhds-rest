package org.openhds.bookmarks;

/**
 * Created by Ben on 5/4/15.
 */
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;


public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Collection<Bookmark> findByAccountUsername(String username);
}
