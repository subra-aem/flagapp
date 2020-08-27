package org.subra.aem.flagapp.internal.services;

import java.util.List;

import org.subra.aem.flagapp.internal.Flag;
import org.subra.aem.flagapp.internal.NewFlag;
import org.subra.aem.flagapp.internal.NewProject;
import org.subra.aem.flagapp.internal.Project;

/**
 * The Service Interface to create, update, read and delete flag
 * 
 * @author Raghava Joijode
 *
 */
public interface FlagService {
	void createOrUpdateFlag(final NewFlag newFlag);

	List<Flag> projectFlags(final String projectName);

	Object readFlag(final String projectName, final String flagName);

	void deleteFlag(final String projectName, final String flagName);

	Project createProject(final NewProject newProject);

	Project readProject(final String name);

	void deleteProject(final String name);

	List<Project> projects();

}
