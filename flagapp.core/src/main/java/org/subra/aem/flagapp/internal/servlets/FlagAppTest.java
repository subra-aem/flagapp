package org.subra.aem.flagapp.internal.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.subra.aem.flagapp.helpers.FlagAppHelper;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Simple Demo Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET,
		"sling.servlet.paths=" + "/bin/subra/flagapptest" }, enabled = true)
public class FlagAppTest extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/plain");
		final String p = StringUtils.defaultIfBlank(req.getParameter("p"), "def");
		if (StringUtils.equalsIgnoreCase(p, "test")) {
			try {
				resp.getWriter().write("value is ..."
						+ FlagAppHelper.build(req.getParameter("project")).getFlagValue(req.getParameter("flag")));
			} catch (Exception e) {
				resp.getWriter().write(e.getMessage());
			}

		} else
			resp.getWriter()
					.write("Welcome... \nAdd parameter to test '?p=test&project=<project-name>&flag=<flag-name>'");
	}
}
