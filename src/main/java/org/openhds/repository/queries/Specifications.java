package org.openhds.repository.queries;

import org.openhds.domain.contract.AuditableEntity;
import org.openhds.domain.contract.UuidIdentifiable;
import org.openhds.domain.model.census.LocationHierarchy;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 6/24/15.
 */
public class Specifications {

    public interface LocationSpecification<T extends AuditableEntity> {
        Specification<T> getSpecification(final List<LocationHierarchy> enclosing);
    }


    // Query for matching multiple property values.
    public static <T extends UuidIdentifiable> Specification<T> multiValue(final QueryValue... queryValues) {
        return (root, query, cb) -> allValuesEqual(root, cb, queryValues);
    }

    // Query a property in some range.
    public static <T extends UuidIdentifiable, R extends Comparable> Specification<T> inRange(final QueryRange<R> queryRange) {
        return (root, query, cb) -> propertyInRange(root, cb, queryRange);
    }

    public static <T extends UuidIdentifiable, R extends Comparable> Specification<T> withRange(final Specification<T> original,
                                                                                                final QueryRange<R> queryRange) {
        return org.springframework.data.jpa.domain.Specifications.where(original).and(inRange(queryRange));
    }

    // Query for a property in some range, and matching multiple other property values.
    public static <T extends UuidIdentifiable, R extends Comparable> Specification<T> rangedMultiValue(final QueryRange<R> queryRange,
                                                                                                       final QueryValue... queryValues) {
        return  withRange(multiValue(queryValues), queryRange);
    }

    public static <R extends java.lang.Comparable> Predicate propertyInRange(Path<?> root, CriteriaBuilder cb, QueryRange<R> queryRange) {

        if(null != queryRange.getMax() && null != queryRange.getMin()){

            return cb.between(root.<R>get(queryRange.getPropertyName()), queryRange.getMin(), queryRange.getMax());

        } else if (null != queryRange.getMax()) {

            return cb.lessThanOrEqualTo(root.<R>get(queryRange.getPropertyName()), queryRange.getMax());

        } else if (null != queryRange.getMin()) {

            return cb.greaterThanOrEqualTo(root.<R>get(queryRange.getPropertyName()), queryRange.getMin());

        } else {

            return cb.and();

        }
    }

    public static Predicate allValuesEqual(Path<?> root, CriteriaBuilder cb, QueryValue... queryValues) {
        List<Predicate> predicates = new ArrayList<>();
        for (QueryValue queryValue : queryValues) {
            predicates.add(cb.equal(root.get(queryValue.getPropertyName()), queryValue.getValue()));
        }
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
