package de.mdietrich.say.entity.sage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Sage project unit object
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectUnit {

	public ProjectUnit() {
		super();
	}

	public ProjectUnit(int id, String name, String number, String active, int dimension) {
		super();
		this.id = id;
		this.name = name;
		this.number = number;
		this.active = active;
		this.dimension = dimension;
	}

	@JsonProperty(value = "Id")
	private int id;

	@JsonProperty(value = "Name")
	private String name;

	@JsonProperty(value = "Number")
	private String number;

	@JsonProperty(value = "Active")
	private String active;

	@JsonProperty(value = "dimension")
	private int dimension;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public int getDimension() {
		return dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	@Override
	public String toString() {
		return "ProjectUnit [id=" + id + ", name=" + name + ", number=" + number + ", active=" + active + ", dimension=" + dimension + "]";
	}
}
