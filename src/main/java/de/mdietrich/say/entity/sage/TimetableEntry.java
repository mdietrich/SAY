package de.mdietrich.say.entity.sage;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Sage time table entry
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimetableEntry {

	@JsonProperty(value = "BuchungsId")
	private int buchungsId;

	@JsonProperty(value = "ProjectId")
	private int projectId;

	// "2022-03-01T07:36:00"
	@JsonProperty(value = "Day")
	private String day;

	@JsonProperty(value = "Amount")
	private int amountMinutes;

	// "2022-03-01T07:36:00"
	@JsonProperty(value = "TimeFrom")
	private String timeFrom;

	// "2022-03-01T10:36:00"
	@JsonProperty(value = "TimeTo")
	private String timeTo;

	@JsonProperty(value = "ProjectTimeStructure")
	private ProjectTimeStructure projectTimeStructure;

	@JsonProperty(value = "Project")
	private Project project;

	@JsonProperty(value = "Remark")
	private String remark;

	@JsonProperty(value = "ProjectUnits")
	private ProjectUnit[] projectUnits;

	@JsonProperty(value = "Breaks")
	private Break[] breaks;

	public int getBuchungsId() {
		return buchungsId;
	}

	public void setBuchungsId(int buchungsId) {
		this.buchungsId = buchungsId;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public int getAmountMinutes() {
		return amountMinutes;
	}

	public void setAmountMinutes(int amountMinutes) {
		this.amountMinutes = amountMinutes;
	}

	public ProjectTimeStructure getProjectTimeStructure() {
		return projectTimeStructure;
	}

	public void setProjectTimeStructure(ProjectTimeStructure projectTimeStructure) {
		this.projectTimeStructure = projectTimeStructure;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		// remove author from remark (sage export)
		// eg "[MMustermann - 15.03.2022 10:05] Dies und das gemacht"
		remark = remark.replaceAll("\\[.* - \\d{2}\\.\\d{2}\\.\\d{4} \\d{2}\\:\\d{2}\\] ", "");
		this.remark = remark;
	}

	public ProjectUnit[] getProjectUnits() {
		return projectUnits;
	}

	public void setProjectUnits(ProjectUnit[] projectUnits) {
		this.projectUnits = projectUnits;
	}

	public Break[] getBreaks() {
		return breaks;
	}

	public void setBreaks(Break[] breaks) {
		this.breaks = breaks;
	}

	public String getTimeFrom() {
		return timeFrom;
	}

	public void setTimeFrom(String timeFrom) {
		this.timeFrom = timeFrom;
	}

	public String getTimeTo() {
		return timeTo;
	}

	public void setTimeTo(String timeTo) {
		this.timeTo = timeTo;
	}

	@Override
	public String toString() {
		return "TimetableEntry [buchungsId=" + buchungsId + ", projectId=" + projectId + ", day=" + day + ", amountMinutes=" + amountMinutes + ", timeFrom=" + timeFrom + ", timeTo=" + timeTo + ", projectTimeStructure="
				+ projectTimeStructure + ", project=" + project + ", remark=" + remark + ", projectUnits=" + Arrays.toString(projectUnits) + ", breaks=" + Arrays.toString(breaks) + "]";
	}

	public int getBreakAmountMinutes() {
		int amountMinutes = 0;
		for (Break b : breaks) {
			amountMinutes += b.getAmountMinutes();
		}
		return amountMinutes;
	}
}
