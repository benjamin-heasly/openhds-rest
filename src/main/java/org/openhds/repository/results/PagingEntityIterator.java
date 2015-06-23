package org.openhds.repository.results;

import org.openhds.domain.contract.UuidIdentifiable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Ben on 6/23/15.
 *
 * Expose query results a few at a time using Jpa Pageable queries.
 *
 */
public class PagingEntityIterator<T extends UuidIdentifiable> implements EntityIterator<T>, Iterator<T> {

    private final PageIterator<T> pageIterator;

    private String collectionName;

    private Iterator<T> objectIterator;

    public PagingEntityIterator(PageIterator<T> pageIterator) {
        this.pageIterator = pageIterator;

        if (pageIterator.hasNext()) {
            objectIterator = nextObjectIterator();
        }
    }

    @Override
    public List<T> toList() {
        List<T> list = new ArrayList<>();
        for (T entity : this) {
            list.add(entity);
        }
        return list;
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }

    @Override
    public String getCollectionName() {
        return collectionName;
    }

    @Override
    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    @Override
    public boolean hasNext() {
        return null != objectIterator && (objectIterator.hasNext() || pageIterator.hasNext());
    }

    @Override
    public T next() {
        if (!objectIterator.hasNext()) {
            objectIterator = nextObjectIterator();
        }
        return objectIterator.next();
    }

    private Iterator<T> nextObjectIterator() {
        return pageIterator.next().getContent().iterator();
    }
}
