package io.jmix.appsettings;


import io.jmix.appsettings.test_entity.TestAppSettingsEntity;
import io.jmix.core.UnconstrainedDataManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = AppSettingsTestConfiguration.class)
class AppSettingsTest {

    @Autowired
    private AppSettings appSettings;

    @Autowired
    private UnconstrainedDataManager dataManager;

    @AfterEach
    public void doCleanup() {
        List<TestAppSettingsEntity> testEntities = dataManager.load(TestAppSettingsEntity.class).all().list();
        if (!testEntities.isEmpty()) {
            dataManager.remove(testEntities.get(0));
        }
    }

    @Test
    void testGetDefaultValuesForNotExistedAppSettings() {
        //ensure there is no records in data store
        Assertions.assertTrue(dataManager.load(TestAppSettingsEntity.class).all().list().isEmpty());

        //ensure default values are returned without actual record in database
        TestAppSettingsEntity testAppSettingsEntity = appSettings.load(TestAppSettingsEntity.class);
        Assertions.assertEquals(1, testAppSettingsEntity.getId());
        Assertions.assertTrue(testAppSettingsEntity.getTestBooleanValue());
        Assertions.assertEquals(123, testAppSettingsEntity.getTestIntegerValue());
        Assertions.assertEquals(100500L, testAppSettingsEntity.getTestLongValue());
        Assertions.assertEquals(3.1415926535, testAppSettingsEntity.getTestDoubleValue());
        Assertions.assertEquals("defVal", testAppSettingsEntity.getTestStringValue());
    }

    @Test
    void testGetChangedValuesForAppSettings() {
        //ensure there is no records in data store
        Assertions.assertTrue(dataManager.load(TestAppSettingsEntity.class).all().list().isEmpty());

        TestAppSettingsEntity appSettingsEntity = dataManager.create(TestAppSettingsEntity.class);
        appSettingsEntity.setTestIntegerValue(410);
        appSettingsEntity.setTestStringValue("defValChanged");
        appSettings.save(appSettingsEntity);

        List<TestAppSettingsEntity> dbSettingsEntities = dataManager.load(TestAppSettingsEntity.class).all().list();
        Assertions.assertEquals(1, dbSettingsEntities.size());

        TestAppSettingsEntity testAppSettingsEntity = appSettings.load(TestAppSettingsEntity.class);
        Assertions.assertEquals(1, testAppSettingsEntity.getId());
        Assertions.assertTrue(testAppSettingsEntity.getTestBooleanValue());
        Assertions.assertNull(dbSettingsEntities.get(0).getTestBooleanValue());
        Assertions.assertEquals(410, testAppSettingsEntity.getTestIntegerValue());
        Assertions.assertEquals(410, dbSettingsEntities.get(0).getTestIntegerValue());
        Assertions.assertEquals(100500L, testAppSettingsEntity.getTestLongValue());
        Assertions.assertNull(dbSettingsEntities.get(0).getTestLongValue());
        Assertions.assertEquals(3.1415926535, testAppSettingsEntity.getTestDoubleValue());
        Assertions.assertNull(dbSettingsEntities.get(0).getTestDoubleValue());
        Assertions.assertEquals("defValChanged", testAppSettingsEntity.getTestStringValue());
        Assertions.assertEquals("defValChanged", dbSettingsEntities.get(0).getTestStringValue());

        testAppSettingsEntity.setTestBooleanValue(false);
        testAppSettingsEntity.setTestLongValue(500100L);
        testAppSettingsEntity.setTestDoubleValue(2.7182818284);
        testAppSettingsEntity.setTestStringValue("access denied");
        appSettings.save(testAppSettingsEntity);

        dbSettingsEntities = dataManager.load(TestAppSettingsEntity.class).all().list();
        Assertions.assertEquals(1, dbSettingsEntities.size());

        testAppSettingsEntity = appSettings.load(TestAppSettingsEntity.class);
        Assertions.assertEquals(1, testAppSettingsEntity.getId());
        Assertions.assertFalse(testAppSettingsEntity.getTestBooleanValue());
        Assertions.assertFalse(dbSettingsEntities.get(0).getTestBooleanValue());
        Assertions.assertEquals(410, testAppSettingsEntity.getTestIntegerValue());
        Assertions.assertEquals(410, dbSettingsEntities.get(0).getTestIntegerValue());
        Assertions.assertEquals(500100L, testAppSettingsEntity.getTestLongValue());
        Assertions.assertEquals(500100L, dbSettingsEntities.get(0).getTestLongValue());
        Assertions.assertEquals(2.7182818284, testAppSettingsEntity.getTestDoubleValue());
        Assertions.assertEquals(2.7182818284, dbSettingsEntities.get(0).getTestDoubleValue());
        Assertions.assertEquals("access denied", testAppSettingsEntity.getTestStringValue());
        Assertions.assertEquals("access denied", dbSettingsEntities.get(0).getTestStringValue());

        //ensure default values are returned for null values
        testAppSettingsEntity.setTestBooleanValue(null);
        testAppSettingsEntity.setTestIntegerValue(null);
        testAppSettingsEntity.setTestLongValue(null);
        testAppSettingsEntity.setTestDoubleValue(null);
        testAppSettingsEntity.setTestStringValue(null);
        appSettings.save(testAppSettingsEntity);

        dbSettingsEntities = dataManager.load(TestAppSettingsEntity.class).all().list();
        Assertions.assertEquals(1, dbSettingsEntities.size());

        testAppSettingsEntity = appSettings.load(TestAppSettingsEntity.class);
        Assertions.assertEquals(1, testAppSettingsEntity.getId());
        Assertions.assertTrue(testAppSettingsEntity.getTestBooleanValue());
        Assertions.assertNull(dbSettingsEntities.get(0).getTestBooleanValue());
        Assertions.assertEquals(123, testAppSettingsEntity.getTestIntegerValue());
        Assertions.assertNull(dbSettingsEntities.get(0).getTestIntegerValue());
        Assertions.assertEquals(100500L, testAppSettingsEntity.getTestLongValue());
        Assertions.assertNull(dbSettingsEntities.get(0).getTestLongValue());
        Assertions.assertEquals(3.1415926535, testAppSettingsEntity.getTestDoubleValue());
        Assertions.assertNull(dbSettingsEntities.get(0).getTestDoubleValue());
        Assertions.assertEquals("defVal", testAppSettingsEntity.getTestStringValue());
        Assertions.assertNull(dbSettingsEntities.get(0).getTestStringValue());
    }

