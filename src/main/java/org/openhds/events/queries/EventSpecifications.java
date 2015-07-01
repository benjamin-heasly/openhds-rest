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

    // Query events by actionType, entityType, date range, system, and status
    public static <R extends Comparable> Specification<Event> multiValueRangedBySystemAndStatus(
            final String system,
            final String status,
            final QueryRange<R> queryRange,
            final QueryValue... queryValues) {

        return new Specification<Event>() {
            @Override
            public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Join<Event, EventMetadata> metadataJoin = root.join("eventMetadata");
                return cb.and(Specifications.allValuesEqualOrNull(root, cb, queryValues),
                        Specifications.inRangeOrNull(root, cb, queryRange),
                        cb.or(noEventMetadataForSystem(metadataJoin, query, cb, system),
                                eventMetadataForSystemWithStatus(metadataJoin, query, cb, system, status)));
            }
        };
    }

    public static Predicate noEventMetadataForSystem(Join<Event, EventMetadata> metadataJoin,
                                                     CriteriaQuery<?> query,
                                                     CriteriaBuilder cb,
                                                     String system) {
        Subquery<EventMetadata> subquery = query.subquery(EventMetadata.class);
        subquery.where(cb.equal(metadataJoin.get("system"), system));
        return cb.exists(subquery).not();
    }

    public static Predicate eventMetadataForSystemWithStatus(Join<Event, EventMetadata> metadataJoin,
                                                             CriteriaQuery<?> query,
                                                             CriteriaBuilder cb,
                                                             String system,
                                                             String status) {
        Subquery<EventMetadata> subquery = query.subquery(EventMetadata.class);
        subquery.where(cb.and(cb.equal(metadataJoin.get("system"), system),
                cb.equal(metadataJoin.get("status"), status)));
        return cb.exists(subquery);
    }

}
