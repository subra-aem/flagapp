package org.subra.aem.flagapp.internal.helpers;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.aem.commons.helpers.ActivatonHelper;
import org.subra.aem.flagapp.internal.services.FlagService;

@Component(service = ActivatonHelper.class, immediate = true)
@ServiceDescription("FMS - Activator Helper")
public class ActivatonHelperImpl implements ActivatonHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActivatonHelperImpl.class);

	@Reference
	private FlagService flagService;

	@Activate
	private void activate() {
		// TODO: Delete if not required
	}

	@Deactivate
	private void deActivate() {
		flagService = null;
	}
}