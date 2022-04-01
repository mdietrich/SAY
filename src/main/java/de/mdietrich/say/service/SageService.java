package de.mdietrich.say.service;

import java.math.BigDecimal;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mdietrich.say.entity.configuration.Export;
import de.mdietrich.say.entity.sage.Break;
import de.mdietrich.say.entity.sage.EmployeeKey;
import de.mdietrich.say.entity.sage.LoginRequest;
import de.mdietrich.say.entity.sage.PortalLoginForm;
import de.mdietrich.say.entity.sage.Project;
import de.mdietrich.say.entity.sage.ProjectTime;
import de.mdietrich.say.entity.sage.ProjectUnit;
import de.mdietrich.say.entity.sage.Timetable;
import de.mdietrich.say.entity.sage.TimetableEntry;
import de.mdietrich.say.entity.sage.TimetableRequest;
import de.mdietrich.say.entity.sage.User;
import de.mdietrich.say.entity.say.SayTimetable;
import de.mdietrich.say.entity.say.SayTimetableEntry;
import de.mdietrich.say.entity.say.SayTimetableImportRow;
import de.mdietrich.say.exception.UnauthorizedException;
import de.mdietrich.say.service.consolidator.AutoPauseConsolidator;
import de.mdietrich.say.service.exporter.ExporterInterface;
import de.mdietrich.say.service.exporter.FiPdfExporter;
import us.codecraft.xsoup.Xsoup;

/**
 * This service handels communication with Sage
 *
 */
@Service
public class SageService {

	@Autowired
	private ConfigService configService;

	@Autowired
	private RequestService requestService;

	@Autowired
	private ImportService importService;

	@Autowired
	private AutoPauseConsolidator consolidator;

	@Autowired
	private FiPdfExporter fiPdfExporter;

	Logger logger = LoggerFactory.getLogger(SageService.class);

	private Boolean loggedIn = false;
	private User user;

	private final String portal = "/mportal";
	private final String portalLogin = "/mportal/Login.aspx";
	private final String apiAuthorization = "/hrportalapi/Authorization";
	private final String apiAuthInfo = "/hrportalapi/AuthorizationInfo";
	// private final String apiProjects = "/hrportalapi/Time/Project/NewProjects";
	private final String apiProjects = "/hrportalapi/Time/Project/Project/";
	private final String apiProjectUnits = "/hrportalapi/Time/Project/ProjectUnits";
	private final String apiProjectTime = "/hrportalapi/Time/Project/ProjectTime";
	private final String apiProjectTimeDelete = "/hrportalapi/Time/Project/ProjectTime";

	private Map<String, ExporterInterface> exporterMap = new HashMap<String, ExporterInterface>();

	/**
	 * Defines which exporter gets used for defined "exports" in config.json and
	 * parameter "type". New exporters need to be added here with unique key.
	 */
	@PostConstruct
	private void setup() {
		this.exporterMap.put("FI_PDF", fiPdfExporter);
	}

	/**
	 * Find the day number of the last day in the month of given date String in
	 * format YYYY-MM-DD
	 * 
	 * @param date
	 * @return
	 */
	private int findLastDayOfMonth(String date) {
		if (!date.matches("(\\d{4})-(\\d{2})")) {
			logger.error("Date must be given in format YYYY-MM");
			return 0;
		}
		date = date + "-01";
		LocalDate givenDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		LocalDate lastDayOfMonthDateGivenDate = givenDate.withDayOfMonth(givenDate.getMonth().length(givenDate.isLeapYear()));
		int lastDayOfMonth = lastDayOfMonthDateGivenDate.getDayOfMonth();
		return lastDayOfMonth;
	}

