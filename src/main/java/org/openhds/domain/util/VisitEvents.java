package org.openhds.domain.util;

import org.openhds.domain.model.update.*;

import java.util.List;

/**
 * Created by motech on 11/12/16.
 */
public class VisitEvents {
    public List<InMigration> inMigrations;
    public List<OutMigration> outMigrations;
    public List<Death> deaths;
    public List<PregnancyObservation> pregnancyObservations;
    public List<PregnancyOutcome> pregnancyOutcomes;

    public VisitEvents(List<InMigration> inMigrations, List<OutMigration> outMigrations,
                           List<Death> deaths, List<PregnancyObservation> pregnancyObservations,
                           List<PregnancyOutcome> pregnancyOutcomes ){
        this.inMigrations = inMigrations;
        this.outMigrations = outMigrations;
        this.deaths = deaths;
        this.pregnancyObservations = pregnancyObservations;
        this.pregnancyOutcomes = pregnancyOutcomes;
    }
}
