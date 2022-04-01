package de.mdietrich.say.entity.sage;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Sage get time table request
 *
 */
public class TimetableRequest {

	@JsonProperty(value = "Sort")
	private String[] sort;

	@JsonProperty(value = "EmployeeKeys")
	private EmployeeKey[] employeeKeys;

	public String[] getSort() {
		return sort;
	}

	public void setSort(String[] sort) {
		this.sort = sort;
	}

	public EmployeeKey[] getEmployeeKeys() {
		return employeeKeys;
	}

	public void setEmployeeKeys(EmployeeKey[] employeeKeys) {
		this.employeeKeys = employeeKeys;
	}

	@Override
	public String toString() {
		return "ApiGetTimetableJson [sort=" + Arrays.toString(sort) + ", employeeKeys=" + Arrays.toString(employeeKeys) + "]";
	}

}
