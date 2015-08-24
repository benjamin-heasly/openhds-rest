package org.openhds.repository.concrete.census;

/**
 * Created by Ben on 5/4/15.
 */

import org.openhds.domain.model.census.Individual;
import org.openhds.domain.model.census.Membership;
import org.openhds.domain.model.census.SocialGroup;
import org.openhds.repository.contract.AuditableCollectedRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MembershipRepository extends AuditableCollectedRepository<Membership> {
  Page<Membership> findByDeletedFalseAndIndividual(Individual individual, Pageable pageable);
  Page<Membership> findByDeletedFalseAndSocialGroup(SocialGroup socialGroup, Pageable pageable);
  Page<Membership> findByDeletedFalseAndIndividualAndSocialGroup(Individual individual, SocialGroup socialGroup, Pageable pageable);

}
