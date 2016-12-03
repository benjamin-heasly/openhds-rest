package org.openhds.domain.util;

import org.openhds.domain.model.update.Visit;

public class QueryStrategyFactory {
    public static QueryStrategy<Visit> VisitStrategy() {
        return (me, you) -> {
            String locationUuid = me.getLocation().getUuid();
            String otherLocationUuid = you.getLocation().getUuid();
            return locationUuid.equals(otherLocationUuid) &&
                   !me.getUuid().equals(you.getUuid());
        };
    }

}
