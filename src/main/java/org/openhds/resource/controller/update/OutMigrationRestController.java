package org.openhds.resource.controller.update;

import org.openhds.domain.model.update.OutMigration;
import org.openhds.resource.contract.AuditableCollectedRestController;
import org.openhds.resource.registration.update.OutMigrationRegistration;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.census.ResidencyService;
import org.openhds.service.impl.update.OutMigrationService;
import org.openhds.service.impl.update.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Ben on 5/18/15.
 */
@RestController
@RequestMapping("/outMigrations")
@ExposesResourceFor(OutMigration.class)
public class OutMigrationRestController extends AuditableCollectedRestController<
        OutMigration,
        OutMigrationRegistration,
        OutMigrationService> {

    private OutMigrationService outMigrationService;

    private final VisitService visitService;

    private final IndividualService individualService;

    private final ResidencyService residencyService;

    private final FieldWorkerService fieldWorkerService;

    @Autowired
    public OutMigrationRestController(OutMigrationService outMigrationService,
                                      VisitService visitService,
                                      IndividualService individualService,
                                      ResidencyService residencyService,
                                      FieldWorkerService fieldWorkerService) {
        super(outMigrationService);
        this.outMigrationService = outMigrationService;
        this.visitService = visitService;
        this.individualService = individualService;
        this.residencyService = residencyService;
        this.fieldWorkerService = fieldWorkerService;
    }

    @Override
    protected OutMigration register(OutMigrationRegistration registration) {
        return outMigrationService.recordOutMigration(registration.getOutMigration(), registration.getIndividualUuid(),
                registration.getResidencyUuid(),
                registration.getVisitUuid(),
                registration.getCollectedByUuid());
    }

    @Override
    protected OutMigration register(OutMigrationRegistration registration, String id) {
        registration.getOutMigration().setUuid(id);
        return register(registration);
    }
}
