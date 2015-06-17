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

    Page<T> findByInsertDateBetween(ZonedDateTime insertedAfter, ZonedDateTime insertedBefore, Pageable pageable);
    Page<T> findByInsertDateAfter(ZonedDateTime insertedAfter, Pageable pageable);
    Page<T> findByInsertDateBefore(ZonedDateTime insertedBefore, Pageable pageable);

}
