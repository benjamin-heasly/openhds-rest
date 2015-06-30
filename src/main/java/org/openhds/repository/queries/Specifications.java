package org.openhds.repository.queries;

import org.openhds.domain.contract.UuidIdentifiable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
    public static <T extends UuidIdentifiable> Specification<T> rangedMultiValue(final QueryRange queryRange, final QueryValue... queryValues) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.and(
                        inRange(root, cb, queryRange),
                        allValuesEqual(root, cb, queryValues));
            }
        };
    }

    private static <R extends java.lang.Comparable> Predicate inRange(Root<?> root, CriteriaBuilder cb, QueryRange<R> queryRange) {
        return cb.between(root.<R>get(queryRange.getPropertyName()), queryRange.getMin(), queryRange.getMax());
    }

    private static Predicate allValuesEqual(Root<?> root, CriteriaBuilder cb, QueryValue... queryValues) {
        List<Predicate> predicates = new ArrayList<>();
        for (QueryValue queryValue : queryValues) {
            predicates.add(cb.equal(root.get(queryValue.getPropertyName()), queryValue.getValue()));
        }
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
    }

}
