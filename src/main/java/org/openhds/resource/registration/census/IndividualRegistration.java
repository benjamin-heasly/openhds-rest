package org.openhds.resource.registration.census;

import org.openhds.domain.model.census.Individual;
import org.openhds.domain.util.Description;
import org.openhds.resource.registration.Registration;

/**
 * Created by Wolfe on 7/13/2015.
 */

@Description(description = "Register an Individual at a Location in the location hierarchy.")
public class IndividualRegistration extends Registration<Individual> {

    private Individual individual;

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

}
