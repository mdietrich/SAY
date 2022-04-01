package de.mdietrich.say.entity.sage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Sage project time structure object
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectTimeStructure {

	@JsonProperty(value = "BookingStructureId")
	private int bookingStructureId;

	@JsonProperty(value = "BookingStructureParentId")
	private int bookingStructureParentId;

	@JsonProperty(value = "ProjectUnitId")
	private int projectUnitId;

	@JsonProperty(value = "ProjectUnit")
	private ProjectUnit projectUnit;
}
