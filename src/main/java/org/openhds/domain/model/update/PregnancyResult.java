package org.openhds.domain.model.update;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.model.census.Individual;
import org.openhds.domain.util.Description;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by Wolfe on 7/14/2015.
 */

@Description(description = "A pregnancy result is recorded within a pregnancy outcome (i.e. stillborn, birth, etc).")
@Entity
@Table(name = "PregnancyResult", indexes={@Index(columnList = "lastModifiedDate")})
public class PregnancyResult extends AuditableCollectedEntity implements Serializable{

    private static final long serialVersionUID = -3113139461022105832L;

    @NotNull(message = "PregnancyResult cannot have a null type.")
    @Description(description="Pregnancy outcome type.")
    private String type;

    @OneToOne
    @Description(description="The child that of the pregnancy.")
    private Individual child;

    @NotNull(message = "A PregnancyResult cannot have a null pregnancyOutome.")
    @ManyToOne
    @Description(description = "The pregnancyOutcome that this pregnancy result came from.")
    private PregnancyOutcome pregnancyOutcome;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Individual getChild() {
        return child;
    }

    public void setChild(Individual child) {
        this.child = child;
    }

    public PregnancyOutcome getPregnancyOutcome() {
        return pregnancyOutcome;
    }

    public void setPregnancyOutcome(PregnancyOutcome pregnancyOutcome) {
        this.pregnancyOutcome = pregnancyOutcome;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof PregnancyResult)) {
            return false;
        }

        final String otherUuid = ((PregnancyResult) other).getUuid();
        return null != uuid && null != otherUuid && uuid.equals(otherUuid);
    }

    @Override
    public String toString() {
        return "PregnancyResult{" +
                "type='" + type + '\'' +
                ", child=" + child +
                ", pregnancyOutcome=" + pregnancyOutcome +
                "} " + super.toString();
    }
}
