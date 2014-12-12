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

package com.telefonica.euro_iaas.paasmanager.manager;

import java.util.List;

import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.exception.IPNotRetrievedException;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidEnvironmentRequestException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidOVFException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidProductInstanceRequestException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidVappException;
import com.telefonica.euro_iaas.paasmanager.exception.NotUniqueResultException;
import com.telefonica.euro_iaas.paasmanager.exception.ProductInstallatorException;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.EnvironmentInstanceSearchCriteria;

public interface EnvironmentInstanceManager {

    /**
     * Create an environentInstance
     * 
     * @return
     * @throws EntityNotFoundException
     * @throws InvalidEntityException
     * @throws AlreadyExistsEntityException
     * @throws NotUniqueResultException
     * @throws InfrastructureException
     * @throws IPNotRetrievedException
     * @throws ProductInstallatorException
     * @throws InvalidEnvironmentRequestException
     */

    EnvironmentInstance create(ClaudiaData claudiaData, EnvironmentInstance environmentInstance)
            throws EntityNotFoundException, InvalidEntityException, AlreadyExistsEntityException,
            NotUniqueResultException, InfrastructureException, IPNotRetrievedException, ProductInstallatorException,
            InvalidEnvironmentRequestException, InvalidProductInstanceRequestException, InvalidOVFException,
            InvalidVappException;

    /**
     * Load an environentInstance
     * 
     * @param vdc
     * @param name
     * @return the environentInstance
     */
    public EnvironmentInstance load(String vdc, String name) throws EntityNotFoundException;

    /**
     * @param envInst
     * @return
     * @throws InvalidEntityException
     */
    public EnvironmentInstance update(EnvironmentInstance envInst) throws InvalidEntityException;

    /**
     * Find all EnvironmentInstances
     * 
     * @return the environentInstances
     */
    List<EnvironmentInstance> findAll();

    /**
     * Find the environment instances that match with the given criteria.
     * 
     * @param criteria
     *            the search criteria
     * @return the list of elements that match with the criteria.
     */
    List<EnvironmentInstance> findByCriteria(EnvironmentInstanceSearchCriteria criteria);

    /**
     * Destroy the Environment (Infrastructure + environment in Database)
     * 
     * @param envInstance
     * @param claudiaData
     * @throws EntityNotF
     *             oundException
     * @throws InfrastructureException
     * @throws Exception
     */
    void destroy(ClaudiaData claudiaData, EnvironmentInstance envInstance) throws Exception;

}
