package org.openhds.repository.queries;

import org.openhds.domain.contract.AuditableEntity;
import org.openhds.domain.model.census.Location;
import org.openhds.domain.model.census.LocationHierarchy;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by ben on 8/18/15.
 */
public class LocationSpecifications {

    public interface LocationSpecification<T extends AuditableEntity> {
        Specification<T> getSpecification(final List<LocationHierarchy> enclosing,
                                          final QueryRange<ZonedDateTime> dateRange);
    }

    public static Specification<Location> enclosedLocations(final List<LocationHierarchy> enclosing,
                                                            final QueryRange<ZonedDateTime> dateRange) {
        return new Specification<Location>() {
            @Override
            public Predicate toPredicate(Root<Location> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.and(root.get("locationHierarchy").in(enclosing),
                        Specifications.inRange(root, cb, dateRange));
            }
        };
    }
}
