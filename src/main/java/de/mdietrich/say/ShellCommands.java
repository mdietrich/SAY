package de.mdietrich.say;

import java.util.List;

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


@ShellComponent
public class ShellCommands {

	@Autowired
	private SageService sageService;

	Logger logger = LoggerFactory.getLogger(ShellCommands.class);

	@ShellMethod("Read information for configured user")
	public String user() {
		String result = "";
		User user = sageService.getUserData();
		result += "\nUserId " + user.getUserId() + " -  " + user.getEmployee().getFullname() + "\n";
		return result;
	}

	@ShellMethod("Read projects for configured user")
	public String projects(@ShellOption(defaultValue = "") String username, @ShellOption(defaultValue = "") String password) {
		String result = "";
		List<Project> projects = sageService.getProjects();
		if (projects.size() > 0) {
			result += "\nAvailable projects:\n";
			for (Project project : projects) {
				result += "ProjectId " + project.getId() + " - " + project.getName() + "\n";
			}
		} else {
			result += "\nNo projects found.\n";
		}
		return result;
	}

	@ShellMethod("Read project details for given project id")
	public String project(@ShellOption int projectId) {
		String result = "";
		List<ProjectUnit> projectUnits = sageService.getProjectUnits(projectId);
		if (projectUnits.size() > 0) {
			System.out.println("\nAvailable project units:\n");
			for (ProjectUnit projectUnit : projectUnits) {
				result += "Unit " + projectUnit.getId() + " - " + projectUnit.getName() + "\n";
			}
		} else {
			result += "\nNo project units found.\n";
		}
		return result;
	}

	@ShellMethod("Read timetable for given month (YYYY-MM)")
	public String timetable(@ShellOption String date) {
		String result = "";

		Timetable timetable = sageService.getTimeTable(date);
		if (timetable == null) {
			return "Error";
		}
		for (TimetableEntry timetableEntry : timetable.getTimetableEntries()) {
			result += "\nBookingId " + timetableEntry.getBuchungsId() + " - " + timetableEntry.getDay() + " - " + timetableEntry.getAmountMinutes() + " Min.: " + timetableEntry.getProject().getName() + " - " + timetableEntry.getRemark();
		}

		return result;
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
