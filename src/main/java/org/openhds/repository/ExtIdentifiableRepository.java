package org.openhds.repository;

import org.openhds.domain.contract.ExtIdIdentifiable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by wolfe on 6/16/15.
 */
public interface ExtIdentifiableRepository <T extends ExtIdIdentifiable> extends JpaRepository<T, String> {

}

