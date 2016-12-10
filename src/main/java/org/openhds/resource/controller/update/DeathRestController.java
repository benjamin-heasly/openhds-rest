package org.openhds.resource.controller.update;

import org.openhds.domain.model.update.Death;
import org.openhds.resource.contract.AuditableCollectedRestController;
import org.openhds.resource.registration.update.DeathRegistration;
import org.openhds.service.contract.AbstractUuidService;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.update.DeathService;
import org.openhds.service.impl.update.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by Wolfe on 7/14/2015.
 */
@RestController
@RequestMapping("/deaths")
@ExposesResourceFor(Death.class)
public class DeathRestController extends AuditableCollectedRestController<
        Death,
        DeathRegistration,
        DeathService> {

    private final FieldWorkerService fieldWorkerService;

    private final VisitService visitService;

    private final IndividualService individualService;

    private final DeathService deathService;


    @Autowired
    public DeathRestController(DeathService deathService,
                               FieldWorkerService fieldWorkerService,
                               VisitService visitService,
                               IndividualService individualService) {
        super(deathService);
        this.deathService = deathService;
        this.fieldWorkerService = fieldWorkerService;
        this.visitService = visitService;
        this.individualService = individualService;
    }

    @Override
    protected DeathRegistration makeSampleRegistration(Death entity) {
        DeathRegistration registration = new DeathRegistration();
        registration.setDeath(entity);
        registration.setIndividualUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setVisitUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        return registration;
    }

    @Override
    protected Death register(DeathRegistration registration) {
        checkRegistrationFields(registration.getDeath(), registration);
        return deathService.recordDeath(registration.getDeath(),
                registration.getIndividualUuid(),
                registration.getVisitUuid(),
                registration.getCollectedByUuid());
    }

    @Override
    protected Death register(DeathRegistration registration, String id) {
        registration.getDeath().setUuid(id);
        return register(registration);
    }


    @RequestMapping(value = "/submitEdited/{id}", method = RequestMethod.PUT)
    public ResponseEntity editInMigration(@PathVariable String id, @RequestBody Map<String,String> deathStub) {

        Death death = deathService.findOne(id);
        if(death == null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        if(deathStub.containsKey("placeOfDeath")){
            death.setDeathPlace(deathStub.get("placeOfDeath"));
        }

        if(deathStub.containsKey("cause")){
            death.setDeathCause(deathStub.get("cause"));
        }



        deathService.recordDeath(death, death.getIndividual().getUuid(), death.getVisit().getUuid(),
                                                           death.getCollectedBy().getFieldWorkerId());

        return new ResponseEntity(HttpStatus.CREATED);
    }
}
