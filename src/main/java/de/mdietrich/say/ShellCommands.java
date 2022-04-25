package de.mdietrich.say;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import de.mdietrich.say.entity.sage.Project;
import de.mdietrich.say.entity.sage.ProjectUnit;
import de.mdietrich.say.entity.sage.Timetable;
import de.mdietrich.say.entity.sage.TimetableEntry;
import de.mdietrich.say.entity.sage.User;
import de.mdietrich.say.service.SageService;

import java.util.List;


@ShellComponent
public class ShellCommands {

	@Autowired
	private SageService sageService;

	Logger logger = LoggerFactory.getLogger(ShellCommands.class);

	@ShellMethod("Read information for configured user")
	public String user() {
		StringBuilder result = new StringBuilder();
		User user = sageService.getUserData();
		result.append("\nUserId ").append(user.getUserId()).append(" -  ").append(user.getEmployee().getFullname())
				.append("\n");
		return result.toString();
	}

	@ShellMethod("Read projects for configured user")
	public String projects() {
		StringBuilder result = new StringBuilder();
		List<Project> projects = sageService.getProjects();
		if (projects.size() > 0) {
			result.append("\nAvailable projects:\n");
			for (Project project : projects) {
				result.append("ProjectId ").append(project.getId()).append(" - ").append(project.getName()).append("\n");
			}
		} else {
			result.append("\nNo projects found.\n");
		}
		return result.toString();
	}

	@ShellMethod("Read project details for given project id")
	public String project(@ShellOption int projectId) {
		StringBuilder result = new StringBuilder();
		List<ProjectUnit> projectUnits = sageService.getProjectUnits(projectId);
		if (projectUnits.size() > 0) {
			System.out.println("\nAvailable project units:\n");
			for (ProjectUnit projectUnit : projectUnits) {
				result.append("Unit ").append(projectUnit.getId()).append(" - ").append(projectUnit.getName()).append("\n");
			}
		} else {
			result.append("\nNo project units found.\n");
		}
		return result.toString();
	}

	@ShellMethod("Read timetable for given month (YYYY-MM)")
	public String timetable(@ShellOption String date) {
		StringBuilder result = new StringBuilder();

		Timetable timetable = sageService.getTimeTable(date);
		if (timetable == null) {
			return "Error";
		}
		for (TimetableEntry timetableEntry : timetable.getTimetableEntries()) {
			result.append("\nBookingId ").append(timetableEntry.getBuchungsId()).append(" - ").append(timetableEntry.getDay()).append(" - ").append(timetableEntry.getAmountMinutes()).append(" Min.: ").append(timetableEntry.getProject().getName()).append(" - ").append(timetableEntry.getRemark());
		}

		return result.toString();
	}

	@ShellMethod(key = "import", value = "Import data from csv")
	public String importData(@ShellOption String csv) {
		String result = "";

		sageService.importExternalData(csv);

		return result;
	}

	@ShellMethod(key = "delete", value = "Delete data for given month (YYYY-MM)")
	public String delete(@ShellOption String date) {
		String result = "";

		sageService.removeProjectTimeForMonth(date);

		return result;
	}

	@ShellMethod(key = "export", value = "Export data from sage to other formats for given month (YYYY-MM)")
	public String export(@ShellOption String date) {
		String result = "";

		Timetable timetable = sageService.getTimeTable(date);
		if (timetable == null) {
			return "Error";
		}

		sageService.exportTimetable(timetable);

		return result;
	}
}
