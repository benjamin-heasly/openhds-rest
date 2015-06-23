package org.openhds.repository;

/**
 * Created by Ben on 5/4/15.
 */

import org.openhds.security.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends UuidIdentifiableRepository<User> {
    Optional<User> findByUsername(String name);
    List<User> findByFirstName(String name);
    List<User> findByLastName(String name);
}
