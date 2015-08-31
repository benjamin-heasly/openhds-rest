package org.openhds.domain.contract;

/**
 * Identify entities that can be identified by a uuid.
 *
 * BSH
 */
public interface UuidIdentifiable {

    public final static String UNKNOWN_STATUS = "UNKNOWN_STATUS";

    String getUuid();

    void setUuid(String uuid);

}
