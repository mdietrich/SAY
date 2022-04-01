package de.mdietrich.say.entity.sage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Sage user object
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

	@JsonProperty(value = "UserId")
	private int userId;

	@JsonProperty(value = "Employee")
	private Employee employee;

	@JsonProperty(value = "EmployeeKey")
	private EmployeeKey employeeKey;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public EmployeeKey getEmployeeKey() {
		return employeeKey;
	}

	public void setEmployeeKey(EmployeeKey employeeKey) {
		this.employeeKey = employeeKey;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", employee=" + employee + ", employeeKey=" + employeeKey + "]";
	}

}
