package org.openhds.service;

import org.openhds.domain.contract.AuditableEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Created by wolfe on 6/9/15.
 */
public interface OpenHdsService <T extends AuditableEntity> {

    T create (T entity);
    T findOne (String uuid);
    List<T> findAll();
    Page<T> findPaged (Pageable pageable);
    T update (T entity);
    void delete (T entity);
    boolean validate(T entity);

}
