package com.telefonica.euro_iaas.paasmanager.environment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import junit.framework.TestCase;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.ContextLoaderListener;

import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.bootstrap.InitDbBootstrap;
import com.telefonica.euro_iaas.paasmanager.claudia.util.ClaudiaUtil;
import com.telefonica.euro_iaas.paasmanager.dao.ProductReleaseDao;
import com.telefonica.euro_iaas.paasmanager.dao.impl.EnvironmentDaoJpaImpl;
import com.telefonica.euro_iaas.paasmanager.exception.AlreadyExistEntityException;
import com.telefonica.euro_iaas.paasmanager.exception.EnvironmentInstanceNotFoundException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidEnvironmentRequestException;
import com.telefonica.euro_iaas.paasmanager.manager.EnvironmentInstanceManager;
import com.telefonica.euro_iaas.paasmanager.manager.EnvironmentManager;
import com.telefonica.euro_iaas.paasmanager.manager.TierInstanceManager;
import com.telefonica.euro_iaas.paasmanager.manager.TierManager;
import com.telefonica.euro_iaas.paasmanager.manager.impl.EnvironmentManagerImpl;
import com.telefonica.euro_iaas.paasmanager.model.Attribute;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.Task;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.model.dto.EnvironmentDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.EnvironmentInstanceDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.TierDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.TierInstanceDto;
import com.telefonica.euro_iaas.paasmanager.rest.resources.EnvironmentInstanceResource;
import com.telefonica.euro_iaas.paasmanager.rest.resources.EnvironmentResource;
import com.telefonica.euro_iaas.paasmanager.rest.resources.EnvironmentResourceImpl;
import com.telefonica.euro_iaas.paasmanager.rest.resources.TierInstanceResource;
import com.telefonica.euro_iaas.paasmanager.rest.resources.TierResource;
import com.telefonica.euro_iaas.paasmanager.rest.util.OVFGeneration;
import com.telefonica.euro_iaas.paasmanager.rest.validation.EnvironmentResourceValidator;
import com.telefonica.euro_iaas.paasmanager.rest.validation.EnvironmentResourceValidatorImpl;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

@RunWith(SpringJUnit4ClassRunner.class)
// ApplicationContext will be loaded from "classpath:/app-config.xml"
@ContextConfiguration(locations = {"classpath:/applicationContextTest.xml"})
@ActiveProfiles("dummy")

public class EnvironmenInstanceTest{


	   @Autowired
	   private EnvironmentResource environmentResource;
	   
	   @Autowired
	   private EnvironmentInstanceResource environmentInstanceResource;
	   
	   @Autowired
	   private TierInstanceResource tierInstanceResource;
	   
	   @Autowired
	   private TierInstanceManager tierInstanceManager;
	   
	   @Autowired
	   private TierResource tierResource;
	   
	   
	   @Autowired
	   private ProductReleaseDao productReleaseDao;
	   
	   @Autowired
	   private EnvironmentManager environmentManager;
	   
	   @Autowired
	   private EnvironmentInstanceManager environmentInstanceManager;
	   
	   @Autowired
	   private TierManager tierManager;

	   String org = "FIWARE";
	   String vdc = "6571e3422ad84f7d828ce2f30373b3d4";

