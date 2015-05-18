package org.openhds.domain.model;

/**
 * Identify entities that can be identified by a uuid.
 *
 * BSH
 */
public interface UuidIdentifiable {

    public String getUuid();

    public void setUuid(String uuid);

}
