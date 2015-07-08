package org.openhds.repository.results;

import org.openhds.domain.contract.UuidIdentifiable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Ben on 6/23/15.
 *
 * Wrap an existing EntityIterator and invoke an update callback for each next().
 *
 */
public class UpdatingIterator<T extends UuidIdentifiable> implements EntityIterator<T>, Iterator<T> {

    public interface Updater<T> {
        T update(T entity);
    }

    private final EntityIterator<T> original;

    private final Updater<T> updater;

    public UpdatingIterator(EntityIterator<T> original, Updater<T> updater) {
        this.original = original;
        this.updater = updater;
    }

    @Override
    public List<T> toList() {
        List<T> list = new ArrayList<>();
        for (T entity : original) {
            list.add(updater.update(entity));
        }
        return list;
    }

    @Override
    public String getCollectionName() {
        return original.getCollectionName();
    }

    @Override
    public void setCollectionName(String collectionName) {
        original.setCollectionName(collectionName);
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return original.iterator().hasNext();
    }

    @Override
    public T next() {
        return updater.update(original.iterator().next());
    }
}
