package org.openhds.service.impl;

import org.openhds.domain.model.FieldWorker;
import org.openhds.repository.concrete.FieldWorkerRepository;
import org.openhds.service.contract.AbstractAuditableService;
import org.springframework.beans.factory.annotation.Autowired;
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


}
