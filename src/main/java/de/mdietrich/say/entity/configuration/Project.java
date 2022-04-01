package de.mdietrich.say.entity.configuration;

/**
 * This objects holds the project configurations from config.json
 *
 */
public class Project {

	private int projectId;
	private int activityId;
	private String defaultDescription;
	private String export;
	private Boolean addBreak = true;

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

	public String getDefaultDescription() {
		return defaultDescription;
	}

	public void setDefaultDescription(String defaultDescription) {
		this.defaultDescription = defaultDescription;
	}

	public String getExport() {
		return export;
	}

	public void setExport(String export) {
		this.export = export;
	}

	public Boolean getAddBreak() {
		return addBreak;
	}

	public void setAddBreak(Boolean addBreak) {
		this.addBreak = addBreak;
	}

	@Override
	public String toString() {
		return "Project [projectId=" + projectId + ", activityId=" + activityId + ", defaultDescription=" + defaultDescription + ", export=" + export + ", addBreak=" + addBreak + "]";
	}
}
