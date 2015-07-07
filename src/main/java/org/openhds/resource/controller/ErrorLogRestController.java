package org.openhds.resource.controller;

import org.openhds.domain.model.FieldWorker;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.queries.QueryRange;
import org.openhds.repository.queries.QueryValue;
import org.openhds.repository.results.EntityIterator;
import org.openhds.resource.contract.AuditableCollectedRestController;
import org.openhds.resource.registration.ErrorLogRegistration;
import org.openhds.service.impl.ErrorLogService;
import org.openhds.service.impl.FieldWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.PagedResources;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.openhds.repository.util.QueryUtil.dateQueryRange;

/**
 * Created by bsh on 6/29/15.
 */
@RestController
@RequestMapping("/errorLogs")
@ExposesResourceFor(ErrorLog.class)
public class ErrorLogRestController extends AuditableCollectedRestController<
        ErrorLog,
        ErrorLogRegistration,
        ErrorLogService> {

    private final ErrorLogService errorLogService;

    private final FieldWorkerService fieldWorkerService;

    @Autowired
    public ErrorLogRestController(ErrorLogService errorLogService,
                                  FieldWorkerService fieldWorkerService) {
        super(errorLogService);
        this.errorLogService = errorLogService;
        this.fieldWorkerService = fieldWorkerService;
    }

    @Override
    protected ErrorLog register(ErrorLogRegistration registration) {
        return errorLogService.createOrUpdate(registration.getErrorLog());
    }

    @Override
    protected ErrorLog register(ErrorLogRegistration registration, String id) {
        registration.getErrorLog().setUuid(id);
        return register(registration);
    }

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public PagedResources findErrorLogs(Pageable pageable, PagedResourcesAssembler assembler,
                                        @RequestParam(value = "resolutionStatus", required = false) String resolutionStatus,
                                        @RequestParam(value = "assignedTo", required = false) String assignedTo,
                                        @RequestParam(value = "fieldWorkerId", required = false) String fieldWorkerId,
                                        @RequestParam(value = "entityType", required = false) String entityType,
                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                        @RequestParam(value = "minDate", required = false) ZonedDateTime minDate,
                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                        @RequestParam(value = "maxDate", required = false) ZonedDateTime maxDate) {

        List<QueryValue> properties = new ArrayList<>();
        addIfPresent(properties, "resolutionStatus", resolutionStatus);
        addIfPresent(properties, "assignedTo", assignedTo);
        addIfPresent(properties, "entityType", entityType);

        if (fieldWorkerId != null && !fieldWorkerId.isEmpty()) {
            EntityIterator<FieldWorker> fieldWorkers =
                    fieldWorkerService.findByFieldWorkerId(new Sort("fieldWorkerId"), fieldWorkerId);
            if (fieldWorkers.iterator().hasNext()) {
                properties.add(new QueryValue("collectedBy", fieldWorkers.iterator().next()));
            }
        }

        QueryRange<ZonedDateTime> dateRange = dateQueryRange("insertDate", minDate, maxDate);
        Page<ErrorLog> errorLogs = errorLogService.findByMultipleValuesRanged(
                pageable,
                dateRange,
                properties.toArray(new QueryValue[properties.size()]));

        return assembler.toResource(errorLogs, entityLinkAssembler);
    }

    private static void addIfPresent(Collection<QueryValue> properties, String propertyName, String property) {
        if (property != null && !property.trim().isEmpty()) {
            properties.add(new QueryValue(propertyName, property));
        }
    }
}
