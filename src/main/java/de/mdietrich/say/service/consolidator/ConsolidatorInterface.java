package de.mdietrich.say.service.consolidator;

import java.util.List;

import de.mdietrich.say.entity.say.SayTimetable;
import de.mdietrich.say.entity.say.SayTimetableImportRow;

public interface ConsolidatorInterface {

	public SayTimetable consolidate(List<SayTimetableImportRow> importRowList);
}
