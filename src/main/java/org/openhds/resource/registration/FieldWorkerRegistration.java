package org.openhds.resource.registration;

import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.util.Description;

/**
 * Created by Ben on 6/3/15.
 * <p>
 * Register a FieldWorker who may collect data.
 */
@Description(description = "Register a FieldWorker who may collect data.")
public class FieldWorkerRegistration extends Registration<FieldWorker> {

    private FieldWorker fieldWorker;

    private String password;

    public FieldWorker getFieldWorker() {
        return fieldWorker;
    }

    public void setFieldWorker(FieldWorker fieldWorker) {
        this.fieldWorker = fieldWorker;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
