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

import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.dao.EnvironmentDao;
import com.telefonica.euro_iaas.paasmanager.dao.EnvironmentInstanceDao;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.EnvironmentInstanceSearchCriteria;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring-test-db-config.xml", "classpath:/spring-dao-config.xml" })
public class EnvironmentInstanceDaoTest {

    @Autowired
    EnvironmentDao environmentDao;
    @Autowired
    EnvironmentInstanceDao environmentInstanceDao;

    public static final String ENVIRONMENT_NAME = "ENVIRONMENT_NAME";
    public static final String ORG = "org";
    public static final String VDC = "vdc";

    @Test
    public void testEnvironmentInstNoTiers() throws Exception {

        int num = environmentInstanceDao.findAll().size();
        EnvironmentInstance envInst = new EnvironmentInstance();
        envInst.setBlueprintName("bluename");
        envInst.setVdc(VDC);
        envInst.setName("env");
        envInst = environmentInstanceDao.create(envInst);

        envInst = environmentInstanceDao.load("bluename", VDC);

        assertNotNull(envInst);
        assertNotNull(envInst.getId());
        assertEquals(environmentInstanceDao.findAll().size(), num + 1);

    }

    @Test
    public void testProductLoadEnvInstError() throws EntityNotFoundException {
        try {
            environmentInstanceDao.load("otro", "3");
            fail("Should have thrown an EntityNotFoundException because the environment does not exit!");
        } catch (EntityNotFoundException e) {

        }

    }

    @Test
    public void testFindEnvInstByCriteria() throws Exception {

        Environment environment = new Environment();
        environment.setName(ENVIRONMENT_NAME + "cri");
        environment.setOrg(ORG);
        environment.setVdc("vdc");
        environment.setDescription("description");
        environment = environmentDao.create(environment);
        EnvironmentInstance envInst = new EnvironmentInstance();
        envInst.setBlueprintName("bluename2");
        envInst.setVdc(VDC);
        envInst.setName("env");
        envInst.setEnvironment(environment);
        envInst = environmentInstanceDao.create(envInst);

        EnvironmentInstanceSearchCriteria criteria = new EnvironmentInstanceSearchCriteria();
        criteria.setEnvironment(environment);
        criteria.setVdc(VDC);
        criteria.setEnviromentName("bluename2");
        List<EnvironmentInstance> lEnvironment = environmentInstanceDao.findByCriteria(criteria);
        assertNotNull(lEnvironment);
        assertEquals(lEnvironment.size(), 1);

    }

}
