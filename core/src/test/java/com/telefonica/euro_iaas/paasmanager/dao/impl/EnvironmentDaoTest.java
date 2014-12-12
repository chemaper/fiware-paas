/**
 * Copyright 2014 Telefonica Investigación y Desarrollo, S.A.U <br>
 * This file is part of FI-WARE project.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License.
 * </p>
 * <p>
 * You may obtain a copy of the License at:<br>
 * <br>
 * http://www.apache.org/licenses/LICENSE-2.0
 * </p>
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * </p>
 * <p>
 * See the License for the specific language governing permissions and limitations under the License.
 * </p>
 * <p>
 * For those usages not covered by the Apache version 2.0 License please contact with opensource@tid.es
 * </p>
 */

package com.telefonica.euro_iaas.paasmanager.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.dao.EnvironmentDao;
import com.telefonica.euro_iaas.paasmanager.model.Environment;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring-test-db-config.xml", "classpath:/spring-dao-config.xml" })
public class EnvironmentDaoTest {

    @Autowired
    EnvironmentDao environmentDao;

    public static final String ENVIRONMENT_NAME = "ENVIRONMENT_NAME";
    public static final String ORG = "org";
    public static final String VDC = "vdc";

    /**
     * Test the create method.
     */
    @Test
    public void testEnvironmentNoTiers() throws Exception {

        int num = environmentDao.findAll().size();
        Environment environment = new Environment();
        environment.setName(ENVIRONMENT_NAME);
        environment.setOrg(ORG);
        environment.setDescription("description");
        environment = environmentDao.create(environment);
        assertNotNull(environment);
        assertNotNull(environment.getId());
        assertEquals(environmentDao.findAll().size(), num + 1);

    }

    /**
     * Test the load method.
     */
    @Test
    public void testLoadNoTiers() throws Exception {

        Environment environment = new Environment();
        environment.setName(ENVIRONMENT_NAME);
        environment.setOrg(ORG);
        environment.setVdc("vdc");
        environment.setDescription("description");
        environment = environmentDao.create(environment);
        environment = environmentDao.findByEnvironmentNameVdc(environment.getName(), "vdc");
        assertNotNull(environment);
        assertNotNull(environment.getId());

    }

    /**
     * Test the load method.
     * 
     * @throws AlreadyExistsEntityException
     * @throws InvalidEntityException
     */
    @Test
    public void testDeleteNoTiers() throws InvalidEntityException, AlreadyExistsEntityException {

        Environment environment = new Environment();
        environment.setName(ENVIRONMENT_NAME);
        environment.setDescription("description");
        environment.setOrg(ORG);
        environment.setVdc("vdc");
        environment = environmentDao.create(environment);
        environmentDao.remove(environment);
        try {
            environmentDao.findByEnvironmentNameVdc(ENVIRONMENT_NAME, "vdc");
            fail("Should have thrown an EntityNotFoundException because the environment does not exit!");
        } catch (EntityNotFoundException e) {

        }
    }

    @Test
    public void shouldFindEnvironmentByOrg() throws AlreadyExistsEntityException {
        // given
        Environment environment = new Environment();
        environment.setOrg("myorg123");
        environment.setName("myname");
        environmentDao.create(environment);

        // when
        List<Environment> list = environmentDao.findByOrg("myorg123");

        // then
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("myname", list.get(0).getName());
        assertEquals("myorg123", list.get(0).getOrg());
    }

    @Test
    public void shouldFindEnvironmentByOrgAndTenant() throws AlreadyExistsEntityException {
        // given
        Environment environment = new Environment();
        environment.setOrg("myorg1");
        environment.setName("myname");
        environment.setVdc("mytenantid1");
        environmentDao.create(environment);

        // when
        List<Environment> list = environmentDao.findByOrgAndVdc("myorg1", "mytenantid1");

        // then
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("myname", list.get(0).getName());
        assertEquals("myorg1", list.get(0).getOrg());
        assertEquals("mytenantid1", list.get(0).getVdc());
    }

    @Test
    public void shouldFindEnvironmentByOrgAndTenantAndName() throws AlreadyExistsEntityException {
        // given
        Environment environment = new Environment();
        environment.setOrg("myorg1");
        environment.setName("envname");
        environment.setVdc("mytenantid1");
        environment.setName("envname");
        environmentDao.create(environment);

        // when
        List<Environment> list = environmentDao.findByOrgAndVdcAndName("myorg1", "mytenantid1", "envname");

        // then
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("envname", list.get(0).getName());
        assertEquals("myorg1", list.get(0).getOrg());
        assertEquals("mytenantid1", list.get(0).getVdc());
    }

}
