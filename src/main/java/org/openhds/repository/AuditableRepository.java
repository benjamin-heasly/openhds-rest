package org.openhds.repository;

import org.openhds.domain.contract.AuditableEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

import java.time.ZonedDateTime;

/**
 * Created by wolfe on 6/12/15.
 */
@NoRepositoryBean
public interface AuditableRepository<T extends AuditableEntity> extends UuidIdentifiableRepository<T>{

    Page<T> findByInsertDateBetween(ZonedDateTime insertedSince, ZonedDateTime insertedBefore, Pageable pageable);

}
