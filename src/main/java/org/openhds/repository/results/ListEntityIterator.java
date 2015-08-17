package org.openhds.repository.results;

import org.openhds.domain.contract.UuidIdentifiable;

import java.util.Iterator;
import java.util.List;

/**
 * Created by ben on 8/17/15.
 */
public class ListEntityIterator<T extends UuidIdentifiable> implements EntityIterator<T> {

    private final List<T> list;

    private String collectionName;

    public ListEntityIterator(List<T> list) {
        this.list = list;
    }

    @Override
    public List<T> toList() {
        return list;
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
    public Iterator<T> iterator() {
        return list.iterator();
    }
}
