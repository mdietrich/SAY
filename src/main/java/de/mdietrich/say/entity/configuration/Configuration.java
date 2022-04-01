package de.mdietrich.say.entity.configuration;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mdietrich.say.service.SageService;

/**
 * This objects holds the configuration data from config.json
 *
 */
public class Configuration {

	Logger logger = LoggerFactory.getLogger(SageService.class);

	private String server;
	private String username;
	private String password;
	private String pauseStart;
	private String pauseDuration;
	private String firstname;
	private String lastname;
	private int dailyHours;
	private Map<String, Project> projects;
	private Map<String, Export> exports;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPauseStart() {
		return pauseStart;
	}

	public void setPauseStart(String pauseStart) {
		this.pauseStart = pauseStart;
	}

	public String getPauseDuration() {
		return pauseDuration;
	}

	public void setPauseDuration(String pauseDuration) {
		this.pauseDuration = pauseDuration;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public int getDailyHours() {
		return dailyHours;
	}

	public void setDailyHours(int dailyHours) {
		this.dailyHours = dailyHours;
	}

	public Map<String, Project> getProjects() {
		return projects;
	}

	public void setProjects(Map<String, Project> projects) {
		this.projects = projects;
	}

	public Map<String, Export> getExports() {
		return exports;
	}

	public void setExports(Map<String, Export> exports) {
		this.exports = exports;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	@Override
	public String toString() {
		return "Configuration [server=" + server + ", username=" + username + ", password=" + password + ", pauseStart=" + pauseStart + ", pauseDuration=" + pauseDuration + ", firstname=" + firstname + ", lastname=" + lastname
				+ ", dailyHours=" + dailyHours + ", projects=" + projects + ", exports=" + exports + "]";
	}

	public Project getProjectForProjectId(int projectId) {
		for (Map.Entry<String, Project> projectMap : this.getProjects().entrySet()) {
			if (projectMap.getValue().getProjectId() == projectId) {
				return projectMap.getValue();
			}
		}
		logger.error("Project id " + projectId + " not found in configuration.");
		return null;
	}

}
