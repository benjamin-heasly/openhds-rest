package org.openhds.repository.queries;

/**
 * Created by Ben on 6/24/15.
 */
public class QueryOrder {

    private final String propertyName;

    private final boolean isAscending;

    public QueryOrder(String propertyName, boolean isAscending) {
        this.propertyName = propertyName;
        this.isAscending = isAscending;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public boolean isAscending() {
        return isAscending;
    }
}
