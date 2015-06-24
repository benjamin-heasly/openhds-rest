package org.openhds.repository.queries;

/**
 * Created by Ben on 6/24/15.
 */
public class QueryRange {

    private final String propertyName;

    private final String min;

    private final String max;

    public QueryRange(String propertyName, String min, String max) {
        this.propertyName = propertyName;
        this.min = min;
        this.max = max;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getMin() {
        return min;
    }

    public String getMax() {
        return max;
    }
}
