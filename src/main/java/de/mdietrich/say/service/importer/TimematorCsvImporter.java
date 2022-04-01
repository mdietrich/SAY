package de.mdietrich.say.service.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import de.mdietrich.say.entity.say.SayTimetableImportRow;

/**
 * Imports a CSV file from the app Timemator.
 * 
 * Further importers can be added and used in ImporService.
 * 
 */
@Service
public class TimematorCsvImporter implements CsvImporterInterface {

	Logger logger = LoggerFactory.getLogger(TimematorCsvImporter.class);

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
				row.setDate(columns[2]);
				row.setBegin(columns[3]);
				row.setEnd(columns[4]);
				row.setCompany(columns[5]);
				row.setProject(columns[6]);
				row.setAmountHours(new BigDecimal(columns[8]));
				row.setRemarks(columns[9]);
				result.add(row);
			}

		} catch (IOException | CsvException e) {
			e.printStackTrace();
			logger.error("Error reading csv file " + filename);
		}

		return result;
	}
}
