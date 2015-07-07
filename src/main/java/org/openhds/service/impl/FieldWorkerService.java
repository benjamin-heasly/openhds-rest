package org.openhds.service.impl;

import org.openhds.domain.model.FieldWorker;
import org.openhds.repository.concrete.FieldWorkerRepository;
import org.openhds.repository.results.EntityIterator;
import org.openhds.service.contract.AbstractAuditableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Created by wolfe on 6/23/15.
 */
@Service
public class FieldWorkerService extends AbstractAuditableService<FieldWorker, FieldWorkerRepository> {

    @Autowired
    public FieldWorkerService(FieldWorkerRepository repository) {
        super(repository);
    }

    @Override
    protected FieldWorker makeUnknownEntity() {
        FieldWorker fieldWorker = new FieldWorker();
        fieldWorker.setFieldWorkerId("unknown");
        fieldWorker.setPasswordHash("unknown");
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
}
