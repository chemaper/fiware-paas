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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.dao.TaskDao;
import com.telefonica.euro_iaas.paasmanager.exception.IPNotRetrievedException;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidEnvironmentRequestException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidOVFException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidProductInstanceRequestException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidVappException;
import com.telefonica.euro_iaas.paasmanager.exception.NotUniqueResultException;
import com.telefonica.euro_iaas.paasmanager.exception.ProductInstallatorException;
import com.telefonica.euro_iaas.paasmanager.exception.ProductReconfigurationException;
import com.telefonica.euro_iaas.paasmanager.manager.async.impl.EnvironmentInstanceAsyncManagerImpl;
import com.telefonica.euro_iaas.paasmanager.manager.async.impl.ProductInstanceAsyncManagerImpl;
import com.telefonica.euro_iaas.paasmanager.manager.async.impl.TaskManagerImpl;
import com.telefonica.euro_iaas.paasmanager.manager.async.impl.TierInstanceAsyncManagerImpl;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.InstallableInstance.Status;
import com.telefonica.euro_iaas.paasmanager.model.ProductInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.Task;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.model.dto.VM;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.ProductInstanceSearchCriteria;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * @author jesus.movilla
 */

public class AsynManagerImplTest {

    private TaskManagerImpl taskManager;
    private TierInstanceAsyncManagerImpl tierInstanceAsyncManagerImpl;
    private ProductInstanceAsyncManagerImpl productInstanceAsyncManagerImpl;
    private EnvironmentInstanceAsyncManagerImpl environmentInstanceAsyncManagerImpl;
    private TierInstanceManager tierInstanceManager;
    private EnvironmentInstanceManager environmentInstanceManager;
    private ProductInstanceManager productInstanceManager;
    private SystemPropertiesProvider systemPropertiesProvider;
    private TaskDao taskDao;

    private TierInstance tierInstanceShard;
    private EnvironmentInstance environmentInstance;
    private ProductInstance productInstance;
    private ProductRelease productReleaseShard;

    /**
     * Initialization of the class.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

        systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        taskManager = new TaskManagerImpl();
        taskDao = mock(TaskDao.class);
        taskManager.setPropertiesProvider(systemPropertiesProvider);
        taskManager.setTaskDao(taskDao);
        when(systemPropertiesProvider.getProperty(anyString())).thenReturn("value");

        tierInstanceAsyncManagerImpl = new TierInstanceAsyncManagerImpl();
        tierInstanceManager = mock(TierInstanceManager.class);
        tierInstanceAsyncManagerImpl.setTierInstanceManager(tierInstanceManager);
        tierInstanceAsyncManagerImpl.setTaskManager(taskManager);
        tierInstanceAsyncManagerImpl.setPropertiesProvider(systemPropertiesProvider);

        productInstanceManager = mock(ProductInstanceManager.class);
        productInstanceAsyncManagerImpl = new ProductInstanceAsyncManagerImpl();
        productInstanceAsyncManagerImpl.setProductInstanceManager(productInstanceManager);
        productInstanceAsyncManagerImpl.setPropertiesProvider(systemPropertiesProvider);
        productInstanceAsyncManagerImpl.setTaskManager(taskManager);

        environmentInstanceAsyncManagerImpl = mock(EnvironmentInstanceAsyncManagerImpl.class);
        environmentInstanceManager = mock(EnvironmentInstanceManager.class);
        environmentInstanceAsyncManagerImpl.setEnvironmentInstanceManager(environmentInstanceManager);
        environmentInstanceAsyncManagerImpl.setPropertiesProvider(systemPropertiesProvider);
        environmentInstanceAsyncManagerImpl.setTaskManager(taskManager);

        productReleaseShard = new ProductRelease("shard", "2.0");
        List<ProductRelease> productReleasesShards = new ArrayList<ProductRelease>();
        productReleasesShards.add(productReleaseShard);

        Tier tierProductShard = new Tier();
        tierProductShard.setInitialNumberInstances(new Integer(1));
        tierProductShard.setMaximumNumberInstances(new Integer(5));
        tierProductShard.setMinimumNumberInstances(new Integer(1));
        tierProductShard.setName("tiershard");
        tierProductShard.setProductReleases(productReleasesShards);

        Environment envResult = new Environment();
        envResult.setName("environemntName");
        envResult.addTier(tierProductShard);

        environmentInstance = new EnvironmentInstance();
        environmentInstance.setEnvironment(envResult);
        environmentInstance.setBlueprintName("blueprintName");
        environmentInstance.setDescription("description");
        productInstance = new ProductInstance(productReleaseShard, Status.INSTALLING, "vdc");
        List<ProductInstance> lProductInstance = new ArrayList();
        lProductInstance.add(productInstance);
        VM vm = new VM();
        tierInstanceShard = new TierInstance(tierProductShard, "tierInsatnceShard", "nametierInstance-tier-1", vm);
        environmentInstance.addTierInstance(tierInstanceShard);

        Task task = new Task();
        task.setId(new Long(1));
        task.setVdc("vdc");
        when(taskDao.load(any(Long.class))).thenReturn(task);
        when(taskDao.create(any(Task.class))).thenReturn(task);
        when(taskDao.update(any(Task.class))).thenReturn(task);

    }

    /**
     * Test the creation of ha task manager implementation.
     * 
     * @throws AlreadyExistsEntityException
     */
    @Test
    public void testCreateTaskManagerImpl() throws AlreadyExistsEntityException {
        Task task = new Task();
        task.setId(new Long(1));
        task.setVdc("vdc");
        task = taskManager.createTask(task);
        assertEquals(task.getId(), 1);
    }

