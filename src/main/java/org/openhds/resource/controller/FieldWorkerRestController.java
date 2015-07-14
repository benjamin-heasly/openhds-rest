package org.openhds.resource.controller;

import org.openhds.domain.model.FieldWorker;
import org.openhds.resource.contract.AuditableRestController;
import org.openhds.resource.registration.FieldWorkerRegistration;
import org.openhds.service.impl.FieldWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Ben on 5/18/15.
 */
@RestController
@RequestMapping("/fieldWorkers")
@ExposesResourceFor(FieldWorker.class)
public class FieldWorkerRestController extends AuditableRestController<
        FieldWorker,
        FieldWorkerRegistration,
        FieldWorkerService> {

    private final FieldWorkerService fieldWorkerService;

    @Autowired
    public FieldWorkerRestController(FieldWorkerService fieldWorkerService) {
        super(fieldWorkerService);
        this.fieldWorkerService = fieldWorkerService;
    }

    @Override
    protected FieldWorker register(FieldWorkerRegistration registration) {
        return fieldWorkerService.createOrUpdate(registration.getFieldWorker());
    }

    @Override
    protected FieldWorker register(FieldWorkerRegistration registration, String id) {
        registration.getFieldWorker().setUuid(id);
        return register(registration);
    }
}
