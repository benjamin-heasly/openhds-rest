package org.openhds.repository.generator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Ben on 5/18/15.
 * <p>
 * Initialize the db with some OpenHDS sample objects.
 */
@Component
public class MasterDataGenerator implements DataGenerator {

    private final Log log = LogFactory.getLog(this.getClass());

    private List<DataGenerator> dataGenerators = new ArrayList<>();

    @Autowired
    public MasterDataGenerator(RequiredDataGenerator requiredDataGenerator,
                               LocationDataGenerator locationDataGenerator,
                               FamilyDataGenerator familyDataGenerator,
                               UpdateDataGenerator updateDataGenerator,
                               MiscellaneousDataGenerator miscellaneousDataGenerator) {

        // list data generators in order of dependency
        dataGenerators.add(requiredDataGenerator);
        dataGenerators.add(locationDataGenerator);
        dataGenerators.add(familyDataGenerator);
        dataGenerators.add(updateDataGenerator);
        dataGenerators.add(miscellaneousDataGenerator);
    }

    @Override
    public void generateData() {
        for (DataGenerator dataGenerator : dataGenerators) {
            dataGenerator.generateData();
        }
    }

    @Override
    public void generateData(int size) {
        log.info("Generating data with size " + size + ".");
        for (DataGenerator dataGenerator : dataGenerators) {
            log.info("Start generating " + dataGenerator.getClass().getSimpleName());
            dataGenerator.generateData(size);
        }
        log.info("Done generating data.");
    }

    @Override
    public void clearData() {
        log.info("Clearing data!");
        ListIterator<DataGenerator> listIterator = dataGenerators.listIterator(dataGenerators.size());
        while (listIterator.hasPrevious()) {
            listIterator.previous().clearData();
        }
    }
}
