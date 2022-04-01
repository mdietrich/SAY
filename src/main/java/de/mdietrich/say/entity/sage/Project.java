package de.mdietrich.say.entity.sage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Sage project object
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {

	@JsonProperty(value = "Id")
	private int id;

	@JsonProperty(value = "ProjectStructureID")
	private int projectStrucutreId;

	@JsonProperty(value = "Number")
	private String number;

	@JsonProperty(value = "Name")
	private String name;

	@JsonProperty(value = "ProjectState")
	private String projectState;

	@JsonProperty(value = "TimeAcquisitionMode")
	private String timeAcquisitionMode;

	@JsonProperty(value = "PlannedProjectStart")
	private String plannedProjectStart;

	@JsonProperty(value = "PlannedEndOfTheProject")
	private String plannedProjectEnd;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProjectStrucutreId() {
		return projectStrucutreId;
	}

	public void setProjectStrucutreId(int projectStrucutreId) {
		this.projectStrucutreId = projectStrucutreId;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProjectState() {
		return projectState;
	}

	public void setProjectState(String projectState) {
		this.projectState = projectState;
	}

	public String getTimeAcquisitionMode() {
		return timeAcquisitionMode;
	}

	public void setTimeAcquisitionMode(String timeAcquisitionMode) {
		this.timeAcquisitionMode = timeAcquisitionMode;
	}

	public String getPlannedProjectStart() {
		return plannedProjectStart;
	}

	public void setPlannedProjectStart(String plannedProjectStart) {
		this.plannedProjectStart = plannedProjectStart;
	}

	public String getPlannedProjectEnd() {
		return plannedProjectEnd;
	}

	public void setPlannedProjectEnd(String plannedProjectEnd) {
		this.plannedProjectEnd = plannedProjectEnd;
	}

	@Override
	public String toString() {
		return "Project [id=" + id + ", projectStrucutreId=" + projectStrucutreId + ", number=" + number + ", name=" + name + ", projectState=" + projectState + ", timeAcquisitionMode=" + timeAcquisitionMode + ", plannedProjectStart="
				+ plannedProjectStart + ", plannedProjectEnd=" + plannedProjectEnd + "]";
	}

}