	   @Test
	   public void testCreateEnvironmentInstance() throws Exception {
		   
		   ProductRelease product = new ProductRelease("tomcat21", "7",
					"Tomcat server 21", null);

			
		   product = productReleaseDao.create(product);
			assertNotNull(product);
		    assertNotNull(product.getId());
			assertEquals(product.getProduct(), "tomcat21");
			assertEquals(product.getVersion(), "7");
			
			Environment environmentBk = new Environment();
			environmentBk.setName("testCreatedEnvirionmentInstance");
			environmentBk.setDescription("Description First environment");
			Tier tierbk = new Tier("tierdtotest", new Integer(1), new Integer(1),
							new Integer(1), null);
			tierbk.setImage("image");
				tierbk.setIcono("icono");
				tierbk.setFlavour("flavour");
				tierbk.setFloatingip("floatingip");
				tierbk.setPayload("");
				tierbk.setKeypair("keypair");
				tierbk.addProductRelease(product);
				environmentBk.addTier(tierbk);
				

				environmentResource.insert(org, vdc, environmentBk.toDto());
				
				Environment env2 = environmentManager.load("testCreatedEnvirionmentInstance");
				
		   
		   EnvironmentInstanceDto envInst = new EnvironmentInstanceDto();
		   envInst.setBlueprintName("blueprintname");
		   envInst.setDescription("description");
		   envInst.setEnvironmentDto(environmentBk.toDto());
		   
		   Task task = environmentInstanceResource.create(org, vdc, envInst, "");
		   
		   Thread.sleep(5000);
		   
		   assertEquals(task.getStatus(), Task.TaskStates.RUNNING);
		   
		   EnvironmentInstance envInstResult = environmentInstanceManager.load(vdc, "blueprintname");
		   assertNotNull(envInstResult);
		   assertEquals(envInstResult.getBlueprintName(), "blueprintname");
		   assertEquals(envInstResult.getEnvironment().getName(), "testCreatedEnvirionmentInstance");
		   
		   TierInstance tierInstance = tierInstanceManager.load("blueprintname-tierdtotest-1");
		   assertEquals(tierInstance.getName(), "blueprintname-tierdtotest-1");
		   assertEquals(tierInstance.getNumberReplica(), 1);
		   assertEquals(tierInstance.getTier().getName(), "tierdtotest");


	   }
	   @Test(expected=InvalidEntityException.class)
	   public void testCreateEnvironmentInstanceNoEnvironment() throws Exception {

		   EnvironmentInstanceDto envInst = new EnvironmentInstanceDto();
		   envInst.setBlueprintName("blueprintname");
		   envInst.setDescription("description");

		   environmentInstanceResource.create(org, vdc, envInst, "");

	   }
	   
	   @Test(expected=InvalidEntityException.class)
	   public void testCreateEnvironmentInstanceNoDescription() throws Exception {

		   EnvironmentInstanceDto envInst = new EnvironmentInstanceDto();
		   envInst.setBlueprintName("blueprintname2");
		   environmentInstanceResource.create(org, vdc, envInst, "");

	   }
	   @Test(expected=InvalidEntityException.class)
	   public void testCreateEnvironmentInstanceNoBlueprintName() throws Exception {

		   EnvironmentInstanceDto envInst = new EnvironmentInstanceDto();
		   envInst.setDescription("description");
		   environmentInstanceResource.create(org, vdc, envInst, "");

	   }
	   
	   @Test(expected=InvalidEntityException.class)
	   public void testCreateEnvironmentInstanceAlreadyDeployed() throws Exception {

		   ProductRelease product = new ProductRelease("tomcat23", "7",
					"Tomcat server 21", null);
		   product.addAttribute(new Attribute("key","value"));
		   
		   product = productReleaseDao.create(product);
			assertNotNull(product);
		    assertNotNull(product.getId());
			assertEquals(product.getProduct(), "tomcat23");
			assertEquals(product.getVersion(), "7");
			
			Environment environmentBk = new Environment();
			environmentBk.setName("testCreatedEnvirionmentInstanceAlreadyDeployed");
			environmentBk.setDescription("Description First environment");
			Tier tierbk = new Tier("tierdtotest1", new Integer(1), new Integer(1),
							new Integer(1), null);
				tierbk.setImage("image");
				tierbk.setIcono("icono");
				tierbk.setFlavour("flavour");
				tierbk.setFloatingip("floatingip");
				tierbk.setPayload("");
				tierbk.setKeypair("keypair");
				tierbk.addProductRelease(product);
				environmentBk.addTier(tierbk);
				

				environmentResource.insert(org, vdc, environmentBk.toDto());
				
		   EnvironmentInstanceDto envInst = new EnvironmentInstanceDto();
		   envInst.setBlueprintName("blueprintnameAlready");
		   envInst.setDescription("description");
		   envInst.setEnvironmentDto(environmentBk.toDto());
		   
		   Task task = environmentInstanceResource.create(org, vdc, envInst, "");
		   
		   Thread.sleep(5000);
		   
		   assertEquals(task.getStatus(), Task.TaskStates.RUNNING);
		   
		   task = environmentInstanceResource.create(org, vdc, envInst, "");
		   
		   

	   }
	   
