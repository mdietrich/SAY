package de.mdietrich.say.entity.sage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Sage project time object
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectTime {

	@JsonProperty(value = "ProjectUnits")
	private ProjectUnit[] projectUnits = new ProjectUnit[0];

	@JsonProperty(value = "Breaks")
	private Break[] breaks = new Break[0];

	@JsonProperty(value = "ProjectId")
	private int projectId;

	@JsonProperty(value = "EmployeeKey")
	private EmployeeKey employeeKey;

	@JsonProperty(value = "Day")
	private String day;

	@JsonProperty(value = "TimeFrom")
	private String timeFrom;

	@JsonProperty(value = "TimeTo")
	private String timeTo;

	@JsonProperty(value = "Amount")
	private int amount;

	@JsonProperty(value = "BuchungsId")
	private int buchungsId;

	@JsonProperty(value = "MostRecentcomment")
	private String mostRecentComment;

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

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public EmployeeKey getEmployeeKey() {
		return employeeKey;
	}

	public void setEmployeeKey(EmployeeKey employeeKey) {
		this.employeeKey = employeeKey;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
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

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getBuchungsId() {
		return buchungsId;
	}

	public void setBuchungsId(int buchungsId) {
		this.buchungsId = buchungsId;
	}

	public String getMostRecentComment() {
		return mostRecentComment;
	}

	public void setMostRecentComment(String mostRecentComment) {
		this.mostRecentComment = mostRecentComment;
	}

	@Override
	public String toString() {
		return "ProjectTime [projectUnits=" + Arrays.toString(projectUnits) + ", breaks=" + Arrays.toString(breaks) + ", projectId=" + projectId + ", employeeKey=" + employeeKey + ", day=" + day + ", timeFrom=" + timeFrom + ", timeTo="
				+ timeTo + ", amount=" + amount + ", buchungsId=" + buchungsId + ", mostRecentComment=" + mostRecentComment + "]";
	}

	public void addProjectUnit(ProjectUnit item) {
		ProjectUnit[] array = this.getProjectUnits();
		List<ProjectUnit> list = new ArrayList<ProjectUnit>(Arrays.asList(array));
		list.add(item);
		this.setProjectUnits(list.toArray(array));
	}

	public void addBreak(Break item) {
		Break[] array = this.getBreaks();
		List<Break> list = new ArrayList<Break>(Arrays.asList(array));
		list.add(item);
		this.setBreaks(list.toArray(array));
	}
}
