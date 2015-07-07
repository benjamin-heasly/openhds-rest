package org.openhds.repository.queries;

import org.openhds.domain.contract.UuidIdentifiable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 6/24/15.
 */
public class Specifications {

    // Query for matching multiple property values.
    public static <T extends UuidIdentifiable> Specification<T> multiValue(final QueryValue... queryValues) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return allValuesEqual(root, cb, queryValues);
            }
        };
    }

    // Query for a property in some range, and matching multiple other property values.
    public static <T extends UuidIdentifiable, R extends Comparable> Specification<T> rangedMultiValue(
            final QueryRange<R> queryRange, final QueryValue... queryValues) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.and(
                        inRange(root, cb, queryRange),
                        allValuesEqual(root, cb, queryValues));
            }
        };
    }

    public static <R extends java.lang.Comparable> Predicate inRange(Path<?> root, CriteriaBuilder cb, QueryRange<R> queryRange) {

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
