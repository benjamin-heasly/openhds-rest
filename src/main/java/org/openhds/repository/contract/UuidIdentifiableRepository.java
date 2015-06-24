package org.openhds.repository.contract;

import org.openhds.domain.contract.UuidIdentifiable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Created by wolfe on 6/12/15.
 */
@NoRepositoryBean
public interface UuidIdentifiableRepository<T extends UuidIdentifiable> extends JpaRepository<T, String>{

}
