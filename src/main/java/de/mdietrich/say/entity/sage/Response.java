package de.mdietrich.say.entity.sage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Sage employee data
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {

	@JsonProperty(value = "Error")
	private String error;

	@JsonProperty(value = "BuchungsId")
	private String buchungsId;

	public String getBuchungsId() {
		return buchungsId;
	}

	public void setBuchungsId(String buchungsId) {
		this.buchungsId = buchungsId;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}


	@Override
	public String toString() {
		return "Response{" +
				"error='" + error + '\'' +
				", buchungsId='" + buchungsId + '\'' +
				'}';
	}
}
