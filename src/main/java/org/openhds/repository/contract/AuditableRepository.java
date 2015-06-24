package org.openhds.repository.contract;

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

    // instead of findOne()
    T findByDeletedFalseAndUuid(String id);

    // instead of findAll(Pageable)
    Page<T> findByDeletedFalse(Pageable pageable);

    Page<T> findByDeletedFalseAndInsertDateBetween(ZonedDateTime insertedAfter, ZonedDateTime insertedBefore, Pageable pageable);
    Page<T> findByDeletedFalseAndInsertDateAfter(ZonedDateTime insertedAfter, Pageable pageable);
    Page<T> findByDeletedFalseAndInsertDateBefore(ZonedDateTime insertedBefore, Pageable pageable);

    // for auditing
    Page<T> findByDeletedTrue(Pageable pageable);

}