    /**
     * Update a Task Manager Impl.
     * 
     * @throws AlreadyExistsEntityException
     */
    @Test
    public void testUpdateTaskManagerImpl() throws AlreadyExistsEntityException {
        Task task = new Task();
        task.setId(new Long(1));
        task.setVdc("vdc");

        task = taskManager.updateTask(task);
        assertEquals(task.getId(), 1);
    }

    /**
     * Test the loading of a task manager implementation.
     * 
     * @throws AlreadyExistsEntityException
     * @throws EntityNotFoundException
     */
    @Test
    public void testLoadTaskManagerImpl() throws AlreadyExistsEntityException, EntityNotFoundException {
        Task task = new Task();
        task.setId(new Long(1));
        task.setVdc("vdc");
        task = taskManager.load(new Long(1));
        assertEquals(task.getId(), 1);
    }

    /**
     * Test the creation of a tier instance manager.
     * 
     * @throws AlreadyExistsEntityException
     * @throws InvalidEntityException
     * @throws InfrastructureException
     * @throws EntityNotFoundException
     * @throws NotUniqueResultException
     * @throws InvalidProductInstanceRequestException
     * @throws ProductInstallatorException
     */
    @Test
    public void testCreateTierInstanceManager() throws AlreadyExistsEntityException, InvalidEntityException,
            InfrastructureException, EntityNotFoundException, NotUniqueResultException,
            InvalidProductInstanceRequestException, ProductInstallatorException {

        ClaudiaData claudiaData = new ClaudiaData("org", "vdc", "service");
        String callback = "";
        Task task = new Task();

        Mockito.doNothing()
                .when(tierInstanceManager)
                .create(any(ClaudiaData.class), any(TierInstance.class), any(EnvironmentInstance.class),
                        any(SystemPropertiesProvider.class));

        tierInstanceAsyncManagerImpl.create(claudiaData, tierInstanceShard, environmentInstance, task, callback);

    }

    @Test
    public void testUpdateTierInstanceManager() throws AlreadyExistsEntityException, InvalidEntityException,
            InfrastructureException, EntityNotFoundException, NotUniqueResultException,
            InvalidProductInstanceRequestException, ProductInstallatorException, ProductReconfigurationException {

        ClaudiaData claudiaData = new ClaudiaData("org", "vdc", "service");
        String callback = "";
        Task task = new Task();

        Mockito.doNothing().when(tierInstanceManager)
                .update(any(ClaudiaData.class), any(TierInstance.class), any(EnvironmentInstance.class));

        tierInstanceAsyncManagerImpl.update(claudiaData, tierInstanceShard, environmentInstance, task, callback);

    }

    @Test
    public void testCreateTierInstanceManagerError() throws AlreadyExistsEntityException, InvalidEntityException,
            InfrastructureException, EntityNotFoundException, NotUniqueResultException,
            InvalidProductInstanceRequestException, ProductInstallatorException {

        ClaudiaData claudiaData = new ClaudiaData("org", "vdc", "service");
        String callback = "";
        Task task = new Task();

        Mockito.doThrow(new InfrastructureException("test"))
                .when(tierInstanceManager)
                .create(any(ClaudiaData.class), any(TierInstance.class), any(EnvironmentInstance.class),
                        any(SystemPropertiesProvider.class));

        tierInstanceAsyncManagerImpl.create(claudiaData, tierInstanceShard, environmentInstance, task, callback);

    }

    @Test
    public void testDeleteTierInstanceManager() throws AlreadyExistsEntityException, InvalidEntityException,
            InfrastructureException, EntityNotFoundException {

        ClaudiaData claudiaData = new ClaudiaData("org", "vdc", "service");
        String callback = "";
        Task task = new Task();

        Mockito.doNothing().when(tierInstanceManager)
                .delete(any(ClaudiaData.class), any(TierInstance.class), any(EnvironmentInstance.class));

        tierInstanceAsyncManagerImpl.delete(claudiaData, tierInstanceShard, environmentInstance, task, callback);

    }

