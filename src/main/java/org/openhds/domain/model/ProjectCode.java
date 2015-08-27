package org.openhds.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.GenericGenerator;
import org.openhds.domain.contract.UuidIdentifiable;
import org.openhds.domain.util.Description;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by bsh on 7/13/15.
 */
@Description(description = "A data code with a consistent name and a value which projects can customize.")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "projectcode")
public class ProjectCode implements UuidIdentifiable, Serializable {


    public static String GENDER = "gender";
    public static String GENDER_MALE = "male";
    public static String GENDER_FEMALE = "female";
    public static String LOCATION_TYPE = "locationType";
    public static String MEMBERSHIP_TYPE = "membershipType";
    public static String SOCIALGROUP_TYPE = "socialGroupType";
    public static String RELATIONSHIP_TYPE = "relationshipType";
    public static String MIGRATION_TYPE = "migrationType";
    public static String MIN_AGE_OF_PREGNANCY = "minAgeOfPregnancy";
    public static String PREGNANCY_RESULT_TYPE = "pregnancyResultType";
    public static String PREGNANCY_RESULT_LIVE_BIRTH = "liveBirth";

    @Id
    @GeneratedValue(generator = "uuidIfMissing")
    @GenericGenerator(name = "uuidIfMissing",
            strategy = "org.openhds.repository.util.IfMissingUuidGenerator")
    @Column(length = 36)
    protected String uuid;

    @Description(description = "The name of this code, which does not change between projects.")
    @NotNull
    @Size(min = 1)
    @Column(unique = true)
    private String codeName;

    @Description(description = "The group this code belongs to, which does not change between projects.")
    @NotNull
    @Size(min = 1)
    private String codeGroup;

    @Description(description = "The value assigned to this code, which may change between projects.")
    @NotNull
    @Size(min = 1)
    private String codeValue;

    @Description(description = "A description of this code.")
    private String description;

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getCodeValue() {
        return codeValue;
    }

    public void setCodeValue(String codeValue) {
        this.codeValue = codeValue;
    }

    public String getCodeGroup() {
        return codeGroup;
    }

    public void setCodeGroup(String codeGroup) {
        this.codeGroup = codeGroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        if (null == uuid) {
            return 0;
        }
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (getClass() != other.getClass()) {
            return false;
        }

        final String otherUuid = ((UuidIdentifiable) other).getUuid();
        return null != uuid && null != otherUuid && uuid.equals(otherUuid);
    }
}
