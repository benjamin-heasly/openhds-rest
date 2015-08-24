package org.openhds.repository.concrete.census;

import org.openhds.domain.model.census.Individual;
import org.openhds.domain.model.census.Relationship;
import org.openhds.repository.contract.AuditableCollectedRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by Wolfe on 7/13/2015.
 */
public interface RelationshipRepository extends AuditableCollectedRepository<Relationship>{
  Page<Relationship> findByDeletedFalseAndIndividualA(Individual individualA, Pageable pageable);
  Page<Relationship> findByDeletedFalseAndIndividualB(Individual individualB, Pageable pageable);
  Page<Relationship> findByDeletedFalseAndIndividualAAndIndividualB(Individual individualA, Individual individualB, Pageable pageable);
}
