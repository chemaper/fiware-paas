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

package com.telefonica.euro_iaas.paasmanager.dao;

import java.util.List;

import com.telefonica.euro_iaas.commons.dao.BaseDAO;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.model.Environment;

/**
 * Defines the methods needed to persist Environment objects.
 * 
 * @author Jesus M. Movilla
 */
public interface EnvironmentDao extends BaseDAO<Environment, String> {

    /**
     * It loads for the environment in the database with the name and vdc.
     * 
     * @param envName
     * @param vdc
     * @return
     * @throws EntityNotFoundException
     */
    Environment findByEnvironmentNameVdc(String envName, String vdc) throws EntityNotFoundException;

    /**
     * Find all environments by tenant and organization.
     * 
     * @param org
     * @param vdc
     * @return
     */
    List<Environment> findByOrgAndVdc(String org, String vdc);

    /**
     * Find all environments by tenant, organization and environment name.
     * 
     * @param org
     * @param vdc
     * @param name
     * @return
     */
    List<Environment> findByOrgAndVdcAndName(String org, String vdc, String name);

    /**
     * Find all environments by organization
     * 
     * @param org
     * @return
     */
    List<Environment> findByOrg(String org);
}
