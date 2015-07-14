package org.openhds.resource.registration;

import org.openhds.domain.model.census.Relationship;

/**
 * Created by Wolfe on 7/13/2015.
 */
public class RelationshipRegistration extends Registration<Relationship> {

    private Relationship relationship;

    private String individualAUuid;

    private String individualBUuid;

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public String getIndividualAUuid() {
        return individualAUuid;
    }

    public void setIndividualAUuid(String individualAUuid) {
        this.individualAUuid = individualAUuid;
    }

    public String getIndividualBUuid() {
        return individualBUuid;
    }

    public void setIndividualBUuid(String individualBUuid) {
        this.individualBUuid = individualBUuid;
    }
}