	/**
	 * Login to sage portal and api
	 * 
	 * @return
	 */
	public Boolean login() {
		logger.debug("Logging in...");

		if (loggedIn) {
			logger.debug("Already logged in.");
			return true;
		}

		// load login page
		String url = configService.getConfig().getServer() + this.portal;
		String html;
		try {
			html = requestService.getPage(url);
		} catch (UnauthorizedException e1) {
			logger.error("Error loading login page");
			this.loggedIn = false;
			return false;
		}

		// extract parameters
		Document document = Jsoup.parse(html);
		String viewState = Xsoup.compile("//input[@id=\"__VIEWSTATE\"]/@value").evaluate(document).get();
		String viewStateGenerator = Xsoup.compile("//input[@id=\"__VIEWSTATEGENERATOR\"]/@value").evaluate(document).get();
		String eventValidation = Xsoup.compile("//input[@id=\"__EVENTVALIDATION\"]/@value").evaluate(document).get();

		if (viewState == null || viewStateGenerator == null || eventValidation == null) {
			logger.error(html + "\n\n");
			logger.error("Data missing on homepage");
			logger.error("__VIEWSTATE = " + viewState);
			logger.error("__VIEWSTATEGENERATOR = " + viewStateGenerator);
			logger.error("__EVENTVALIDATION = " + eventValidation);
			this.loggedIn = false;
			return false;
		}

		// portal login
		url = configService.getConfig().getServer() + this.portalLogin;
		PortalLoginForm form = new PortalLoginForm();
		form.setViewState(viewState);
		form.setViewStateGenerator(viewStateGenerator);
		form.setEventValidation(eventValidation);
		form.setTxtUsername(configService.getConfig().getUsername());
		form.setTxtPassword(configService.getConfig().getPassword());

		try {
			html = requestService.postPage(url, form.toFormData(), 200, "application/x-www-form-urlencoded");
		} catch (UnauthorizedException e1) {
			logger.error("Could not login to sage portal");
			this.loggedIn = false;
			return false;
		}

		// find key
		document = Jsoup.parse(html);
		String script = Xsoup.compile("//script").evaluate(document).get();
		String[] rows = script.split("\n");
		String key = "";
		for (String row : rows) {
			if (row.strip().length() >= 4 && row.strip().substring(0, 4).equals("key:")) {
				key = row.split(":")[1].replace("'", "").replace(",", "").trim();
			}
		}
		if (key == "") {
			logger.error("Could not find session key after login.");
			this.loggedIn = false;
			return false;
		}

		// api login
		url = configService.getConfig().getServer() + this.apiAuthorization;
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setKey(key);
		ObjectMapper mapper = new ObjectMapper();
		String json;
		try {
			json = mapper.writeValueAsString(loginRequest);
		} catch (JsonProcessingException e) {
			logger.error("Could not parse json from api login");
			this.loggedIn = false;
			return false;
		}
		try {
			html = requestService.postPage(url, BodyPublishers.ofString(json), 204, "application/json");
		} catch (UnauthorizedException e) {
			logger.error("Could not login to sage api	");
			this.loggedIn = false;
			return false;
		}


		if (html.trim().equals("")) {
			this.loggedIn = true;
		}

		return this.loggedIn;
	}

