package org.openhds.repository.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Ben on 5/18/15.
 * <p>
 * Initialize the db with some OpenHDS sample objects.
 */
@Component
public class SampleDataGenerator {

    @Autowired
    private RequiredDataGenerator requiredDataGenerator;

    @Autowired
    private LocationDataGenerator locationDataGenerator;

    @Autowired
    private FamilyDataGenerator familyDataGenerator;

    @Autowired
    private UpdateDataGenerator updateDataGenerator;

    @Autowired
    private MiscellaneousDataGenerator miscellaneousDataGenerator;

    public void clearData() {
        miscellaneousDataGenerator.clearData();
        updateDataGenerator.clearData();
        familyDataGenerator.clearData();
        locationDataGenerator.clearData();
        requiredDataGenerator.clearData();
    }

    public void generateSampleData() {
        requiredDataGenerator.generateData();
        locationDataGenerator.generateData(1);
        familyDataGenerator.generateData();
        updateDataGenerator.generateData();
        miscellaneousDataGenerator.generateData();
    }
}
