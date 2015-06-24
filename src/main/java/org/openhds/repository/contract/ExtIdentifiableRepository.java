package org.openhds.repository.contract;

import org.openhds.domain.contract.ExtIdIdentifiable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * Created by wolfe on 6/16/15.
 */
@NoRepositoryBean
public interface ExtIdentifiableRepository <T extends ExtIdIdentifiable> extends JpaRepository<T, String> {

    List<T> findByExtId(String extId);

}
