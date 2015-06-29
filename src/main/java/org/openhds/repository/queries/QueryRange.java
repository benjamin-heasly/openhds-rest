package org.openhds.repository.queries;

/**
 * Created by Ben on 6/24/15.
 */
public class QueryRange<R extends java.lang.Comparable> {

    private final String propertyName;

    private final R min;

    private final R max;

    public QueryRange(String propertyName, R min, R max) {
        this.propertyName = propertyName;
        this.min = min;
        this.max = max;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public R getMin() {
        return min;
    }

    public R getMax() {
        return max;
    }
}
