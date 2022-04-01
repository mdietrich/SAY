package de.mdietrich.say.entity.say;

import java.math.BigDecimal;

/**
 * Single time table entry for SayTimeTable
 *
 */
public class SayTimetableEntry {

	private int projectId;

	private int activityId;

	private String day;

	private String begin;

	private String end;

	private BigDecimal amountHours = new BigDecimal(0);

	private String remarks;

	private String breakFrom;

	private String breakTo;

	private int breakAmountMinutes = 0;

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public BigDecimal getAmountHours() {
		return amountHours;
	}

	public void setAmountHours(BigDecimal amountHours) {
		this.amountHours = amountHours;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getBreakFrom() {
		return breakFrom;
	}

	public void setBreakFrom(String breakFrom) {
		this.breakFrom = breakFrom;
	}

	public String getBreakTo() {
		return breakTo;
	}

	public void setBreakTo(String breakTo) {
		this.breakTo = breakTo;
	}

	public String getBegin() {
		return begin;
	}

	public void setBegin(String begin) {
		this.begin = begin;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public int getBreakAmountMinutes() {
		return breakAmountMinutes;
	}

	public void setBreakAmountMinutes(int breakAmountMinutes) {
		this.breakAmountMinutes = breakAmountMinutes;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

	@Override
	public String toString() {
		return "SayTimetableEntry [projectId=" + projectId + ", activityId=" + activityId + ", day=" + day + ", begin=" + begin + ", end=" + end + ", amountHours=" + amountHours + ", remarks=" + remarks + ", breakFrom=" + breakFrom
				+ ", breakTo=" + breakTo + ", breakAmountMinutes=" + breakAmountMinutes + "]";
	}

}
