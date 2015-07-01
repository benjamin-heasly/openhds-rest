package org.openhds.repository.contract;

import org.openhds.domain.contract.AuditableExtIdEntity;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Created by wolfe on 6/12/15.
 *
 * ExtId logic is held in the ExtIdentifialRepository
 *
 */
@NoRepositoryBean
public interface AuditableExtIdRepository<T extends AuditableExtIdEntity>
        extends AuditableCollectedRepository<T>, ExtIdentifiableRepository<T> {



}
