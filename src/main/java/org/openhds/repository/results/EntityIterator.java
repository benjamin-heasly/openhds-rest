package org.openhds.repository.results;

import org.openhds.domain.contract.UuidIdentifiable;

import java.util.List;

/**
 * Created by Ben on 6/23/15.
 *
 * Represent results sets from queries.
 *
 * For large queries, we want to iterate results without loading them all into memory.  So use Iterable methods.
 *
 * For small queries, handy conversion to list.
 *
 * For convenience (e.g. constructing responses to clients) report the name of the collection that was queried.
 *
 * EntityIterator could be implemented using paged queries, Jpa Pageables, Hibernate Scrollable queries, or some other
 * mechanism.  The caller shouldn't have to care.  So we provide this interface to abstract away those details.
 *
 */
public interface EntityIterator<T extends UuidIdentifiable> extends Iterable<T> {

    List<T> toList();

    String getCollectionName();

}
