package org.openhds.repository.results;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by Ben on 6/17/15.
 */
public class PageIterator<T> implements Iterator<Page<T>> {

    public interface PagedQueryable<T> {
        Page<T> query(Pageable pageable);
    }

    private final PagedQueryable<T> pagedQueryable;

    private Pageable pageable;

    private Page<T> currentPage;

    public PageIterator(PagedQueryable<T> pagedQueryable, Pageable pageable) {
        this.pagedQueryable = pagedQueryable;
        this.pageable = pageable.first();
    }

    @Override
    public boolean hasNext() {
        return null == currentPage || currentPage.hasNext();
    }

    @Override
    public Page<T> next() {
        if (null == currentPage) {
            return readNextPage();
        }

        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        return readNextPage();
    }

    private Page<T> readNextPage() {
        currentPage = pagedQueryable.query(pageable);
        pageable = pageable.next();
        return currentPage;
    }

}
