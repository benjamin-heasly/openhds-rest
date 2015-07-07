package org.openhds.repository.contract;

import org.openhds.domain.contract.AuditableEntity;
import org.openhds.security.model.User;
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
    Page<T> findByDeletedFalseAndVoidDateBetween(ZonedDateTime insertedAfter, ZonedDateTime insertedBefore, Pageable pageable);
    Page<T> findByDeletedFalseAndVoidDateAfter(ZonedDateTime insertedAfter, Pageable pageable);
    Page<T> findByDeletedFalseAndVoidDateBefore(ZonedDateTime insertedBefore, Pageable pageable);
    Page<T> findByDeletedFalseAndLastModifiedDateBetween(ZonedDateTime modifiedAfter, ZonedDateTime modifiedBefore, Pageable pageable);
    Page<T> findByDeletedFalseAndLastModifiedDateAfter(ZonedDateTime modifiedAfter, Pageable pageable);
    Page<T> findByDeletedFalseAndLastModifiedDateBefore(ZonedDateTime modifiedBefore, Pageable pageable);

    Page<T> findByDeletedFalseAndInsertBy(User user, Pageable pageable);
    Page<T> findByDeletedFalseAndVoidBy(User user, Pageable pageable);
    Page<T> findByDeletedFalseAndLastModifiedBy(User user, Pageable pageable);

    // for auditing
    Page<T> findByDeletedTrue(Pageable pageable);

}
