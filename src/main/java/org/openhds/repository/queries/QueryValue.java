package org.openhds.repository.queries;

/**
 * Created by Ben on 6/24/15.
 */
public class QueryValue {

    private final String propertyName;

    private final String value;

    public QueryValue(String propertyName, String value) {
        this.propertyName = propertyName;
        this.value = value;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getValue() {
        return value;
    }
}
