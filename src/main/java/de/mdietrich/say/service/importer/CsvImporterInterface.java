package de.mdietrich.say.service.importer;

import java.util.List;

import de.mdietrich.say.entity.say.SayTimetableImportRow;

public interface CsvImporterInterface {

	public List<SayTimetableImportRow> importCsv(String filename);
}
