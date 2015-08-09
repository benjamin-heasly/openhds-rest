package org.openhds.service.impl.census;

import org.openhds.domain.model.census.Residency;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.census.ResidencyRepository;
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
    private IndividualService individualService;

    @Autowired
    private LocationService locationService;

    @Autowired
    public ResidencyService(ResidencyRepository repository) {
        super(repository);
    }

    @Override
    public Residency makePlaceHolder(String id, String name) {
        Residency residency = new Residency();
        residency.setUuid(id);
        residency.setStartDate(ZonedDateTime.now().minusYears(1));
        residency.setStartType(name);
        residency.setIndividual(individualService.getUnknownEntity());
        residency.setLocation(locationService.getUnknownEntity());

        initPlaceHolderCollectedFields(residency);

        return residency;
    }


    public Residency recordResidency (Residency residency, String individualId, String locationId, String fieldWorkerId){
        residency.setIndividual(individualService.findOrMakePlaceHolder(individualId));
        residency.setLocation(locationService.findOrMakePlaceHolder(locationId));
        residency.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));
        return createOrUpdate(residency);
    }

    @Override
    public void validate(Residency entity, ErrorLog errorLog) {
        super.validate(entity, errorLog);
    }
}
