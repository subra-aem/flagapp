package org.subra.aem.flagapp.models;

import java.util.List;

import org.subra.aem.flagapp.internal.Flag;
import org.subra.aem.flagapp.internal.Project;

public interface FMSModel {

	default String getProject() {
		throw new UnsupportedOperationException();
	}

	default List<Project> getProjects() {
		throw new UnsupportedOperationException();
	}

	default List<Flag> getFlags() {
		throw new UnsupportedOperationException();
	}

	default Object getMessage() {
		throw new UnsupportedOperationException();
	}

}
