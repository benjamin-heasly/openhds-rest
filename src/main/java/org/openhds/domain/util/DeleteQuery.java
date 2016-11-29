package org.openhds.domain.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.openhds.domain.model.update.Visit;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DeleteQuery {
    private final Map<String, Visit> entities = Maps.newHashMap();

    public void addEntity(final Visit entity) {
        entities.put(entity.getUuid(), entity);
    }

    public Set<Visit> getDependentEntities(String deletableEntityUuid) {
        Visit entity = entities.get(deletableEntityUuid);

        return entities.entrySet()
                .stream()
                .filter(e -> e.getValue().getLocation().equals(entity.getLocation()))
                .map(Map.Entry::getValue)
                .filter(v -> entity.compareTo(v) < 0)
                .collect(Collectors.toSet());
    }
}
