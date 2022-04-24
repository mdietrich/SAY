package de.mdietrich.say.service.consolidator;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.mdietrich.say.service.TimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mdietrich.say.entity.configuration.Project;
import de.mdietrich.say.entity.say.SayTimetable;
import de.mdietrich.say.entity.say.SayTimetableEntry;
import de.mdietrich.say.entity.say.SayTimetableImportRow;
import de.mdietrich.say.service.ConfigService;

/**
 * Consolidates csv rows to DayTimeTable for month. Timetable entries get
 * consolidated per day an project. Afterwards they get stuck together
 * sequentially starting with the first starttime. A default pause with
 * "pauseDuration" from config.json gets added at "pauseStart" every day.
 *
 * This class can be exchanged for other time recording rules.
 */
@Service
public class AutoPauseConsolidator implements ConsolidatorInterface {

	@Autowired
	private ConfigService configService;

	@Autowired
	private TimeService timeService;

	Logger logger = LoggerFactory.getLogger(AutoPauseConsolidator.class);

	SayTimetable sayTimetable = new SayTimetable();

	private Project getProjectConfig(String companyName, String projectName) {
		Map<String, Project> projectMap = configService.getConfig().getProjects();
		String value = companyName + " - " + projectName;
		for (Map.Entry<String, Project> entry : projectMap.entrySet()) {
			if (entry.getKey().equals(value)) {
				return entry.getValue();
			}
		}
		logger.error("Could not find configuration for company '" + companyName + "' and project '" + projectName + "'. Please update config.json.");
		return null;
	}

	private int getProjectIdByCompanyAndProject(String companyName, String projectName) {

		Project project = this.getProjectConfig(companyName, projectName);
		if (project == null) {
			return 0;
		}
		return project.getProjectId();
	}

	private int getActivityIdByCompanyAndProject(String companyName, String projectName) {

		Project project = this.getProjectConfig(companyName, projectName);
		if (project == null) {
			return 0;
		}
		return project.getActivityId();
	}



	private String addHoursToTime(String time, BigDecimal hours) {
		LocalTime t = timeService.timeStringToTime(time);

		String[] hoursParts = hours.toString().split("[.]");
		long hoursH = Long.valueOf(hoursParts[0]);
		BigDecimal hoursMBigDecimal = hours.subtract(new BigDecimal(hoursH));
		long hoursM = hoursMBigDecimal.multiply(new BigDecimal(60)).longValue();
		t = t.plusHours(hoursH);
		t = t.plusMinutes(hoursM);

		String newTime = t.toString();

		return newTime;
	}



	private Boolean shouldHaveBreak(String begin, String end) {
		String breakStart = this.configService.getConfig().getPauseStart();
		if (timeService.isTimeABeforeTimeB(begin.substring(11, 16), breakStart)
				&& timeService.isTimeABeforeTimeB(breakStart, end.substring(11, 16))) {
			return true;
		}
		return false;
	}

	private SayTimetableEntry updateEntry(SayTimetableEntry existingEntry, SayTimetableEntry newEntry) {

		// update hours
		existingEntry.setAmountHours(existingEntry.getAmountHours().add(newEntry.getAmountHours()));

		// update end
		String newEnd = timeService.addHoursToDateTime(existingEntry.getEnd(), newEntry.getAmountHours());
		existingEntry.setEnd(newEnd);

		// update remarks
		String newRemarks = newEntry.getRemarks().trim();
		if (newRemarks.length() > 0) {
			if (existingEntry.getRemarks().trim().length() > 0) {
				existingEntry.setRemarks(existingEntry.getRemarks().trim() + ", " + newRemarks);
			} else {
				existingEntry.setRemarks(newRemarks);
			}
		}

		return existingEntry;
	}

	private void addEntryForDay(String day, SayTimetableEntry entry) {

		if (!this.sayTimetable.getDays().containsKey(day)) {
			// add new list for new day
			List<SayTimetableEntry> entryList = new ArrayList<>();
			entryList.add(entry);
			this.sayTimetable.getDays().put(day, entryList);

		} else {
			// existing day: check if project exists
			List<SayTimetableEntry> entryList = this.sayTimetable.getDays().get(day);
			Boolean projectExists = false;
			SayTimetableEntry existingEntry = null;
			for (SayTimetableEntry sayTimetableEntry : entryList) {
				if (sayTimetableEntry.getProjectId() == entry.getProjectId()) {
					projectExists = true;
					existingEntry = sayTimetableEntry;
					break;
				}
			}

			if (!projectExists) {
				// add new project entry for day
				entryList.add(entry);

			} else {
				// update existing project entry for day
				this.updateEntry(existingEntry, entry);

			}
		}
	}

