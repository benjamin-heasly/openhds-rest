package org.openhds.repository.concrete;

/**
 * Created by Ben on 5/4/15.
 */

import org.openhds.repository.contract.UuidIdentifiableRepository;
import org.openhds.security.model.User;

import java.util.Optional;

public interface UserRepository extends UuidIdentifiableRepository<User> {
    Optional<User> findByUsername(String name);
}
