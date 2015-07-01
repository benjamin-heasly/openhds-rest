package org.openhds.repository.util;

import org.openhds.repository.queries.QueryRange;

import java.time.ZonedDateTime;

/**
 * Created by bsh on 7/1/15.
 */
public class QueryUtil {

    public static QueryRange<ZonedDateTime> dateQueryRange(String propertyName, ZonedDateTime minDate, ZonedDateTime maxDate) {
        // default to errors up until now
        ZonedDateTime rangeMax = ZonedDateTime.now();
        if (maxDate != null) {
            rangeMax = maxDate;
        }

        // default to date range of one week
        ZonedDateTime rangeMin = rangeMax.minusDays(7);
        if (minDate != null) {
            rangeMin = minDate;
        }

        return new QueryRange<>(propertyName, rangeMin, rangeMax);
    }

}
