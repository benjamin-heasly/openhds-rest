package org.openhds.repository;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.contract.AuditableExtIdEntity;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * Created by wolfe on 6/12/15.
 */
@NoRepositoryBean
public interface AuditableExtIdRepository<T extends AuditableExtIdEntity> extends AuditableCollectedRepository<T>, ExtIdentifiableRepository<T>{


}
