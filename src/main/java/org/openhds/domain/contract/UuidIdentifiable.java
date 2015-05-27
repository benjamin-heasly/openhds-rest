package org.openhds.domain.contract;

/**
 * Identify entities that can be identified by a uuid.
 *
 * BSH
 */
public interface UuidIdentifiable {

    String getUuid();

    void setUuid(String uuid);

}