	private void serializeDailyEntries(List<SayTimetableEntry> entries) {
		String lastEnd = "";
		for (SayTimetableEntry entry : entries) {
			if (!lastEnd.equals("")) {
				// fix times
				String begin = lastEnd;
				String end = timeService.addHoursToDateTime(begin, entry.getAmountHours());
				lastEnd = end;
				entry.setBegin(begin);
				entry.setEnd(end);
			} else {
				lastEnd = entry.getEnd();
			}
		}
	}

	private void addBreakForDayEntries(List<SayTimetableEntry> entries) {
		Boolean dayHasBreak = false;
		int breakDurationMinutes = Integer.valueOf(configService.getConfig().getPauseDuration());
		BigDecimal breakDurationHours = new BigDecimal(breakDurationMinutes).divide(new BigDecimal(60));
		String breakStart = this.configService.getConfig().getPauseStart();
		for (SayTimetableEntry entry : entries) {
			de.mdietrich.say.entity.configuration.Project projectConfig = this.configService.getConfig().getProjectForProjectId(entry.getProjectId());
			if (!dayHasBreak && projectConfig.getAddBreak()) {
				// no break so far
				if (this.shouldHaveBreak(entry.getBegin(), entry.getEnd())) {

					// set break
					String breakEnd = this.addHoursToTime(breakStart, breakDurationHours);
					entry.setBreakFrom(breakStart);
					entry.setBreakTo(breakEnd);
					entry.setBreakAmountMinutes(breakDurationMinutes);

					// fix end
					String end = timeService.addHoursToDateTime(entry.getEnd(), breakDurationHours);
					entry.setEnd(end);
					dayHasBreak = true;

					// add work time
					entry.setAmountHours(entry.getAmountHours().add(breakDurationHours));
				}

			} else {
				// fix times of following project on that day after break
				String begin = timeService.addHoursToDateTime(entry.getBegin(), breakDurationHours);
				String end = timeService.addHoursToDateTime(entry.getEnd(), breakDurationHours);
				entry.setBegin(begin);
				entry.setEnd(end);
			}
		}
	}

	public SayTimetable consolidate(List<SayTimetableImportRow> importRowList) {

		// reset timetable
		this.sayTimetable = new SayTimetable();

		// reduce to one project per day
		for (SayTimetableImportRow row : importRowList) {
			SayTimetableEntry entry = new SayTimetableEntry();

			int projectId = this.getProjectIdByCompanyAndProject(row.getCompany(), row.getProject());
			if (projectId == 0) {
				continue;
			}

			int activityId = this.getActivityIdByCompanyAndProject(row.getCompany(), row.getProject());
			if (activityId == 0) {
				continue;
			}

			entry.setProjectId(projectId);
			entry.setActivityId(activityId);
			entry.setDay(row.getDate() + "T" + row.getBegin() + ":00");
			entry.setBegin(row.getDate() + "T" + row.getBegin() + ":00");
			entry.setEnd(row.getDate() + "T" + row.getEnd() + ":00");
			entry.setAmountHours(row.getAmountHours());
			entry.setRemarks(row.getRemarks());
			entry.setBreakAmountMinutes(0);

			this.addEntryForDay(row.getDate(), entry);
		}

		for (Map.Entry<String, List<SayTimetableEntry>> day : this.sayTimetable.getDays().entrySet()) {

			// serialize daily entries
			this.serializeDailyEntries(day.getValue());

			// add daily break
			this.addBreakForDayEntries(day.getValue());
		}

		// make sure, we have a remark
		for (Map.Entry<String, List<SayTimetableEntry>> day : this.sayTimetable.getDays().entrySet()) {
			for (SayTimetableEntry entry : day.getValue()) {
				if (entry.getRemarks().trim().equals("")) {
					// get default description from config
					int projectId = entry.getProjectId();
					Project projectConfig = this.configService.getConfig().getProjectForProjectId(projectId);
					entry.setRemarks(projectConfig.getDefaultDescription());
				}

			}
		}

		return sayTimetable;
	}
}