    @Test
    void testOnlyOneAppSettingsExist() {
        TestAppSettingsEntity firstTestAppSettingsEntity = dataManager.create(TestAppSettingsEntity.class);
        firstTestAppSettingsEntity.setTestBooleanValue(true);
        firstTestAppSettingsEntity.setTestLongValue(333L);
        firstTestAppSettingsEntity.setTestDoubleValue(6.626);
        firstTestAppSettingsEntity.setTestStringValue("access granted");
        appSettings.save(firstTestAppSettingsEntity);

        TestAppSettingsEntity loadedTestAppSettingsEntity = appSettings.load(TestAppSettingsEntity.class);
        Assertions.assertEquals(1, loadedTestAppSettingsEntity.getId());
        Assertions.assertTrue(loadedTestAppSettingsEntity.getTestBooleanValue());
        Assertions.assertEquals(123, loadedTestAppSettingsEntity.getTestIntegerValue());
        Assertions.assertEquals(333L, loadedTestAppSettingsEntity.getTestLongValue());
        Assertions.assertEquals(6.626, loadedTestAppSettingsEntity.getTestDoubleValue());
        Assertions.assertEquals("access granted", loadedTestAppSettingsEntity.getTestStringValue());

        TestAppSettingsEntity secondTestAppSettingsEntity = dataManager.create(TestAppSettingsEntity.class);
        secondTestAppSettingsEntity.setTestBooleanValue(false);
        secondTestAppSettingsEntity.setTestLongValue(444L);
        secondTestAppSettingsEntity.setTestDoubleValue(99.111);
        secondTestAppSettingsEntity.setTestStringValue("unknown");
        appSettings.save(secondTestAppSettingsEntity);

        loadedTestAppSettingsEntity = appSettings.load(TestAppSettingsEntity.class);
        Assertions.assertEquals(1, loadedTestAppSettingsEntity.getId());
        Assertions.assertFalse(loadedTestAppSettingsEntity.getTestBooleanValue());
        Assertions.assertEquals(123, loadedTestAppSettingsEntity.getTestIntegerValue());
        Assertions.assertEquals(444L, loadedTestAppSettingsEntity.getTestLongValue());
        Assertions.assertEquals(99.111, loadedTestAppSettingsEntity.getTestDoubleValue());
        Assertions.assertEquals("unknown", loadedTestAppSettingsEntity.getTestStringValue());
    }

    @Test
    void testExplicitlyEstablishedValuesThatMatchDefaultsSavedToDataStore() {
        List<TestAppSettingsEntity> testEntities = dataManager.load(TestAppSettingsEntity.class).all().list();
        Assertions.assertTrue(testEntities.isEmpty());

        //ensure that an attempt to store appSettings entity with null values does not lead to storing it actually
        TestAppSettingsEntity createdTestAppSettingsEntity = dataManager.create(TestAppSettingsEntity.class);
        appSettings.save(createdTestAppSettingsEntity);
        testEntities = dataManager.load(TestAppSettingsEntity.class).all().list();
        Assertions.assertTrue(testEntities.isEmpty());

        //set some settings value equal to default and save it
        createdTestAppSettingsEntity.setTestStringValue("defVal");
        createdTestAppSettingsEntity.setTestLongValue(100500L);
        appSettings.save(createdTestAppSettingsEntity);

        testEntities = dataManager.load(TestAppSettingsEntity.class).all().list();
        Assertions.assertEquals(1, testEntities.size());

        TestAppSettingsEntity testAppSettingsEntity = appSettings.load(TestAppSettingsEntity.class);
        Assertions.assertEquals("defVal", testAppSettingsEntity.getTestStringValue());
        Assertions.assertEquals("defVal", testEntities.get(0).getTestStringValue());
        Assertions.assertEquals(100500L, testAppSettingsEntity.getTestLongValue());
        Assertions.assertEquals(100500L, testEntities.get(0).getTestLongValue());

        Assertions.assertNull(testEntities.get(0).getTestIntegerValue());
        Assertions.assertNull(testEntities.get(0).getTestDoubleValue());
        Assertions.assertNull(testEntities.get(0).getTestBooleanValue());
    }

}
