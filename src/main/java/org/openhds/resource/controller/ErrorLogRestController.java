package org.openhds.resource.controller;

import org.openhds.domain.model.FieldWorker;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.ErrorLogRepository;
import org.openhds.repository.concrete.UserRepository;
import org.openhds.repository.queries.QueryRange;
import org.openhds.repository.queries.QueryValue;
import org.openhds.repository.results.EntityIterator;
import org.openhds.resource.contract.AuditableCollectedRestController;
import org.openhds.resource.registration.ErrorLogRegistration;
import org.openhds.service.impl.ErrorLogService;
import org.openhds.service.impl.FieldWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolationException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsh on 6/29/15.
 */
@RestController
@RequestMapping("/errorLogs")
@ExposesResourceFor(ErrorLog.class)
public class ErrorLogRestController extends AuditableCollectedRestController<ErrorLog, ErrorLogRegistration> {

    private final ErrorLogService errorLogService;

    private final FieldWorkerService fieldWorkerService;

    private final UserRepository userRepository;

    @Autowired
    public ErrorLogRestController(ErrorLogRepository errorLogRepository,
                                  ErrorLogService errorLogService,
                                  FieldWorkerService fieldWorkerService,
                                  UserRepository userRepository) {
        super(errorLogRepository);
        this.errorLogService = errorLogService;
        this.fieldWorkerService = fieldWorkerService;
        this.userRepository = userRepository;
    }

    @Override
    protected ErrorLog register(ErrorLogRegistration registration) {
        // TODO: this seems like service stuff
        ErrorLog errorLog = registration.getErrorLog();

        if (null == errorLog.getDataPayload()) {
            throw new ConstraintViolationException("Error log payload must not be null.", null);
        }

        errorLog.setInsertBy(userRepository.findAll().get(0));
        errorLog.setCollectedBy(fieldWorkerService.findOne(registration.getCollectedByUuid()));
        errorLog.setInsertDate(ZonedDateTime.now());

        return errorLogService.createOrUpdate(registration.getErrorLog());
    }

    @Override
    protected ErrorLog register(ErrorLogRegistration registration, String id) {
        registration.getErrorLog().setUuid(id);
        return register(registration);
    }

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public EntityIterator<ErrorLog> getErrors(@RequestParam(value="resolutionStatus", required=false) String resolutionStatus,
                                       @RequestParam(value="assignedTo", required=false) String assignedTo,
                                       @RequestParam(value="fieldWorkerExtId", required=false) String fieldWorkerExtId,
                                       @RequestParam(value="entityType", required=false) String entityType,
                                       @RequestParam(value="minDate", required = false) ZonedDateTime minDate,
                                       @RequestParam(value = "maxDate", required = false) ZonedDateTime maxDate) {

        List<QueryValue> properties = new ArrayList<>();

        if (resolutionStatus != null && !resolutionStatus.isEmpty()) {
            properties.add(new QueryValue("resolutionStatus", resolutionStatus));
        }

        if (assignedTo != null && !assignedTo.isEmpty()) {
            properties.add(new QueryValue("assignedTo", assignedTo));
        }

        if (entityType != null && !entityType.isEmpty()) {
            properties.add(new QueryValue("entityType", entityType));
        }

        if (fieldWorkerExtId != null && !fieldWorkerExtId.isEmpty()) {
            EntityIterator<FieldWorker> fieldWorkers = fieldWorkerService.findByMultipleValues(
                    null,
                    new QueryValue("extId", fieldWorkerExtId));
            if (fieldWorkers.iterator().hasNext()) {
                properties.add(new QueryValue("collectedBy", fieldWorkers.iterator().next()));
            }
        }

        // default to errors up until now
        ZonedDateTime rangeMax = ZonedDateTime.now();
        if (minDate != null) {
            rangeMax = maxDate;
        }

        // default to date range of one week
        ZonedDateTime rangeMin = rangeMax.minusDays(7);
        if (minDate != null) {
            rangeMin = minDate;
        }

        QueryRange<ZonedDateTime> dateRange = new QueryRange<>("insertDate", rangeMin, rangeMax);
        return errorLogService.findByMultipleValuesRanged(
                new Sort("insertDate"),
                dateRange,
                properties.toArray(new QueryValue[properties.size()]));
    }
}
