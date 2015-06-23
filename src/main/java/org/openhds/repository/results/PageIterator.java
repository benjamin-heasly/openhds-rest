package org.openhds.repository.results;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by Ben on 6/17/15.
 */
public class PageIterator<T> implements Iterator<Page<T>> {

    public static final int DEFAULT_PAGE_SIZE = 1000;

    public interface PagedQueryable<T> {
        Page<T> query(Pageable pageable);
    }

    private final PagedQueryable<T> pagedQueryable;

    private Pageable pageable;

    private Page<T> currentPage;

    public PageIterator(PagedQueryable<T> pagedQueryable, Sort sort, int pageSize) {
        this.pagedQueryable = pagedQueryable;
        this.pageable = new PageRequest(0, pageSize, sort);
    }

    public PageIterator(PagedQueryable<T> pagedQueryable, Sort sort) {
        this(pagedQueryable, sort, DEFAULT_PAGE_SIZE);
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
