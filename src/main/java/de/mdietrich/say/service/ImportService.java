package de.mdietrich.say.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mdietrich.say.service.importer.CsvImporterInterface;
import de.mdietrich.say.service.importer.SimpleCsvImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mdietrich.say.entity.say.SayTimetableImportRow;
import de.mdietrich.say.service.importer.TimematorCsvImporter;

import javax.annotation.PostConstruct;

/**
 * Reads timetable data from file. Actually this is always a static csv
 * importer.
 *
 */
@Service
public class ImportService {

	@Autowired
	private TimematorCsvImporter timematorCsvImporter;

	@Autowired
	private SimpleCsvImporter simpleCsvImporter;

	Logger logger = LoggerFactory.getLogger(ImportService.class);

	private final Map<String, CsvImporterInterface> csvImporterMap = new HashMap<String, CsvImporterInterface>();

	/**
	 * Defines which csv importer gets used for import of files
	 * New importers need to be added here with unique key.
	 */
	@PostConstruct
	private void setup() {
		this.csvImporterMap.put("simpleCsvImporter", simpleCsvImporter);
		this.csvImporterMap.put("timematorCsvImporter", timematorCsvImporter);
	}

	public List<SayTimetableImportRow> readCsv(String importer, String filename) {
		if(!this.csvImporterMap.containsKey(importer)) {
			logger.error("Importer '" + importer+"' unknown.");
			return null;
		}
		return this.csvImporterMap.get(importer).importCsv(filename);
	}
}
