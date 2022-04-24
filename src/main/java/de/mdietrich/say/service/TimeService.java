package de.mdietrich.say.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalTime;

@Service
public class TimeService {

	Logger logger = LoggerFactory.getLogger(TimeService.class);

	public LocalTime timeStringToTime(String time) {
		int timeH = Integer.parseInt(time.split(":")[0]);
		int timeM = Integer.parseInt(time.split(":")[1]);
		LocalTime t = LocalTime.of(timeH, timeM);
		return t;
	}

	public String addHoursToDateTime(String dateTime, BigDecimal hours) {
		String time = dateTime.substring(11, 16);
		LocalTime t = timeStringToTime(time);

		String[] hoursParts = hours.toString().split("[.]");
		long hoursH = Long.parseLong(hoursParts[0]);
		BigDecimal hoursMBigDecimal = hours.subtract(new BigDecimal(hoursH));
		long hoursM = hoursMBigDecimal.multiply(new BigDecimal(60)).longValue();
		t = t.plusHours(hoursH);
		t = t.plusMinutes(hoursM);

		String newTime = dateTime.substring(0, 10) + "T" + t.toString() + ":00";

		return newTime;
	}

	public Boolean isTimeABeforeTimeB(String timeA, String timeB) {
		LocalTime tA = timeStringToTime(timeA);
		LocalTime tB = timeStringToTime(timeB);
		return tA.isBefore(tB);
	}

	public BigDecimal calculateHoursDuration(String timeFrom, String timeTo) {
		LocalTime from = timeStringToTime(timeFrom);
		LocalTime to = timeStringToTime(timeTo);
		Long minutes = Duration.between(from, to).toMinutes();
		return new BigDecimal(minutes).divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
	}

	public String germanDateStringToDateString(String date) {
		StringBuilder result = new StringBuilder();
		if(date.length() == 8) {
			result.append("20")
				.append(date.substring(6, 8))
				.append("-")
				.append(date.substring(3, 5))
				.append("-")
				.append(date.substring(0, 2));
		} else if(date.length() == 10) {
			result.append(date.substring(6,10))
				.append("-")
				.append(date.substring(3, 5))
				.append("-")
				.append(date.substring(0, 2));
		} else {
			logger.error("Unsupported date format '" + date +"'");
		}
		return result.toString();
	}
}
