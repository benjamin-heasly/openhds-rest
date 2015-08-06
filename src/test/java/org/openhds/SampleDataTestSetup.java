package org.openhds;

import org.openhds.repository.generator.MasterDataGenerator;
import org.openhds.repository.generator.RequiredDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * Created by ben on 8/5/15.
 *
 * Set up sample data as a fixture for each test method.
 *
 */
@Component
public class SampleDataTestSetup extends AbstractTestExecutionListener {

    @Autowired
    private MasterDataGenerator masterDataGenerator;

    @Autowired
    private RequiredDataGenerator requiredDataGenerator;

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        // for some reason Spring doesn't autowire TestExecutionListeners like it does with regular components
        testContext.getApplicationContext()
                .getAutowireCapableBeanFactory()
                .autowireBean(this);

        // make sure default user exists before the first test
        //  which is tricky because @WithUserDetails advice is invoked *before* the beforeTestMethod method
        requiredDataGenerator.generateData();
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        masterDataGenerator.clearData();
        masterDataGenerator.generateData();
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        // make sure default user exists before the first test
        //  which is tricky because @WithUserDetails advice is invoked *before* the beforeTestMethod method
        requiredDataGenerator.createDefaultUser();
    }
}
