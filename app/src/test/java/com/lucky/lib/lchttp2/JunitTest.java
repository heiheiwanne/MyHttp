package com.lucky.lib.lchttp2;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;

public class JunitTest {
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getEClass method, of class tmp.
     */
    @Test
    public void testNewClass() {
        EntityDao testDao = new EntityDao();
        Entity e = testDao.get(null);
        assertNotNull(e);
    }

}
