package org.openhds.repository.concrete;

/**
 * Created by Ben on 5/4/15.
 */

import org.openhds.security.model.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrivilegeRepository extends JpaRepository<Privilege, String> {
    Optional<Privilege> findByGrant(Privilege.Grant grant);
}
