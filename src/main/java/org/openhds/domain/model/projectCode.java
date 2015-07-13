package org.openhds.domain.model;

import org.hibernate.annotations.GenericGenerator;
import org.openhds.domain.contract.UuidIdentifiable;
import org.openhds.domain.util.Description;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by bsh on 7/13/15.
 */
public class ProjectCode implements UuidIdentifiable, Serializable {

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

    @Description(description = "The value assigned to this code, which may change between projects.")
    @NotNull
    @Size(min = 1)
    private String codeValue;

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
}
