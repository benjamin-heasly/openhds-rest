package org.openhds.repository.contract;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Created by wolfe on 6/12/15.
 */
@NoRepositoryBean
public interface AuditableCollectedRepository<T extends AuditableCollectedEntity> extends AuditableRepository<T>{

}
