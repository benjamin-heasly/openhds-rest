package org.openhds.service.impl;

import org.openhds.domain.model.FieldWorker;
import org.openhds.service.AuditableServiceTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Wolfe on 7/13/2015.
 */
public class FieldWorkerServiceTest extends AuditableServiceTest<FieldWorker, FieldWorkerService> {

    @Override
    protected FieldWorker makeInvalidEntity() {
        return new FieldWorker();
    }

    @Override
    @Autowired
    protected void initialize(FieldWorkerService service) {
        this.service = service;
    }
}
