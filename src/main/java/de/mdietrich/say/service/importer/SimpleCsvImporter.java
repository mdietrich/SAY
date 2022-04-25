package de.mdietrich.say.service.importer;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import de.mdietrich.say.entity.say.SayTimetableImportRow;
import de.mdietrich.say.service.TimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Imports a CSV file from the app Timemator.
 * 
 * Further importers can be added and used in ImporService.
 * 
 */
@Service
public class SimpleCsvImporter implements CsvImporterInterface {

	Logger logger = LoggerFactory.getLogger(SimpleCsvImporter.class);

	@Autowired
	private TimeService timeService;

	public List<SayTimetableImportRow> importCsv(String filename) {
		List<SayTimetableImportRow> result = new ArrayList<>();
		try {
			// create CSVReader
			Path myPath = Paths.get(filename);
			CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
			BufferedReader br = Files.newBufferedReader(myPath, StandardCharsets.UTF_8);
			CSVReader reader = new CSVReaderBuilder(br).withCSVParser(parser).build();

			reader.skip(1);
			List<String[]> rows = reader.readAll();
			for (String[] columns : rows) {
				SayTimetableImportRow row = new SayTimetableImportRow();
				row.setDate(timeService.germanDateStringToDateString(columns[0]));
				row.setBegin(columns[1]);
				row.setEnd(columns[2]);
				row.setCompany(columns[3]);
				row.setProject(columns[4]);
				row.setAmountHours(timeService.calculateHoursDuration(columns[1], columns[2]));
				row.setRemarks(columns[5]);
				result.add(row);
			}

		} catch (IOException | CsvException e) {
			e.printStackTrace();
			logger.error("Error reading csv file " + filename);
		}

		return result;
	}

}
