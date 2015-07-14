package org.openhds.service.impl;

import org.openhds.domain.model.Residency;
import org.openhds.repository.concrete.ResidencyRepository;
import org.openhds.service.contract.AbstractAuditableCollectedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

/**
 * Created by Wolfe on 7/14/2015.
 */
@Service
public class ResidencyService extends AbstractAuditableCollectedService<Residency, ResidencyRepository> {

    @Autowired
    private FieldWorkerService fieldWorkerService;

    @Autowired
    private IndividualService individualService;

    @Autowired
    private LocationService locationService;

    @Autowired
    public ResidencyService(ResidencyRepository repository) {
        super(repository);
    }

    @Override
    protected Residency makeUnknownEntity() {
        Residency residency = new Residency();
        residency.setStartDate(ZonedDateTime.now().minusYears(1));
        residency.setStartType("unknown");
        residency.setIndividual(individualService.getUnknownEntity());
        residency.setLocation(locationService.getUnknownEntity());
        residency.setCollectionDateTime(ZonedDateTime.now());
        residency.setCollectedBy(fieldWorkerService.getUnknownEntity());

        return residency;
    }
}
