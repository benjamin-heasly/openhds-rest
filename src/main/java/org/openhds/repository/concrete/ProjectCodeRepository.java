package org.openhds.repository.concrete;

/**
 * Created by Ben on 5/4/15.
 */

import org.openhds.domain.model.ProjectCode;
import org.openhds.repository.contract.UuidIdentifiableRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectCodeRepository extends UuidIdentifiableRepository<ProjectCode> {
    Optional<ProjectCode> findByCodeName(String name);
    List<ProjectCode> findByCodeGroup(String group);
    List<ProjectCode> findByCodeGroupAndCodeValue(String group, String value);
}
