package org.openhds.events.model;

import org.openhds.domain.contract.AuditableEntity;
import org.openhds.domain.util.Description;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Description(description = "Collection of atomic OpenHDS system events")
@Entity
@Table(name = "events", indexes={
        @Index(columnList = "deleted"),
        @Index(columnList = "lastModifiedDate"),
        @Index(columnList = "entityType")})
public class Event extends AuditableEntity implements Serializable {

    public static final String DEFAULT_ACTION = "default-action";
    public static final String DEFAULT_ENTITY = "default-entity";
    public static final String DEFAULT_SYSTEM = "default-system";
    public static final String PERSIST_ACTION = "persist-action";
    public static final String DEFAULT_STATUS = "unread";
    public static final String READ_STATUS = "read";

    @NotNull(message = "event action type may not be null")
    private String actionType;

    @NotNull(message = "event entity type may not be null")
    private String entityType;

    @Column(length=65535)
    private String eventData;

    private static final long serialVersionUID = 1L;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = EventMetadata.class)
    @JoinColumn(name = "event_uuid")
    private List<EventMetadata> eventMetadata = new ArrayList<>();

    public Event() { }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEventData() {
        return eventData;
    }

    public void setEventData(String eventData) {
        this.eventData = eventData;
    }

    public List<EventMetadata> getEventMetadata() {
        return eventMetadata;
    }

    public void setEventMetadata(List<EventMetadata> eventMetadata) {
        this.eventMetadata = eventMetadata;
    }

    public EventMetadata findMetadataForSystem(String system) {
        if (null == eventMetadata) {
            return null;
        }

        for (EventMetadata metadata : eventMetadata) {
            if (metadata.getSystem().equals(system)) {
                return metadata;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "Event{" +
                "actionType='" + actionType + '\'' +
                ", entityType='" + entityType + '\'' +
                ", eventData='" + eventData + '\'' +
                ", eventMetadata=" + eventMetadata +
                "} " + super.toString();
    }
}
