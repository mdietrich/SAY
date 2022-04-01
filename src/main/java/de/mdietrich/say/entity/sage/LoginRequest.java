package de.mdietrich.say.entity.sage;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Sage login request
 *
 */
public class LoginRequest {

	@JsonProperty(value = "Key")
	private String key = "";

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return "ApiLoginJson [key=" + key + "]";
	}
}
