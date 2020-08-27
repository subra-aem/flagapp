package org.subra.aem.flagapp.internal.components.models;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.osgi.service.cm.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.aem.commons.helpers.RequestParser;
import org.subra.aem.flagapp.helpers.FlagAppHelper;
import org.subra.aem.flagapp.internal.Flag;
import org.subra.aem.flagapp.internal.Project;
import org.subra.aem.flagapp.models.FMSModel;

@Model(adaptables = { SlingHttpServletRequest.class, Resource.class }, adapters = { FMSModel.class })
public class FMSModelImpl implements FMSModel {

	private static final Logger LOGGER = LoggerFactory.getLogger(FMSModelImpl.class);

	@Self
	private SlingHttpServletRequest request;

	private String projectName;

	private FlagAppHelper flagApp;

	@PostConstruct
	protected void init() {
		projectName = RequestParser.getFirstSelector(request).orElse(null);
		try {
			flagApp = FlagAppHelper.build(projectName);
		} catch (ConfigurationException e) {
			LOGGER.error("Error Configuring SubraFlagApp...", e);
		}
	}

	@Override
	public String getProject() {
		return projectName;
	}

	@Override
	public List<Project> getProjects() {
		List<Project> projects = Collections.emptyList();
		try {
			projects = FlagAppHelper.projects();
			projects.sort(Comparator.comparing(Project::getName));
		} catch (ConfigurationException e) {
			LOGGER.error("Error Configuring SubraFlagApp...", e);
		}
		return projects;
	}

	@Override
	public List<Flag> getFlags() {
		return Optional.of(flagApp).map(FlagAppHelper::getAllFlags).orElseGet(Collections::emptyList);
	}

	@Override
	public Object getMessage() {
		return Optional.of(flagApp).map(f -> f.getFlagValue("test")).orElseGet(() -> null);
	}
}
