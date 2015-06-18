package org.openhds.repository.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by Ben on 6/17/15.
 */
public class PageIterator<T> implements Iterator<Page<T>> {

    private final JpaRepository<T, ?> repository;

    private Pageable pageable;

    private Page<T> currentPage;

    public PageIterator(JpaRepository<T, ?> repository, Pageable pageable) {
        this.repository = repository;
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
        currentPage = repository.findAll(pageable);
        pageable = pageable.next();
        return currentPage;
    }
}
