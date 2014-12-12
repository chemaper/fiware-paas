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

import java.util.List;

import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.telefonica.euro_iaas.commons.dao.AbstractBaseDao;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.dao.EnvironmentDao;
import com.telefonica.euro_iaas.paasmanager.model.Environment;

@Transactional(propagation = Propagation.REQUIRED)
public class EnvironmentDaoJpaImpl extends AbstractBaseDao<Environment, String> implements EnvironmentDao {
    private static Logger log = LoggerFactory.getLogger(EnvironmentDaoJpaImpl.class);

    @Override
    public Environment load(String envName) throws EntityNotFoundException {
        return findByEnvironmentNameVdc(envName, "");
    }

    /**
     * It obtains all the environments in database.
     */
    public List<Environment> findAll() {
        return super.findAll(Environment.class);
    }

    /**
     * It loads for the environment in the database with the name and vdc.
     */
    public Environment findByEnvironmentNameVdc(String envName, String vdc) throws EntityNotFoundException {
        log.info("findByEnvironmentNameVdc");
        Query query = getEntityManager().createQuery(
                "select p from Environment p left join " + "fetch p.tiers where p.name = :name and p.vdc = :vdc");
        query.setParameter("name", envName);
        query.setParameter("vdc", vdc);
        Environment environment = null;
        try {
            environment = (Environment) query.getResultList().get(0);
            log.info("" + Hibernate.isInitialized(environment));
            Hibernate.initialize(environment);
            log.info("" + Hibernate.isInitialized(environment));
        } catch (Exception e) {
            log.warn("Error to load the env " + envName + " " + e.getMessage());
            throw new EntityNotFoundException(Environment.class, "name", envName);
        }

        log.info("returning env");
        return environment;
    }

    public List<Environment> findByOrgAndVdc(String org, String vdc) {

        List list;
        String queryString = "SELECT e FROM Environment e WHERE e.org LIKE :orgName and e.vdc = :vdcName";
        Query query = getEntityManager().createQuery(queryString);

        list = query.setParameter("orgName", org).setParameter("vdcName", vdc).getResultList();

        return list;
    }

    @Override
    public List<Environment> findByOrgAndVdcAndName(String org, String vdc, String name) {

        List list;
        String queryString = "SELECT e FROM Environment e WHERE e.org LIKE :orgName and e.vdc = :vdcName and e.name=:envName";
        Query query = getEntityManager().createQuery(queryString);

        list = query.setParameter("orgName", org).setParameter("vdcName", vdc).setParameter("envName", name)
                .getResultList();

        return list;
    }

    @Override
    public List<Environment> findByOrg(String org) {

        List<Environment> list;
        String queryString = "SELECT e FROM Environment e WHERE e.org LIKE :orgName";
        Query query = getEntityManager().createQuery(queryString);

        list = query.setParameter("orgName", org).getResultList();

        return list;
    }

}
