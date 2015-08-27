package org.openhds.service.impl;

import org.openhds.domain.model.FieldWorker;
import org.openhds.repository.concrete.FieldWorkerRepository;
import org.openhds.repository.results.EntityIterator;
import org.openhds.service.contract.AbstractAuditableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Created by wolfe on 6/23/15.
 */
@Service
public class FieldWorkerService extends AbstractAuditableService<FieldWorker, FieldWorkerRepository> {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public FieldWorkerService(FieldWorkerRepository repository) {
        super(repository);
    }

    @Override
    public FieldWorker makePlaceHolder(String id, String name) {
        FieldWorker fieldWorker = new FieldWorker();
        fieldWorker.setUuid(id);
        fieldWorker.setEntityStatus(name);
        fieldWorker.setFieldWorkerId(name);
        fieldWorker.setPasswordHash(name);
        return fieldWorker;
    }

    public EntityIterator<FieldWorker> findByFieldWorkerId(Sort sort, String fieldWorkerId) {
        return iteratorFromPageable(pageable -> repository.findByDeletedFalseAndFieldWorkerId(fieldWorkerId, pageable), sort);
    }

    public EntityIterator<FieldWorker> findByFirstName(Sort sort, String firstName) {
        return iteratorFromPageable(pageable -> repository.findByDeletedFalseAndFirstName(firstName, pageable), sort);
    }

    public EntityIterator<FieldWorker> findByLastName(Sort sort, String lastName) {
        return iteratorFromPageable(pageable -> repository.findByDeletedFalseAndLastName(lastName, pageable), sort);
    }

    public FieldWorker recordFieldWorker(FieldWorker fieldWorker, String password) {
        fieldWorker.setPasswordHash(passwordEncoder.encode(password));
        fieldWorker.setEntityStatus(fieldWorker.NORMAL_STATUS);
        return createOrUpdate(fieldWorker);
    }
}
