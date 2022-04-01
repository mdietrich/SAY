package de.mdietrich.say.entity.say;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Own time table structure for easier handling than with sage (hope so at
 * least...)
 *
 */
public class SayTimetable {

	private Map<String, List<SayTimetableEntry>> days = new HashMap<>();

	public Map<String, List<SayTimetableEntry>> getDays() {
		return days;
	}

	@Override
	public String toString() {
		return "SayTimetable [days=" + days + "]";
	}

}
