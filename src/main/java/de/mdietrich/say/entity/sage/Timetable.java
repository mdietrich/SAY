package de.mdietrich.say.entity.sage;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Sage time table object
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Timetable {

	// amount in hours
	@JsonProperty(value = "TotalAmount")
	private int totalAmount;

	// amount in hours
	@JsonProperty(value = "TotalAmountBreaks")
	private int totalAmountBreaks;

	// Number of entries
	@JsonProperty(value = "Total")
	private int total;

	@JsonProperty(value = "Results")
	private TimetableEntry[] timetableEntries;

	public int getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}

	public int getTotalAmountBreaks() {
		return totalAmountBreaks;
	}

	public void setTotalAmountBreaks(int totalAmountBreaks) {
		this.totalAmountBreaks = totalAmountBreaks;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public TimetableEntry[] getTimetableEntries() {
		return timetableEntries;
	}

	public void setTimetableEntries(TimetableEntry[] timetableEntries) {
		this.timetableEntries = timetableEntries;
	}

	@Override
	public String toString() {
		return "Timetable [totalAmount=" + totalAmount + ", totalAmountBreaks=" + totalAmountBreaks + ", total=" + total + ", timetableEntries=" + Arrays.toString(timetableEntries) + "]";
	}

}