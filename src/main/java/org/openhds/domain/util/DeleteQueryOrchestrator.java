package org.openhds.domain.util;

import org.openhds.domain.model.update.Visit;
import org.openhds.repository.concrete.update.VisitRepository;
import org.openhds.service.impl.update.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeleteQueryOrchestrator {

    private VisitService visitService;

    @Autowired
    public DeleteQueryOrchestrator(VisitService visitService) {
        this.visitService = visitService;
    }

    /**
     * @param id VisitUUID
     * @param reason Reason for deletion
     * @return If this cannot delete the visit, returns the list of dependent visits that must be deleted first. Otherwise returns an empty list.
     */
    public List<Visit> deleteVisit(String id, String reason) {
        DeleteQuery<Visit, VisitRepository> query = new DeleteQuery<>(visitService, QueryStrategyFactory.VisitStrategy());
        List<Visit> visits = query.dependsOn(id);
        if (visits.isEmpty()) {
            visitService.delete(id, reason);
        }
        return visits;
    }
}
