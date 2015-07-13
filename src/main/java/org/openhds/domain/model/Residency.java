package org.openhds.domain.model;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.util.Description;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by bsh on 7/13/15.
 */
@Description(description = "A record of an Individual living at a Location for some time interval.")
@Entity
@Table(name = "residency")
public class Residency extends AuditableCollectedEntity {

}
