package org.openhds.repository;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * Created by wolfe on 6/12/15.
 */
@NoRepositoryBean
public interface AuditableCollectedRepository<T extends AuditableCollectedEntity> extends AuditableRepository<T>{

}