	/**
	 * Get sage user data
	 * 
	 * @return
	 */
	public User getUserData() {
		logger.debug("Getting user data...");

		if (!loggedIn) {
			if (!this.login()) {
				return null;
			}
		}

		if (this.user != null) {
			return this.user;
		}

		String url = configService.getConfig().getServer() + this.apiAuthInfo + "?_=" + Instant.now().getEpochSecond();
		String json;
		try {
			json = requestService.getPage(url);
		} catch (UnauthorizedException e1) {
			this.loggedIn = false;
			return this.getUserData();
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			this.user = mapper.readValue(json, User.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			logger.error("Could not parse json");
		}

		return this.user;
	}

	/**
	 * Get sage projects
	 * 
	 * @return
	 */
	public List<Project> getProjects() {
		logger.debug("Getting projects...");

		if (!loggedIn) {
			this.login();
		}

		List<Project> projects = new ArrayList<Project>();
		String url = configService.getConfig().getServer() + this.apiProjects
				+ "?options%5BSkip%5D=0&options%5BTake%5D=50&options%5BSort%5D%5B0%5D%5BPropertyName%5D=Name&options%5BSort%5D%5B0%5D%5BOrderDirection%5D=Ascending&projectmember%5BAnNr%5D=7243&projectmember%5BMdNr%5D=6122&date="
				+ LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")) + "-01T00%3A00%3A00&filterOnProjectDescription=true&_="
				+ Instant.now().getEpochSecond() + "000";
		String json = "";
		try {
			json = requestService.getPage(url);
		} catch (UnauthorizedException e1) {
			this.loggedIn = false;
			return this.getProjects();
		}
		try {
			JsonNode node = new ObjectMapper().readTree(json);

			JsonNode jsonProjects = node.get("Results");
				for (JsonNode jsonProject : jsonProjects) {
					// build project object
					ObjectMapper mapper = new ObjectMapper();
					Project project = null;
					try {
						project = mapper.readValue(jsonProject.toString(), Project.class);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
						this.logger.error("Could not import project data");
						continue;
					}
					projects.add(project);
				}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			this.logger.error("Could not parse json");
		}
		return projects;
	}

	/**
	 * Get sage prject units
	 * 
	 * @param projectId
	 * @return
	 */
	public List<ProjectUnit> getProjectUnits(int projectId) {
		logger.debug("Getting activities for project id " + projectId + "...");

		if (!loggedIn) {
			this.login();
		}

		List<ProjectUnit> projectUnits = new ArrayList<ProjectUnit>();
		String dateFormatted = DateTimeFormatter.ofPattern("YYYY-MM-dd").format(ZonedDateTime.now());
		String url = configService.getConfig().getServer() + this.apiProjectUnits + "?projectId=" + projectId + "&dimension=0&date=" + dateFormatted + "T00%3A00%3A00&parentUnitId=" + projectId + "&_=" + Instant.now().getEpochSecond();
		String json;
		try {
			json = requestService.getPage(url);
		} catch (UnauthorizedException e1) {
			this.loggedIn = false;
			return this.getProjectUnits(projectId);
		}
		try {
			JsonNode node = new ObjectMapper().readTree(json);
			for (JsonNode jsonUnit : node) {
				ProjectUnit projectUnit = new ProjectUnit();
				projectUnit.setId(jsonUnit.get("Id").intValue());
				projectUnit.setName(jsonUnit.get("Name").textValue());
				projectUnit.setNumber(jsonUnit.get("Number").textValue());
				projectUnit.setActive(jsonUnit.get("Active").textValue());
				projectUnits.add(projectUnit);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			this.logger.error("Could not parse json");
		}
		return projectUnits;
	}

	/**
	 * Get sage time table for given date in format YYYY-MM-DD
	 * 
	 * @param date
	 * @return
	 */
	public Timetable getTimeTable(String date) {
		logger.debug("Getting timetable for month " + date + "...");
		int lastDayOfMonth = this.findLastDayOfMonth(date);

		if (!loggedIn) {
			this.login();
		}
		User user = this.getUserData();
		if (user == null) {
			return null;
		}

		String begin = date + "-01T00:00:00";
		String end = date + "-" + lastDayOfMonth + "T00:00:00";
		String url = configService.getConfig().getServer() + this.apiProjectTime + "?from=" + begin + "&to=" + end;

		// build data
		TimetableRequest timetableRequest = new TimetableRequest();
		EmployeeKey employeeKey = new EmployeeKey();
		employeeKey.setMdNr(user.getEmployeeKey().getMdNr());
		employeeKey.setAnNr(user.getEmployeeKey().getAnNr());
		employeeKey.setCombinedKey(user.getEmployeeKey().getCombinedKey());
		EmployeeKey[] employeeKeys = { employeeKey };
		timetableRequest.setEmployeeKeys(employeeKeys);

		// build json
		ObjectMapper mapper = new ObjectMapper();
		String jsonData;
		try {
			jsonData = mapper.writeValueAsString(employeeKey);
		} catch (JsonProcessingException e) {
			this.loggedIn = false;
			return this.getTimeTable(date);
		}

		// request
		String json;
		try {
			json = requestService.postPage(url, BodyPublishers.ofString(jsonData), 200, "application/json");
		} catch (UnauthorizedException e1) {
			this.loggedIn = false;
			return this.getTimeTable(date);
		}

		// build timetable object
		Timetable timetable = null;
		try {
			timetable = mapper.readValue(json, Timetable.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			this.logger.error("Could not parse json");
		}
		return timetable;
	}

	/**
	 * Find sage project unit for given projectId and activityId
	 * 
	 * @param projectId
	 * @param activityId
	 * @return
	 */
	private ProjectUnit findProjectUnitForProjectAndActivityId(int projectId, int activityId) {
		List<ProjectUnit> projectUnits = this.getProjectUnits(projectId);

		for (ProjectUnit projectUnit : projectUnits) {
			if (projectUnit.getId() == activityId) {
				return projectUnit;
			}
		}
		logger.error("Activity not found for project id " + projectId + " and activity id " + activityId);
		return null;
	}

	/**
	 * Build sage project time from say timetable entry
	 * 
	 * @param sayTimetableEntry
	 * @return
	 */
	private ProjectTime buildProjectTimeFromSayTimetableEntry(SayTimetableEntry sayTimetableEntry) {
		// make sure we have a session
		if (this.user == null) {
			this.getUserData();
		}

		// build projectTime
		ProjectTime projectTime = new ProjectTime();

		// add projectUnit
		ProjectUnit projectUnit = this.findProjectUnitForProjectAndActivityId(sayTimetableEntry.getProjectId(), sayTimetableEntry.getActivityId());
		if (projectUnit == null) {
			return null;
		}
		projectUnit.setDimension(1);
		projectTime.addProjectUnit(projectUnit);

		// add EmployeeKey
		projectTime.setEmployeeKey(this.user.getEmployeeKey());

		// optionally add break
		if (sayTimetableEntry.getBreakAmountMinutes() > 0) {
			Break b = new Break();
			b.setAmountMinutes(sayTimetableEntry.getBreakAmountMinutes());
			b.setFrom(sayTimetableEntry.getBreakFrom());
			b.setTo(sayTimetableEntry.getBreakTo());
			projectTime.addBreak(b);
		}

		projectTime.setProjectId(sayTimetableEntry.getProjectId());
		projectTime.setDay(sayTimetableEntry.getDay());
		projectTime.setTimeFrom(sayTimetableEntry.getBegin());
		projectTime.setTimeTo(sayTimetableEntry.getEnd());
		projectTime.setAmount(sayTimetableEntry.getAmountHours().multiply(new BigDecimal(60)).intValue());
		projectTime.setBuchungsId(0);
		projectTime.setMostRecentComment(sayTimetableEntry.getRemarks());

		logger.debug(projectTime.toString());

		return projectTime;
	}

	/**
	 * Save project time to sage
	 * 
	 * @param sayTimetableEntry
	 * @return
	 */
	private Boolean addProjectTime(SayTimetableEntry sayTimetableEntry) {

		logger.info("Adding project Time for " + sayTimetableEntry.getDay() + "...");

		if (!loggedIn) {
			if (!this.login()) {
				return false;
			}
		}

		// put
		String url = configService.getConfig().getServer() + this.apiProjectTime;
		ProjectTime projectTime = this.buildProjectTimeFromSayTimetableEntry(sayTimetableEntry);
		ObjectMapper mapper = new ObjectMapper();
		String json;
		try {
			json = mapper.writeValueAsString(projectTime);
		} catch (JsonProcessingException e) {
			logger.error("Could not parse json");
			return false;
		}
		String html = "";
		try {
			html = requestService.putPage(url, BodyPublishers.ofString(json), 200, "application/json");
		} catch (UnauthorizedException e1) {
			this.loggedIn = false;
			return this.addProjectTime(sayTimetableEntry);
		}
		logger.debug(html);

		return true;
	}

	/**
	 * Delete project time from sage
	 * 
	 * @param projectTimeId
	 * @return
	 */
	private Boolean deleteProjectTime(int projectTimeId) {
		logger.debug("Deleting project Time...");

		if (!loggedIn) {
			if (!this.login()) {
				return false;
			}
		}
		// delete from sage
		String url = configService.getConfig().getServer() + this.apiProjectTimeDelete + "?projectTimeId=" + projectTimeId;
		try {
			requestService.deletePage(url, 204, "application/json");
		} catch (UnauthorizedException e) {
			this.loggedIn = false;
			return this.deleteProjectTime(projectTimeId);
		}

		return true;
	}

	/**
	 * Remove all project times for given day from sage
	 * 
	 * @param timetable
	 * @param day
	 * @return
	 */
	private Boolean removeProjectTimeForDay(Timetable timetable, String day) {

		logger.info("Removing project times for " + day + "...");
		if (!loggedIn) {
			if (!this.login()) {
				return false;
			}
		}

		List<Integer> entryListToRemove = new ArrayList<>();
		int i = 0;
		for (TimetableEntry timetableEntry : timetable.getTimetableEntries()) {
			if (timetableEntry.getDay().equals(day)) {
				// delete from sage
				logger.info("Deleting timetable entry with buchungsId: " + timetableEntry.getBuchungsId());
				this.deleteProjectTime(timetableEntry.getBuchungsId());
				entryListToRemove.add(i);
			}
			i++;
		}

		// remove timetable entries from object
		Object[] entryListToRemoveReversed = entryListToRemove.toArray();
		ArrayUtils.reverse(entryListToRemoveReversed);
		for (Object pos : entryListToRemoveReversed) {
			TimetableEntry[] timetableEntries = ArrayUtils.remove(timetable.getTimetableEntries(), (Integer) pos);
			timetable.setTimetableEntries(timetableEntries);
		}

		return true;
	}

	/**
	 * Remove all project times for month with given date (YYYY-MM-DD) from sage
	 * 
	 * @param date
	 */
	public void removeProjectTimeForMonth(String date) {
		Timetable sageTimetable = this.getTimeTable(date);

		for (TimetableEntry entry : sageTimetable.getTimetableEntries()) {
			// delete from sage
			logger.info("Deleting timetable entry for buchungsId " + entry.getBuchungsId() + "...");
			this.deleteProjectTime(entry.getBuchungsId());
		}
	}

	/**
	 * Find new say entries missing in sage
	 * 
	 * @param sageTimetable
	 * @param sayTimetable
	 * @return
	 */
	private List<SayTimetableEntry> findNewSayEntries(Timetable sageTimetable, SayTimetable sayTimetable) {

		List<SayTimetableEntry> newSayEntries = new ArrayList<>();
		for (Entry<String, List<SayTimetableEntry>> sayDay : sayTimetable.getDays().entrySet()) {

			// iterate over sayEntries
			List<SayTimetableEntry> sayEntryList = sayDay.getValue();
			for (SayTimetableEntry sayEntry : sayEntryList) {

				// iterate over sage entries
				Boolean entryExists = false;
				if (sageTimetable == null) {
					return null;
				}
				for (TimetableEntry sageEntry : sageTimetable.getTimetableEntries()) {

					// check if day exists in sage timetable
					if (sayDay.getKey().equals(sageEntry.getDay().substring(0, 10))) {
						if (sayEntry.getProjectId() == sageEntry.getProjectId()) {
							if (sayEntry.getBegin().equals(sageEntry.getTimeFrom())) {
								if (sayEntry.getEnd().equals(sageEntry.getTimeTo())) {
									logger.debug("Time entry exists already: " + sageEntry.getProject().getName() + " - " + sageEntry.getTimeFrom() + " - " + sageEntry.getTimeTo());
									entryExists = true;
									break;
								}
							}
						}

					}
				}
				if (!entryExists) {
					// mark entry for addition
					newSayEntries.add(sayEntry);
				}
			}
		}
		return newSayEntries;
	}

	/**
	 * Find obsolete entries in sage
	 * 
	 * @param sageTimetable
	 * @param sayTimetable
	 * @return
	 */
	private List<String> findObsoleteSageEntryDays(Timetable sageTimetable, SayTimetable sayTimetable) {
		List<String> days = new ArrayList<>();
		for (TimetableEntry sageEntry : sageTimetable.getTimetableEntries()) {
			// check each sage entry for say entry
			Boolean sayEntryExists = false;
			for (Entry<String, List<SayTimetableEntry>> sayDay : sayTimetable.getDays().entrySet()) {
				// check if say entry exists
				if (sayDay.getKey().equals(sageEntry.getDay().substring(0, 10))) {
					// day found => check details
					List<SayTimetableEntry> sayEntryList = sayDay.getValue();
					for (SayTimetableEntry sayEntry : sayEntryList) {
						// check other parameters
						if (sayEntry.getProjectId() == sageEntry.getProjectId()) {
							if (sayEntry.getBegin().equals(sageEntry.getTimeFrom())) {
								if (sayEntry.getEnd().equals(sageEntry.getTimeTo())) {
									// matching entry found
									sayEntryExists = true;
									break;
								}
							}
						}
					}
				}
			}
			if (!sayEntryExists) {
				// say entry missing => add to list for later deletion of whole day
				logger.debug("Sage entry obsolete: " + sageEntry.getProject().getName() + " - " + sageEntry.getTimeFrom() + " - " + sageEntry.getTimeTo());
				String day = sageEntry.getDay();
				if (!days.contains(day)) {
					days.add(day);
				}
			}
		}

		return days;
	}

	/**
	 * Check if Sage timetable has entries for day
	 * 
	 * @param sageTimetable
	 * @param day
	 * @return
	 */
	private Boolean sageHasEntryForDay(Timetable sageTimetable, String day) {
		for (TimetableEntry entry : sageTimetable.getTimetableEntries()) {
			if (entry.getDay().equals(day)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Write say timetable to sage an fix differences
	 * 
	 * @param sageTimetable
	 * @param sayTimetable
	 */
	private void writeTimetableToSage(Timetable sageTimetable, SayTimetable sayTimetable) {

		List<SayTimetableEntry> newSayEntries = new ArrayList<>();

		// find days with obsolete entries
		List<String> days = this.findObsoleteSageEntryDays(sageTimetable, sayTimetable);
		for (String day : days) {
			// remove all entries for day
			this.removeProjectTimeForDay(sageTimetable, day);
		}

		// find new entries in cleaned sageTimetable
		newSayEntries = this.findNewSayEntries(sageTimetable, sayTimetable);
		if (newSayEntries == null) {
			// no new entries found
			return;
		} else {
			days.clear();

			// clear day before entering new data in Sage
			for (SayTimetableEntry entry : newSayEntries) {
				if(this.sageHasEntryForDay(sageTimetable, entry.getDay())) {
					this.removeProjectTimeForDay(sageTimetable, entry.getDay());
				}
			}
		}

		// add new entries to sage
		for (SayTimetableEntry sayTimetableEntry : newSayEntries) {
			this.addProjectTime(sayTimetableEntry);
		}
	}

	/**
	 * Import external data (eg. from csv)
	 * 
	 * @param data
	 */
	public void importExternalData(String data) {
		// read csv
		List<SayTimetableImportRow> importRowList = importService.read(data);
		if (importRowList.size() == 0)
			return;

		// read sage timetable for given month
		String date = importRowList.get(0).getDate().substring(0, 7);
		Timetable sageTimetable = this.getTimeTable(date);

		// build consolidated timetable
		SayTimetable sayTimetable = consolidator.consolidate(importRowList);

		// compare timetables and write to sage
		this.writeTimetableToSage(sageTimetable, sayTimetable);
	}

	/**
	 * Build map of exports with list of timetable entries
	 * 
	 * @param timetable
	 * @return
	 */
	private Map<String, List<TimetableEntry>> buildExportLists(Timetable timetable) {
		Map<String, List<TimetableEntry>> exportEntryMap = new HashMap<>();
		for (TimetableEntry entry : timetable.getTimetableEntries()) {
			// get export name from config for project id
			String exportname = this.configService.getConfig().getProjectForProjectId(entry.getProjectId()).getExport();

			// add timetable entry
			if (exportname != null) {
				if(!exportEntryMap.containsKey(exportname)) {
					exportEntryMap.put(exportname, new ArrayList<>());
				}
				exportEntryMap.get(exportname).add(entry);
			}
		}
		return exportEntryMap;
	}

	/**
	 * Export timetable data for external accounting
	 * 
	 * @param timetable
	 */
	public void exportTimetable(Timetable timetable) {
		Map<String, List<TimetableEntry>> exportList = this.buildExportLists(timetable);

		for (Map.Entry<String, List<TimetableEntry>> exportMap : exportList.entrySet()) {
			String exportName = exportMap.getKey();
			List<TimetableEntry> entryList = exportMap.getValue();

			// get export configuration
			if (!this.configService.getConfig().getExports().containsKey(exportName)) {
				this.logger.error("Could not find export with name '" + exportName + "'");
				continue;
			}
			Export exportConfiguration = this.configService.getConfig().getExports().get(exportName);

			// export with matching exporter
			if (!this.exporterMap.containsKey(exportConfiguration.getType())) {
				this.logger.error("Could not find export type with name '" + exportConfiguration.getType() + "'");
				continue;
			}

			// call exporter
			ExporterInterface exporter = this.exporterMap.get(exportConfiguration.getType());
			exporter.export(entryList, exportConfiguration);
		}

	}
}
