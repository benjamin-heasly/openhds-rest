package org.openhds.repository;

import org.openhds.domain.contract.AuditableEntity;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Created by wolfe on 6/12/15.
 */
@NoRepositoryBean
public interface AuditableRepository<T extends AuditableEntity> extends UuidIdentifiableRepository<T>{

}
