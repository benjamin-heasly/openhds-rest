package org.openhds.repository.results;

import org.openhds.domain.contract.UuidIdentifiable;
import org.openhds.domain.util.ShallowCopier;

/**
 * Created by Ben on 6/23/15.
 *
 * Wrap an existing EntityIterator and convert the results to shallow copies.
 *
 */
public class ShallowCopyIterator<T extends UuidIdentifiable> extends UpdatingIterator<T> {

    public ShallowCopyIterator(EntityIterator<T> original) {
        super(original, ShallowCopyIterator::shallowCopy);
    }

    private static <T extends UuidIdentifiable> T shallowCopy(T entity) {
        return ShallowCopier.makeShallowCopy(entity, null);
    }
}
