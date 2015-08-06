package org.openhds.repository.generator;

/**
 * Created by ben on 8/4/15.
 *
 * Interface for things that can generate or clear sample data, for demo and testing purposes.
 *
 */
public interface DataGenerator {

    void generateData();

    void generateData(int size);

    void clearData();

}
