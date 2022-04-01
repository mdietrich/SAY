package de.mdietrich.say.entity.sage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Sage breaks
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Break {

	@JsonProperty(value = "Id")
	private int id;

	@JsonProperty(value = "BookingId")
	private int bookingId;

	@JsonProperty(value = "From")
	private String from;

	@JsonProperty(value = "To")
	private String to;

	@JsonProperty(value = "Amount")
	private int amountMinutes;

	@JsonProperty(value = "_hasError")
	private Boolean hasError = false;

	@JsonProperty(value = "_error")
	private String error = "";

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBookingId() {
		return bookingId;
	}

	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public int getAmountMinutes() {
		return amountMinutes;
	}

	public void setAmountMinutes(int amountMinutes) {
		this.amountMinutes = amountMinutes;
	}

	public Boolean getHasError() {
		return hasError;
	}

	public void setHasError(Boolean hasError) {
		this.hasError = hasError;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "Break [id=" + id + ", bookingId=" + bookingId + ", from=" + from + ", to=" + to + ", amountMinutes=" + amountMinutes + ", hasError=" + hasError + ", error=" + error + "]";
	}

}
