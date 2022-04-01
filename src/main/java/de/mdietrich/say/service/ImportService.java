package de.mdietrich.say.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mdietrich.say.entity.say.SayTimetableImportRow;
import de.mdietrich.say.service.importer.TimematorCsvImporter;

/**
 * Reads timetable data from file. Actually this is always a static csv
 * importer.
 *
 */
@Service
public class ImportService {

	@Autowired
	private TimematorCsvImporter timematorCsvImporter;

	Logger logger = LoggerFactory.getLogger(ImportService.class);

	public List<SayTimetableImportRow> read(String filename) {
		List<SayTimetableImportRow> rows = timematorCsvImporter.importCsv(filename);
		return rows;
	}
}
