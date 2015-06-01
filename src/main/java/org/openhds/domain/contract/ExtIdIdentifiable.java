package org.openhds.domain.contract;

/**
 * Identify entities that can be identified by an external id.
 *
 * Extends UuidIdentifiable because we never want to deal with something that has only ExtIds.
 * ExtIds get too messy because they may change and may be duplicated.
 * Requiring Uuids simplifies our own reasoning as humans.  It also simplifies the code.
 *
 * BSH
 */
public interface ExtIdIdentifiable extends UuidIdentifiable {

    String getExtId();

    void setExtId(String extId);

}
