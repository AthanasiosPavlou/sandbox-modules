package com.blipnip.app.client.mainapp.utils.eureka;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface ResourcesBundle extends ClientBundle {
	ResourcesBundle INSTANCE = GWT.create(ResourcesBundle.class);

	@Source("com/blipnip/app/client/mainapp/utils/eureka/resources/timepicker_AM.png")
	ImageResource timePickerAM();

	@Source("com/blipnip/app/client/mainapp/utils/eureka/resources/timepicker_PM.png")
	ImageResource timePickerPM();

	@Source("com/blipnip/app/client/mainapp/utils/eureka/resources/timepicker_AM_small.png")
	ImageResource timePickerAMSmall();

	@Source("com/blipnip/app/client/mainapp/utils/eureka/resources/timepicker_PM_small.png")
	ImageResource timePickerPMSmall();
}