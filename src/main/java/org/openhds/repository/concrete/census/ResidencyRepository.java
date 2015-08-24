package org.openhds.repository.concrete.census;

import org.openhds.domain.model.census.Individual;
import org.openhds.domain.model.census.Location;
import org.openhds.domain.model.census.Residency;
import org.openhds.repository.contract.AuditableCollectedRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by Wolfe on 7/14/2015.
 */
public interface ResidencyRepository extends AuditableCollectedRepository<Residency> {
  Page<Residency> findByDeletedFalseAndIndividual(Individual individual, Pageable pageable);
  Page<Residency> findByDeletedFalseAndLocation(Location location, Pageable pageable);
  Page<Residency> findByDeletedFalseAndIndividualAndLocation(Individual individual, Location location, Pageable pageable);
}