	   @Test
	   public void testCreateEnvironmentInstanceCreateTierInstance() throws Exception {
		   
		   ProductRelease product = new ProductRelease("tomcat25", "7",
					"Tomcat server 21", null);

			
		   product = productReleaseDao.create(product);
			assertNotNull(product);
		    assertNotNull(product.getId());
			assertEquals(product.getProduct(), "tomcat25");
			assertEquals(product.getVersion(), "7");
			
			Environment environmentBk = new Environment();
			environmentBk.setName("testCreatedEnvirionmentInstanceTierInstance");
			environmentBk.setDescription("Description First environment");
			Tier tierbk = new Tier("tierdAddTierInstancr", new Integer(2), new Integer(1),
							new Integer(1), null);
				tierbk.setImage("image");
				tierbk.setIcono("icono");
				tierbk.setFlavour("flavour");
				tierbk.setFloatingip("floatingip");
				tierbk.setPayload("");
				tierbk.setKeypair("keypair");
				tierbk.addProductRelease(product);
				environmentBk.addTier(tierbk);
				

				environmentResource.insert(org, vdc, environmentBk.toDto());
				
				Environment env2 = environmentManager.load("testCreatedEnvirionmentInstanceTierInstance");
				assertNotNull(env2);
				assertEquals(env2.getName(), "testCreatedEnvirionmentInstanceTierInstance");
				assertEquals(env2.getDescription(), "Description First environment");
				assertEquals(env2.getVdc(), vdc);
				assertEquals(env2.getOrg(), org);
				assertEquals(env2.getTiers().size(), 1);
				assertEquals(env2.getTiers().get(0).getName(), "tierdAddTierInstancr");
		   
		   EnvironmentInstanceDto envInst = new EnvironmentInstanceDto();
		   envInst.setBlueprintName("blueprintnameTierInstance");
		   envInst.setDescription("description");
		   envInst.setEnvironmentDto(environmentBk.toDto());
		   
		   Task task = environmentInstanceResource.create(org, vdc, envInst, "");
		   Thread.sleep(20000);
		   assertEquals(task.getStatus(), Task.TaskStates.RUNNING);
		   
		   EnvironmentInstance envInstResult = environmentInstanceManager.load(vdc, "blueprintnameTierInstance");
		   assertNotNull(envInstResult);
		   assertEquals(envInstResult.getBlueprintName(), "blueprintnameTierInstance");
		   assertEquals(envInstResult.getEnvironment().getName(), "testCreatedEnvirionmentInstanceTierInstance");
		   assertEquals(envInstResult.getTierInstances().size(), 1);
		   
		   TierInstance tierInstance = tierInstanceManager.load("blueprintnameTierInstance-tierdAddTierInstancr-1");
		   assertEquals(tierInstance.getName(), "blueprintnameTierInstance-tierdAddTierInstancr-1");
		   assertEquals(tierInstance.getNumberReplica(), 1);

		   task = tierInstanceResource.insert(org, vdc, "blueprintnameTierInstance", tierbk.toDto(), "");
		   Thread.sleep(5000);
		  assertEquals(task.getStatus(), Task.TaskStates.RUNNING);
		   tierInstance = tierInstanceManager.load("blueprintnameTierInstance-tierdAddTierInstancr-2");
		  assertEquals(tierInstance.getName(), "blueprintnameTierInstance-tierdAddTierInstancr-2");
		  assertEquals(tierInstance.getNumberReplica(), 2);
		  
		


	   }
	   
	   
	   @Test
	   public void testCreateEnvironmentInstanceDeleteTierInstance() throws Exception {
		   
		   ProductRelease product = new ProductRelease("tomcat31", "7",
					"Tomcat server 21", null);

			
		   product = productReleaseDao.create(product);
			assertNotNull(product);
		    assertNotNull(product.getId());
			assertEquals(product.getProduct(), "tomcat31");
			assertEquals(product.getVersion(), "7");
			
			Environment environmentBk = new Environment();
			environmentBk.setName("testCreatedEnvirionmentInstanceDeleteTierInstance");
			environmentBk.setDescription("Description First environment");
			Tier tierbk = new Tier("tierdAddDeleteTierInstancr", new Integer(2), new Integer(0),
							new Integer(1), null);
				tierbk.setImage("image");
				tierbk.setIcono("icono");
				tierbk.setFlavour("flavour");
				tierbk.setFloatingip("floatingip");
				tierbk.setPayload("");
				tierbk.setKeypair("keypair");
				tierbk.addProductRelease(product);
				environmentBk.addTier(tierbk);
				

				environmentResource.insert(org, vdc, environmentBk.toDto());
				Environment env = environmentManager.load("testCreatedEnvirionmentInstanceDeleteTierInstance",vdc);
			
				assertNotNull(env);
				assertEquals(env.getName(), "testCreatedEnvirionmentInstanceDeleteTierInstance");
				assertEquals(env.getDescription(), "Description First environment");
				assertEquals(env.getTiers().size(), 1);
				assertEquals(env.getTiers().get(0).getName(), "tierdAddDeleteTierInstancr");
		   
		   EnvironmentInstanceDto envInst = new EnvironmentInstanceDto();
		   envInst.setBlueprintName("blueprintnameTierInstanceDelete");
		   envInst.setDescription("description");
		   envInst.setEnvironmentDto(environmentBk.toDto());
		   
		   Task task = environmentInstanceResource.create(org, vdc, envInst, "");
		   Thread.sleep(5000);
		   assertEquals(task.getStatus(), Task.TaskStates.RUNNING);
		   
		   EnvironmentInstance envInstResult = environmentInstanceManager.load(vdc, "blueprintnameTierInstanceDelete");
		   assertNotNull(envInstResult);
		   assertEquals(envInstResult.getBlueprintName(), "blueprintnameTierInstanceDelete");
		   assertEquals(envInstResult.getEnvironment().getName(), "testCreatedEnvirionmentInstanceDeleteTierInstance");
		   assertEquals(envInstResult.getTierInstances().size(), 1);
		   String name = envInstResult.getTierInstances().get(0).getName();

		   tierInstanceResource.removeTierInstance(org, vdc, "blueprintnameTierInstanceDelete", name, "");
		   
		   envInstResult = environmentInstanceManager.load(vdc, "blueprintnameTierInstanceDelete");
		   assertNotNull(envInstResult);
		   assertEquals(envInstResult.getBlueprintName(), "blueprintnameTierInstanceDelete");
		   assertEquals(envInstResult.getEnvironment().getName(), "testCreatedEnvirionmentInstanceDeleteTierInstance");
		   
		   assertEquals(envInstResult.getTierInstances().size(), 0);


	   }
	   
	   
	   @Test
	   public void testCreateDeleteEnvironmentInstance() throws Exception {
		   
		   ProductRelease product = new ProductRelease("tomcat26", "7",
					"Tomcat server 21", null);

			
		   product = productReleaseDao.create(product);
			assertNotNull(product);
		    assertNotNull(product.getId());
			assertEquals(product.getProduct(), "tomcat26");
			assertEquals(product.getVersion(), "7");
			
			Environment environmentBk = new Environment();
			environmentBk.setName("testDeleteEnvirionmentInstance");
			environmentBk.setDescription("Description First environment");
			Tier tierbk = new Tier("tierDeleteEnvInst", new Integer(1), new Integer(1),
							new Integer(1), null);
				tierbk.setImage("image");
				tierbk.setIcono("icono");
				tierbk.setFlavour("flavour");
				tierbk.setFloatingip("floatingip");
				tierbk.setPayload("");
				tierbk.setKeypair("keypair");
				tierbk.addProductRelease(product);
				environmentBk.addTier(tierbk);
				

				environmentResource.insert(org, vdc, environmentBk.toDto());
				Environment env = environmentManager.load("testDeleteEnvirionmentInstance",vdc);
				assertNotNull(env);
				assertEquals(env.getName(), "testDeleteEnvirionmentInstance");
				assertEquals(env.getDescription(), "Description First environment");
				assertEquals(env.getTiers().size(), 1);
				
		   
		   EnvironmentInstanceDto envInst = new EnvironmentInstanceDto();
		   envInst.setBlueprintName("blueprintnameDeleteEnvInstn");
		   envInst.setDescription("description");
		   envInst.setEnvironmentDto(environmentBk.toDto());
		   
		   Task task = environmentInstanceResource.create(org, vdc, envInst, "");
		   Thread.sleep(5000);
		   
		   assertEquals(task.getStatus(), Task.TaskStates.RUNNING);
		   
		   EnvironmentInstance envInstResult = environmentInstanceManager.load(vdc, "blueprintnameDeleteEnvInstn");
		   assertNotNull(envInstResult);
		   assertEquals(envInstResult.getBlueprintName(), "blueprintnameDeleteEnvInstn");
		   assertEquals(envInstResult.getEnvironment().getName(), "testDeleteEnvirionmentInstance");
		   assertEquals(envInstResult.getTierInstances().size(), 1);
		   
		   environmentInstanceResource.destroy(org, vdc, "blueprintnameDeleteEnvInstn", "");
	   //   environmentInstanceManager.load(vdc, "blueprintnameDeleteEnvInstn");
		   


	   }
	   
}