package de.mdietrich.say.entity.sage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Sage employee keys
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeKey {

	@JsonProperty(value = "MdNr")
	private int mdNr;

	@JsonProperty(value = "AnNr")
	private int anNr;

	@JsonProperty(value = "CombinedKey")
	private String combinedKey;

	@JsonProperty(value = "IsEmpty")
	private Boolean isEmpty = false;

	public int getMdNr() {
		return mdNr;
	}

	public void setMdNr(int mdNr) {
		this.mdNr = mdNr;
	}

	public int getAnNr() {
		return anNr;
	}

	public void setAnNr(int anNr) {
		this.anNr = anNr;
	}

	public String getCombinedKey() {
		return combinedKey;
	}

	public void setCombinedKey(String combinedKey) {
		this.combinedKey = combinedKey;
	}

	public Boolean getIsEmpty() {
		return isEmpty;
	}

	public void setIsEmpty(Boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	@Override
	public String toString() {
		return "ApiEmployeeKeysJson [mdNr=" + mdNr + ", anNr=" + anNr + ", combinedKey=" + combinedKey + ", isEmpty=" + isEmpty + "]";
	}

}
