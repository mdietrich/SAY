package de.mdietrich.say.entity.sage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Sage employee data
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee {

	@JsonProperty(value = "Firstname")
	private String firstname;

	@JsonProperty(value = "Lastname")
	private String lastname;

	@JsonProperty(value = "Fullname")
	private String fullname;

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	@Override
	public String toString() {
		return "Username [firstname=" + firstname + ", lastname=" + lastname + ", fullname=" + fullname + "]";
	}

}
