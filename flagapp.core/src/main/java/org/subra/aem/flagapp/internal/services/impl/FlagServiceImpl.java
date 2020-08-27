package org.subra.aem.flagapp.internal.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.aem.commons.exceptions.SubraRuntimeException;
import org.subra.aem.commons.helpers.SubraCommonHelper;
import org.subra.aem.commons.jcr.constants.SubraJcrFileNames;
import org.subra.aem.commons.jcr.constants.SubraJcrPrimaryType;
import org.subra.aem.commons.jcr.constants.SubraJcrProperties;
import org.subra.aem.commons.jcr.utils.SubraResourceUtils;
import org.subra.aem.commons.utils.SubraDateTimeUtil;
import org.subra.aem.commons.utils.SubraStringUtils;
import org.subra.aem.flagapp.helpers.FlagAppHelper;
import org.subra.aem.flagapp.internal.Flag;
import org.subra.aem.flagapp.internal.NewFlag;
import org.subra.aem.flagapp.internal.NewProject;
import org.subra.aem.flagapp.internal.Project;
import org.subra.aem.flagapp.internal.services.FlagService;

/**
 * The Service Implementaion to create, update, read and delete flag
 *
 * @author Raghava Joijode
 */
@Component(service = FlagService.class, immediate = true)
@ServiceDescription("Subra Flag App Service")
@Designate(ocd = FlagServiceImpl.Config.class)
public class FlagServiceImpl implements FlagService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FlagServiceImpl.class);

	private static final String PN_FMS_CREATED_ON = "fms-createdOn";
	private static final String PN_FMS_UPDATED_ON = "fms-updatedOn";
	private static final String PN_FMS_IS_BOOLEAN = "fms-isBoolean";
	private static final String PN_FMS_TEMP_CREATENOW = "fms-created-now";
	private static final String PN_FMS_TITLE = "fms-title";
	private static final String PN_FMS_VALUE = "fms-value";

	private Resource rootNode;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@ObjectClassDefinition(name = "Subra Flag App Service Configuration")
	public @interface Config {

		@AttributeDefinition(name = "Flag App Root Node Path")
		String flag_app_root_node_path() default "/var/subra/flag-app";
	}

	@Activate
	protected void activate(final Config config) {
		try {
			useResourceResolver(resolver -> rootNode = SubraResourceUtils.getOrCreateResource(resolver,
					config.flag_app_root_node_path(), SubraJcrPrimaryType.SLING_FOLDER));
			if (rootNode != null)
				FlagAppHelper.configure(this);
			else
				deactivate();
		} catch (ConfigurationException e) {
			LOGGER.error("Error Configuring FlagAppHelper...");
			deactivate();
		}
	}

	/**
	 * Method to create a new flag or update if already exists
	 *
	 * @param flagTitle The flag which needs to be created or updated
	 * @param flagValue The value to be associate with the flag
	 * @param type      The type of the value associated with the flag
	 */
	@Override
	public void createOrUpdateFlag(final NewFlag newFlag) {
		if (newFlag == null)
			throw new SubraRuntimeException("Invalid NewFlag Request...");
		final String flagTitle = newFlag.getFlagTitle();
		final Object flagValue = newFlag.getFlagValue();
		try {
			Resource project = getProjectResource(newFlag.getProjectName());
			ResourceResolver resolver = project.getResourceResolver();
			Optional.of(project).map(p -> SubraResourceUtils.getChildResource(p, SubraJcrFileNames.CONFIG_NODE.value()))
					.map(c -> SubraResourceUtils.getOrCreateResource(resolver,
							c.getPath().concat(SubraStringUtils.SLASH)
									.concat(SubraCommonHelper.createNameFromTitle(flagTitle)),
							SubraJcrPrimaryType.SLING_FOLDER))
					.ifPresent(flag -> {
						try {
							ModifiableValueMap mvm = flag.adaptTo(ModifiableValueMap.class);
							if (Boolean.FALSE.equals(mvm.get(PN_FMS_TEMP_CREATENOW, false)))
								mvm.put(PN_FMS_UPDATED_ON, SubraDateTimeUtil.localDateTimeString());
							mvm.put(PN_FMS_VALUE, flagValue);
							mvm.put(PN_FMS_TITLE, flagTitle);
							mvm.put(PN_FMS_IS_BOOLEAN, flagValue instanceof Boolean);
							mvm.remove(PN_FMS_TEMP_CREATENOW);
							resolver.commit();
						} catch (PersistenceException e) {
							throw new SubraRuntimeException("Error Creating Flag...");
						}
					});
		} catch (NullPointerException e) {
			throw new SubraRuntimeException("Project Not Found...");
		}
	}

	/**
	 * Method to read the flag value given flag name
	 *
	 * @param flagName The name of the flag whose value needs to be returned
	 * @return The value associate with the flag
	 */
	@Override
	public Object readFlag(final String projectName, final String flagName) {
		return Optional.ofNullable(rootNode).map(r -> r.getChild(projectName))
				.map(p -> p.getChild(SubraJcrFileNames.CONFIG_NODE.value())).map(c -> c.getChild(flagName))
				.map(Resource::getValueMap).map(vm -> vm.get(PN_FMS_VALUE)).orElseThrow(() -> new SubraRuntimeException(
						"Flag Not Found - Either of Project or Flag Doesn't exists..."));
	}

	/**
	 * Method to delete the given flag
	 *
	 * @param flagName The flag which needs to be deleted
	 */
	@Override
	public void deleteFlag(final String projectName, final String flagName) {
		try {
			Resource project = getProjectResource(projectName);
			ResourceResolver resolver = project.getResourceResolver();
			Optional.of(getProjectResource(projectName))
					.map(p -> SubraResourceUtils.getChildResource(p, SubraJcrFileNames.CONFIG_NODE.value()))
					.map(c -> SubraResourceUtils.getChildResource(c, flagName)).ifPresent(flag -> {
						try {
							SubraResourceUtils.deleteResource(resolver, flag);
						} catch (PersistenceException e) {
							throw new SubraRuntimeException(e);
						}
					});
		} catch (NullPointerException e) {
			throw new SubraRuntimeException(e);
		}
	}

	@Override
	public Project createProject(final NewProject project) {
		if (project == null)
			throw new SubraRuntimeException("Invalid NewProject Request...");

		ResourceResolver resolver = rootNode.getResourceResolver();
		final String name = project.getProjectName();
		try {
			Optional.of(rootNode).map(r -> r.getChild(name)).ifPresentOrElse(p -> {
				if (p.getChild(SubraJcrFileNames.CONFIG_NODE.value()) == null)
					SubraResourceUtils.getOrCreateResource(resolver,
							p.getPath().concat(SubraStringUtils.SLASH).concat(
									SubraCommonHelper.createNameFromTitle(SubraJcrFileNames.CONFIG_NODE.value())),
							SubraJcrPrimaryType.UNSTRUCTURED);
			}, () -> SubraResourceUtils.getOrCreateResource(resolver,
					rootNode.getPath().concat("/" + name).concat(SubraStringUtils.SLASH)
							.concat(SubraCommonHelper.createNameFromTitle(SubraJcrFileNames.CONFIG_NODE.value())),
					SubraJcrPrimaryType.UNSTRUCTURED));
		} catch (NullPointerException e) {
			LOGGER.error("Error Creating Project...", e);
			throw new SubraRuntimeException("Error Creating Project...");
		}
		return readProject(SubraCommonHelper.createNameFromTitle(name));
	}

	@Override
	public Project readProject(final String name) {
		Project projectVo = new Project();
		Optional.ofNullable(rootNode).map(r -> r.getChild(name))
				.map(p -> p.getChild(SubraJcrFileNames.CONFIG_NODE.value()))
				.ifPresentOrElse(c -> setProjectVo(c.getParent(), projectVo), () -> {
					throw new SubraRuntimeException("Project Doesn't Exists...");
				});
		return projectVo;
	}

	@Override
	public void deleteProject(final String name) {
		Optional.ofNullable(rootNode).map(r -> r.getChild(name))
				.map(p -> p.getChild(SubraJcrFileNames.CONFIG_NODE.value())).ifPresentOrElse(c -> {
					try {
						SubraResourceUtils.deleteResource(c.getResourceResolver(), c.getParent());
					} catch (PersistenceException e) {
						LOGGER.error("Error Deleting Project...", e);
						throw new SubraRuntimeException("Error Deleting Project...");
					}
				}, () -> {
					throw new SubraRuntimeException("Project Doesn't Exists...");
				});
	}

	@Override
	public List<Flag> projectFlags(final String projectName) {
		List<Flag> flags = new ArrayList<>();
		try {
			Optional.ofNullable(rootNode).map(r -> SubraResourceUtils.getChildResource(r, projectName))
					.map(p -> SubraResourceUtils.getChildResource(p, SubraJcrFileNames.CONFIG_NODE.value()))
					.map(Resource::listChildren).ifPresentOrElse(itr -> {
						while (itr.hasNext()) {
							Resource f = itr.next();
							flags.add(createFlagVo(f));
						}
					}, () -> {
						throw new SubraRuntimeException("Flag Not Found - Either of Project or Flag Doesn't exists...");
					});
		} catch (NullPointerException e) {
			throw new SubraRuntimeException("Error Building Project");
		}
		return flags;
	}

	@Override
	public List<Project> projects() {
		List<Project> projects = new LinkedList<>();
		Optional.ofNullable(rootNode).map(Resource::listChildren).ifPresent(i -> {
			while (i.hasNext()) {
				projects.add(createProjectVo(i.next()));
			}
		});
		return projects;
	}

	private Project createProjectVo(Resource project) {
		Project projectVo = new Project();
		setProjectVo(project, projectVo);
		return projectVo;
	}

	private void setProjectVo(Resource project, Project projectVo) {
		projectVo.setTitle(project.getValueMap().get(SubraJcrProperties.PN_TITLE.property(), project.getName()));
		projectVo.setFlagsCount(Optional.ofNullable(project.getChild(SubraJcrFileNames.CONFIG_NODE.value()))
				.map(Resource::listChildren).map(IteratorUtils::size).orElse(0));
		projectVo.setName(project.getName());
		projectVo.setCreatedOn(SubraDateTimeUtil.toLocalDateTimeString(
				project.getValueMap().get(SubraJcrProperties.PN_CREATED_ON.property(), Calendar.class)));
		projectVo.setUpdatedOn(SubraDateTimeUtil.toLocalDateTimeString(
				project.getValueMap().get(SubraJcrProperties.PN_UPDATED_ON.property(), Calendar.class)));
	}

	private Flag createFlagVo(Resource flag) {
		Flag flagVo = new Flag();
		ValueMap vm = flag.getValueMap();
		flagVo.setTitle(vm.get(PN_FMS_TITLE, SubraCommonHelper.decodeTitleFromName(flag.getName())));
		flagVo.setName(flag.getName());
		flagVo.setBoolean(vm.get(PN_FMS_IS_BOOLEAN, false));
		flagVo.setValue(vm.get(PN_FMS_VALUE));
		flagVo.setCreatedOn(vm.get(PN_FMS_CREATED_ON, String.class));
		flagVo.setUpdatedOn(vm.get(PN_FMS_UPDATED_ON, String.class));
		return flagVo;
	}

	private Resource getProjectResource(final String name) {
		return SubraResourceUtils.getChildResource(rootNode, name);
	}

	private void useResourceResolver(Consumer<ResourceResolver> consumer) {
		try {
			ResourceResolver resolver = getServiceResourceResolver();
			consumer.accept(resolver);
		} catch (LoginException e) {
			LOGGER.error("Error Getting ResourceResolver...", e);
		}
	}

	private ResourceResolver getServiceResourceResolver() throws LoginException {
		return SubraResourceUtils.getAdminServiceResourceResolver(resourceResolverFactory);
	}

	@Deactivate
	protected void deactivate() {
		LOGGER.info("De-Activating FlagService...");
		try {
			if (getServiceResourceResolver() != null && getServiceResourceResolver().isLive())
				getServiceResourceResolver().close();
		} catch (LoginException e) {
			LOGGER.error("Error Closing ResourceResolver...", e);
		}
	}

}
