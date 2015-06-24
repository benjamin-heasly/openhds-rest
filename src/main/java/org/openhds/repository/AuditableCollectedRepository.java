package org.openhds.repository;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.model.FieldWorker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by wolfe on 6/12/15.
 */
@NoRepositoryBean
public interface AuditableCollectedRepository<T extends AuditableCollectedEntity> extends AuditableRepository<T>{

    Page<T> findByDeletedFalseAndCollectedBy(FieldWorker fieldWorker, Pageable pageable);


    Page<T> findByDeletedFalseAndCollectionDateTimeBetween(ZonedDateTime collectedAfter, ZonedDateTime collectedBefore, Pageable pageable);
    Page<T> findByDeletedFalseAndCollectionDateTimeAfter(ZonedDateTime collectedAfter, Pageable pageable);
    Page<T> findByDeletedFalseAndCollectionDateTimeBefore(ZonedDateTime collectedBefore, Pageable pageable);

}
