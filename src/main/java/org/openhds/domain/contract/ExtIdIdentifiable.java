package org.openhds.domain.contract;

/**
 * Identify entities that can be identified by an external id.
 *
 * Extends UuidIdentifiable because we never want to deal with something that has only ExtIds -- they are too messy.
 *
 * BSH
 */
public interface ExtIdIdentifiable extends UuidIdentifiable {

    String getExtId();

    void setExtId(String extId);

}
