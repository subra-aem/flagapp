package org.subra.aem.flagapp.internal;

public class Flag extends FlagApp {

	private boolean isBoolean;

	private Object value;

	public boolean isBoolean() {
		return isBoolean;
	}

	public void setBoolean(boolean isBoolean) {
		this.isBoolean = isBoolean;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
