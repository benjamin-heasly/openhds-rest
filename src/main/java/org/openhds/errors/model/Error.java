package org.openhds.errors.model;

import org.hibernate.annotations.GenericGenerator;
import org.openhds.domain.contract.UuidIdentifiable;
import org.openhds.domain.util.Description;

import javax.persistence.*;
import java.io.Serializable;

@Description(description = "An individual error.")
@Entity
@Table(name = "error")
public class Error implements UuidIdentifiable, Serializable {

    @Id
    @GeneratedValue(generator = "uuidIfMissing")
    @GenericGenerator(name = "uuidIfMissing",
            strategy = "org.openhds.repository.util.IfMissingUuidGenerator")
    @Column(length = 36)
    protected String uuid;


    private static final long serialVersionUID = 1L;

    private String errorMessage;

    public Error() { }

    public Error(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
