package org.openhds.domain.model;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.util.Description;

import javax.persistence.*;

/**
 * Created by bsh on 7/13/15.
 */
@Description(description = "A record of two Individuals having some relationship for some time interval.")
@Entity
@Table(name = "relationship")
public class Relationship extends AuditableCollectedEntity {


    @ManyToOne
    @JoinColumn(name = "individualA")
    @Description(description = "One of the individuals participating in the relationship.")
    private Individual individualA;

    @ManyToOne
    @JoinColumn(name = "individualB")
    @Description(description = "One of the individuals participating in the relationship.")
    private Individual individualB;

}
