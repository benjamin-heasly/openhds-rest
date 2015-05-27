package org.openhds.domain.contract;

/**
 * Identify entities that can be identified by an external id.
 *
 * BSH
 */
public interface ExtIdIdentifiable {

    String getExtId();

    void setExtId(String extId);

}
