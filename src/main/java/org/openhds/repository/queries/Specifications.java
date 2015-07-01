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

    public static <R extends java.lang.Comparable> Predicate inRange(Root<?> root, CriteriaBuilder cb, QueryRange<R> queryRange) {
        return cb.between(root.<R>get(queryRange.getPropertyName()), queryRange.getMin(), queryRange.getMax());
    }

    public static <R extends java.lang.Comparable> Predicate inRangeOrNull(Root<?> root, CriteriaBuilder cb, QueryRange<R> queryRange) {
        Path<R> path = root.<R>get(queryRange.getPropertyName());
        return cb.or(cb.isNull(path),
                cb.between(path, queryRange.getMin(), queryRange.getMax()));
    }

    public static Predicate allValuesEqual(Root<?> root, CriteriaBuilder cb, QueryValue... queryValues) {
        List<Predicate> predicates = new ArrayList<>();
        for (QueryValue queryValue : queryValues) {
            predicates.add(cb.equal(root.get(queryValue.getPropertyName()), queryValue.getValue()));
        }
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    public static Predicate allValuesEqualOrNull(Root<?> root, CriteriaBuilder cb, QueryValue... queryValues) {
        List<Predicate> predicates = new ArrayList<>();
        for (QueryValue queryValue : queryValues) {
            Path<?> path = root.get(queryValue.getPropertyName());
            predicates.add(cb.or(cb.isNull(path),
                    cb.equal(path, queryValue.getValue())));
        }
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
    }

}
