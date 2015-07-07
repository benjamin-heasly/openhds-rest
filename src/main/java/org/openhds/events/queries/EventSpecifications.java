package org.openhds.events.queries;

import org.openhds.events.model.Event;
import org.openhds.events.model.EventMetadata;
import org.openhds.repository.queries.QueryRange;
import org.openhds.repository.queries.QueryValue;
import org.openhds.repository.queries.Specifications;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

/**
 * Created by bsh on 6/30/15.
 */
public class EventSpecifications {

    // Query events by properties and date range, never queries by given system.
    public static <R extends Comparable> Specification<Event> multiValueRangedWithoutSystem (
            final QueryRange<R> queryRange,
            final QueryValue[] queryValues,
            final String system) {

        return new Specification<Event>() {
            @Override
            public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                return cb.and(
                        Specifications.allValuesEqual(root, cb, queryValues),
                        Specifications.inRange(root, cb, queryRange),
                        noExistingMetadataForSystem(root, query, cb, system));
            }
        };
    }

    // Query events by properties and date range, never queries by given system.
    public static <R extends Comparable> Specification<Event> multiValueRangedMatchingValues(
            final QueryRange<R> queryRange,
            final QueryValue[] queryValues,
            final QueryValue[] metadataQueryValues) {

        return new Specification<Event>() {
            @Override
            public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                return cb.and(
                        Specifications.allValuesEqual(root, cb, queryValues),
                        Specifications.inRange(root, cb, queryRange),
                        existingMetadataAllValuesEqual(root, query, cb, metadataQueryValues));
            }
        };
    }

    public static Predicate noExistingMetadataForSystem(Root<Event> root,
                                                        CriteriaQuery<?> query,
                                                        CriteriaBuilder cb,
                                                        String system) {

        Join<Event, EventMetadata> metadataJoin = root.join("eventMetadata");
        Subquery<EventMetadata> subquery = query.subquery(EventMetadata.class);
        subquery.from(EventMetadata.class);
        subquery.select(metadataJoin);
        subquery.where(cb.equal(metadataJoin.get("system"), system));
        return cb.not(cb.exists(subquery));
    }

    public static Predicate existingMetadataAllValuesEqual(Root<Event> root,
                                                           CriteriaQuery<?> query,
                                                           CriteriaBuilder cb,
                                                           QueryValue[] metadataQueryValues) {

        Join<Event, EventMetadata> metadataJoin = root.join("eventMetadata");
        Subquery<EventMetadata> subquery = query.subquery(EventMetadata.class);
        subquery.from(EventMetadata.class);
        subquery.select(metadataJoin);
        subquery.where(Specifications.allValuesEqual(metadataJoin, cb, metadataQueryValues));
        return cb.exists(subquery);
    }

}
