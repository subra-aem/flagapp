package org.subra.aem.flagapp.internal;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class NewFlag {

	private String projectName;

	private String flagTitle;

	private String flagValue;

	private String flagType;

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getFlagTitle() {
		return flagTitle;
	}

	public void setFlagTitle(String flagTitle) {
		this.flagTitle = flagTitle;
	}

	public Object getFlagValue() {
		return StringUtils.equalsIgnoreCase(getFlagType(), "Boolean") ? BooleanUtils.toBoolean(flagValue) : flagValue;
	}

	public void setFlagValue(String flagValue) {
		this.flagValue = flagValue;
	}

	public String getFlagType() {
		return flagType;
	}

	public void setFlagType(String flagType) {
		this.flagType = flagType;
	}

}
