package de.mdietrich.say.entity.say;

import java.math.BigDecimal;

/**
 * Structure for csv import. Write your own importer in service.importer
 *
 */
public class SayTimetableImportRow {

	/**
	 * For example 2022-02-25
	 */
	private String date;

	/**
	 * For example 07:00
	 */
	private String begin;

	/**
	 * For example 16:00
	 */
	private String end;

	private String company;

	private String project;

	/**
	 * For example 8.0
	 */
	private BigDecimal amountHours;

	private String remarks;

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

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "CsvRow [date=" + date + ", begin=" + begin + ", end=" + end + ", company=" + company + ", project=" + project + ", amountHours=" + amountHours + ", remarks=" + remarks + "]";
	}

}
