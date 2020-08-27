package org.subra.aem.flagapp.helpers;

import java.util.List;

import org.osgi.service.cm.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.aem.flagapp.internal.Flag;
import org.subra.aem.flagapp.internal.Project;
import org.subra.aem.flagapp.internal.services.FlagService;

public class FlagAppHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(FlagAppHelper.class);

	private static FlagService flagService;
	private String projectName;

	private FlagAppHelper() {
		throw new IllegalStateException(this.getClass().getSimpleName());
	}

	private FlagAppHelper(final String projectName) {
		this.projectName = projectName;
	}

	public static void configure(FlagService fs) throws ConfigurationException {
		flagService = fs;
		checkConfiguration();
	}

	public static FlagAppHelper build(final String project) throws ConfigurationException {
		checkConfiguration();
		return new FlagAppHelper(project);
	}

	public static List<Project> projects() throws ConfigurationException {
		checkConfiguration();
		return flagService.projects();
	}

	public String getProjectName() {
		return projectName;
	}

	public List<Flag> getAllFlags() {
		return flagService.projectFlags(projectName);
	}

	public Object getFlagValue(String flagName) {
		return flagService.readFlag(projectName, flagName);
	}

	public void deleteFlag(String flagName) {
		flagService.deleteFlag(projectName, flagName);
	}

	private static void checkConfiguration() throws ConfigurationException {
		if (flagService == null) {
			LOGGER.debug("[SubraFlagApp] -> {} not configured...", FlagAppHelper.class.getSimpleName());
			throw new ConfigurationException(null,
					"Either 'author' or 'publish' run modes may be specified, not both.");
		}
		LOGGER.debug("[SubraFlagApp] -> {} configured succesfully...", FlagAppHelper.class.getSimpleName());
	}

}