    @Test
    public void testCreateProductInstanceManager() throws AlreadyExistsEntityException, InvalidEntityException,
            InfrastructureException, EntityNotFoundException, NotUniqueResultException,
            InvalidProductInstanceRequestException, ProductInstallatorException {

        ClaudiaData claudiaData = new ClaudiaData("org", "vdc", "service");
        String callback = "";
        Task task = new Task();

        when(
                productInstanceManager.install(any(TierInstance.class), any(ClaudiaData.class), any(String.class),
                        any(ProductRelease.class), any(HashSet.class))).thenReturn(productInstance);

        productInstanceAsyncManagerImpl.install(tierInstanceShard, claudiaData, "env", productReleaseShard, null, task,
                callback);

    }

    @Test
    public void testCreateProductInstanceManagerError() throws AlreadyExistsEntityException, InvalidEntityException,
            InfrastructureException, EntityNotFoundException, NotUniqueResultException,
            InvalidProductInstanceRequestException, ProductInstallatorException {

        ClaudiaData claudiaData = new ClaudiaData("org", "vdc", "service");
        String callback = "";
        Task task = new Task();

        when(
                productInstanceManager.install(any(TierInstance.class), any(ClaudiaData.class), any(String.class),
                        any(ProductRelease.class), any(HashSet.class))).thenThrow(
                new InvalidProductInstanceRequestException("test"));

        productInstanceAsyncManagerImpl.install(tierInstanceShard, claudiaData, "env", productReleaseShard, null, task,
                callback);

    }

    @Test
    public void testCreateProductInstanceManagerErrorII() throws AlreadyExistsEntityException, InvalidEntityException,
            InfrastructureException, EntityNotFoundException, NotUniqueResultException,
            InvalidProductInstanceRequestException, ProductInstallatorException {

        ClaudiaData claudiaData = new ClaudiaData("org", "vdc", "service");
        String callback = "";
        Task task = new Task();

        when(
                productInstanceManager.install(any(TierInstance.class), any(ClaudiaData.class), any(String.class),
                        any(ProductRelease.class), any(HashSet.class))).thenThrow(
                new InvalidProductInstanceRequestException("test"));

        when(productInstanceManager.loadByCriteria(any(ProductInstanceSearchCriteria.class))).thenThrow(
                new EntityNotFoundException(ProductInstance.class, callback, task));

        productInstanceAsyncManagerImpl.install(tierInstanceShard, claudiaData, "env", productReleaseShard, null, task,
                callback);

    }

    @Test
    public void testDeleteProductInstanceManager() throws AlreadyExistsEntityException, InvalidEntityException,
            InfrastructureException, EntityNotFoundException, ProductInstallatorException {

        ClaudiaData claudiaData = new ClaudiaData("org", "vdc", "service");
        String callback = "";
        Task task = new Task();

        Mockito.doNothing().when(productInstanceManager).uninstall(any(ClaudiaData.class), any(ProductInstance.class));

        productInstanceAsyncManagerImpl.uninstall(claudiaData, productInstance, task, callback);

    }

    @Test
    public void testCreateEnvInstanceManager() throws AlreadyExistsEntityException, InvalidEntityException,
            InfrastructureException, EntityNotFoundException, NotUniqueResultException,
            InvalidProductInstanceRequestException, ProductInstallatorException, IPNotRetrievedException,
            InvalidEnvironmentRequestException, InvalidOVFException, InvalidVappException {

        ClaudiaData claudiaData = new ClaudiaData("org", "vdc", "service");
        String callback = "";
        Task task = new Task();
        when(environmentInstanceManager.load(any(String.class), any(String.class))).thenThrow(
                new EntityNotFoundException(EnvironmentInstance.class, callback, task));

        when(environmentInstanceManager.create(any(ClaudiaData.class), any(EnvironmentInstance.class))).thenReturn(
                environmentInstance);

        environmentInstanceAsyncManagerImpl.create(claudiaData, environmentInstance, task, callback);

    }

    @Test
    public void testCreateEnvInstanceManagerError() throws AlreadyExistsEntityException, InvalidEntityException,
            InfrastructureException, EntityNotFoundException, NotUniqueResultException,
            InvalidProductInstanceRequestException, ProductInstallatorException, IPNotRetrievedException,
            InvalidEnvironmentRequestException, InvalidOVFException, InvalidVappException {

        ClaudiaData claudiaData = new ClaudiaData("org", "vdc", "service");
        String callback = "";
        Task task = new Task();
        when(environmentInstanceManager.load(any(String.class), any(String.class))).thenThrow(
                new EntityNotFoundException(EnvironmentInstance.class, callback, task));

        when(environmentInstanceManager.create(any(ClaudiaData.class), any(EnvironmentInstance.class))).thenThrow(
                new InfrastructureException("test"));

        environmentInstanceAsyncManagerImpl.create(claudiaData, environmentInstance, task, callback);

    }

    @Test
    public void testDeleteEnvInstanceManager() throws Exception {

        ClaudiaData claudiaData = new ClaudiaData("org", "vdc", "service");
        String callback = "";
        Task task = new Task();

        Mockito.doNothing().when(environmentInstanceManager)
                .destroy(any(ClaudiaData.class), any(EnvironmentInstance.class));

        environmentInstanceAsyncManagerImpl.destroy(claudiaData, environmentInstance, task, callback);

    }

}
