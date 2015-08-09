package org.openhds.resource.controller.update;

import org.openhds.domain.model.update.InMigration;
import org.openhds.resource.contract.AuditableCollectedRestController;
import org.openhds.resource.registration.update.InMigrationRegistration;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.census.ResidencyService;
import org.openhds.service.impl.update.InMigrationService;
import org.openhds.service.impl.update.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Ben on 5/18/15.
 */
@RestController
@RequestMapping("/inMigrations")
@ExposesResourceFor(InMigration.class)
public class InMigrationRestController extends AuditableCollectedRestController<
        InMigration,
        InMigrationRegistration,
        InMigrationService> {

    private InMigrationService inMigrationService;

    private final VisitService visitService;

    private final IndividualService individualService;

    private final ResidencyService residencyService;

    private final FieldWorkerService fieldWorkerService;

    @Autowired
    public InMigrationRestController(InMigrationService inMigrationService,
                                     VisitService visitService,
                                     IndividualService individualService,
                                     ResidencyService residencyService,
                                     FieldWorkerService fieldWorkerService) {
        super(inMigrationService);
        this.inMigrationService = inMigrationService;
        this.visitService = visitService;
        this.individualService = individualService;
        this.residencyService = residencyService;
        this.fieldWorkerService = fieldWorkerService;
    }

    @Override
    protected InMigration register(InMigrationRegistration registration) {
        return inMigrationService.recordInMigration(registration.getInMigration(),
                registration.getIndividualUuid(),
                registration.getResidencyUuid(),
                registration.getVisitUuid(),
                registration.getCollectedByUuid());
    }

    @Override
    protected InMigration register(InMigrationRegistration registration, String id) {
        registration.getInMigration().setUuid(id);
        return register(registration);
    }
}
