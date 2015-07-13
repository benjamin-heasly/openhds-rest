package org.openhds.resource.registration;

import org.openhds.domain.model.Relationship;

/**
 * Created by Wolfe on 7/13/2015.
 */
public class RelationshipRegistration extends Registration<Relationship> {

    private Relationship relationship;

    private String individualAId;

    private String individualBId;

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public String getIndividualAId() {
        return individualAId;
    }

    public void setIndividualAId(String individualAId) {
        this.individualAId = individualAId;
    }

    public String getIndividualBId() {
        return individualBId;
    }

    public void setIndividualBId(String individualBId) {
        this.individualBId = individualBId;
    }
}
