package org.openhds.resource.controller.update;

import org.openhds.domain.model.census.SocialGroup;
import org.openhds.domain.model.update.InMigration;
import org.openhds.resource.contract.AuditableCollectedRestController;
import org.openhds.resource.registration.update.InMigrationRegistration;
import org.openhds.service.contract.AbstractUuidService;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.census.ResidencyService;
import org.openhds.service.impl.update.InMigrationService;
import org.openhds.service.impl.update.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    protected InMigrationRegistration makeSampleRegistration(InMigration entity) {
        InMigrationRegistration registration = new InMigrationRegistration();
        registration.setInMigration(entity);
        registration.setVisitUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setIndividualUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setResidencyUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        return registration;
    }

    @Override
    protected InMigration register(InMigrationRegistration registration) {
        checkRegistrationFields(registration.getInMigration(), registration);
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

    @RequestMapping(value = "/submitEdited/{id}", method = RequestMethod.PUT)
    public ResponseEntity editInMigration(@PathVariable String id, @RequestBody Map<String,String> inMigrationStub) {

        InMigration inMig = inMigrationService.findOne(id);
        if(inMig == null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        if(inMigrationStub.containsKey("origin")){
            inMig.setOrigin(inMigrationStub.get("origin"));
        }

        if(inMigrationStub.containsKey("reason")){
            inMig.setReason(inMigrationStub.get("reason"));
        }


        inMigrationService.recordInMigration(inMig, inMig.getIndividual().getUuid(), inMig.getResidency().getUuid(),
                                             inMig.getVisit().getUuid(), inMig.getCollectedBy().getFieldWorkerId());
        return new ResponseEntity(HttpStatus.CREATED);
    }

}
