package org.openhds.domain.util;

public interface QueryStrategy<T> {
    public boolean dependsOn(T me, T you);
}
