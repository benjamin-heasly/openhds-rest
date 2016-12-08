package org.openhds.domain.util;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface ExtIdGenerator {
    String suggestNextId(Map<String, Object> data);
    boolean validateExtId(String extId, Map<String, Object> data);
}
