package org.subra.aem.flagapp.internal.utils;

public class FlagAppUtils {

	private FlagAppUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static final String FLAP_APP_GET_API_PATTERN = "/api/subra/flagapp/v1/(?<target>project|flag)/(?<action>read|delete)(?:/(?<pname>[a-zA-Z0-9-_%\\s]+))?(?:/(?<fname>[a-zA-Z0-9-_%]+))?";
	public static final String FLAP_APP_POST_API_PATTERN = "/api/subra/flagapp/v1/(?<target>project|flag)/(?<action>create|update)";
	public static final String FLAG = "flag";
	public static final String TARGET = "target";
	public static final String ACTION = "action";
	public static final String PROJECT_NAME_GROUP = "pname";
	public static final String FLAG_NAME_GROUP = "fname";
	public static final String PATTERN_ERROR = "PATTERN_ERROR";
	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";
	public static final String RS_FAILURE_REASON = "REASON";
	public static final String RS_STATUS = "STATUS";
	public static final String RS_DATA = "data";

}