package org.openhds.helpers;

/**
 * Created by wolfe on 6/17/15.
 */
public abstract class AbstractTestHelper<T> {

    public abstract T makeValidEntity(String name, String id);

    public abstract T makeInvalidEntity();

}
