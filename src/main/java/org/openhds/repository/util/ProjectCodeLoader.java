package org.openhds.repository.util;

import org.openhds.service.impl.ProjectCodeService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by bsh on 7/15/15.
 * <p>
 * Load any required project codes that are not already loaded.
 * <p>
 * This is expected to work with project-codes.yml and the @Bean yamlMapFactoryBean from OpenHdsRestApplication.
 * <p>
 * The mapFactoryBean is expected to return a map-of-maps with the following structure:
 * - top-level keys point to groups of project codes
 * - groups of codes are maps where each key is a codeName
 * - each codeName points to a final map which contains the code value and description
 */
@Component
public class ProjectCodeLoader {

    private final ProjectCodeService projectCodeService;

    private final FactoryBean<Map<String, Object>> mapFactoryBean;

    @Autowired
    public ProjectCodeLoader(ProjectCodeService projectCodeService,
                             @Qualifier("projectCodeMap") FactoryBean<Map<String, Object>> mapFactoryBean) {
        this.projectCodeService = projectCodeService;
        this.mapFactoryBean = mapFactoryBean;
    }

    // iterate the code groups at the top level
    public void loadAllCodes() {

        Map<String, Object> topMap = null;
        try {
            topMap = mapFactoryBean.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (Map.Entry<String, Object> entry : topMap.entrySet()) {
            loadGroup(entry.getKey(), (Map<String, Object>) entry.getValue());
        }
    }

    // iterate codes within a given group
    private void loadGroup(String groupName, Map<String, Object> groupEntries) {
        for (Map.Entry<String, Object> entry : groupEntries.entrySet()) {
            loadCode(groupName, entry.getKey(), (Map<String, String>) entry.getValue());
        }
    }

    // load a code if it doesn't exist yet
    private void loadCode(String groupName, String codeName, Map<String, String> codeProperties) {
        String codeValue = codeProperties.get("codeValue");
        String description = codeProperties.get("description");
        saveIfNeeded(groupName, codeName, codeValue, description);
    }

    // save a code if it doesn't exist yet
    private void saveIfNeeded(String groupName, String codeName, String codeValue, String description) {
        if (projectCodeService.codeNameExists(codeName)) {
            return;
        }

        projectCodeService.createCode(codeName, codeValue, groupName, description);
    }
}
