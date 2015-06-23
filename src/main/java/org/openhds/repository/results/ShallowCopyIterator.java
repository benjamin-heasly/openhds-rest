package org.openhds.repository.results;

import org.openhds.domain.contract.UuidIdentifiable;
import org.openhds.domain.util.ShallowCopier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Ben on 6/23/15.
 *
 * Wrap an existing EntityIterator and convert the results to shallow copies.
 *
 */
public class ShallowCopyIterator<T extends UuidIdentifiable> implements EntityIterator<T>, Iterator<T> {

    private final EntityIterator<T> original;

    public ShallowCopyIterator(EntityIterator<T> original) {
        this.original = original;
    }

    @Override
    public List<T> toList() {
        List<T> list = new ArrayList<>();
        for (T entity : original) {
            list.add(entity);
        }
        return list;
    }

    @Override
    public String getCollectionName() {
        return original.getCollectionName();
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
        return shallowCopy(original.iterator().next());
    }

    private T shallowCopy(T entity) {
        return ShallowCopier.makeShallowCopy(entity, null);
    }
}
