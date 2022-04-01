package de.mdietrich.say.service.exporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.XfaForm;

import de.mdietrich.say.entity.configuration.Export;
import de.mdietrich.say.entity.sage.TimetableEntry;
import de.mdietrich.say.service.ConfigService;
import de.mdietrich.say.service.SageService;

/**
 * This class exports the time entries of a project from Sage into an accounting
 * PDF for FI. The choice of export to be used is made in SageService based on
 * the configuration entry in config.json for exports[].type
 * 
 * Further exporters can be added.
 *
 */
@Service
public class FiPdfExporter implements ExporterInterface {

	@Autowired
	private ConfigService configService;

	private String inPath = "export/in/";
	private String outPath = "export/out/";
	private String xmlFilename = "data.xml";

	// 01 = Arbeitszeit, 02 = Bereitschaftszeit, 03 = Reisezeit Hinfahrt, 04 =
	// Reisezeit Rückfahrt, 05 = Zeile gelöscht
	private final String compensationType = "01";

	private final String overtimeNotice = "Überzeiten wurden bereits übertragen bitte bestätigen.";

	Logger logger = LoggerFactory.getLogger(SageService.class);

	private String fillXmlBaseData(String xml, Export exportConfiguration) {
		xml = xml.replaceAll("%BESTELL_NR%", exportConfiguration.getBestellNr());
		xml = xml.replaceAll("%BESTELL_POS%", exportConfiguration.getBestellPos());
		xml = xml.replaceAll("%WARENKORB_NR%", exportConfiguration.getWarenkorbNr());
		xml = xml.replaceAll("%KONTIERUNGS_OBJEKT%", exportConfiguration.getKontierungsObjekt());
		xml = xml.replaceAll("%KREDITOREN_NR%", exportConfiguration.getKreditorenNr());
		xml = xml.replaceAll("%LEISTUNG_ART%", exportConfiguration.getLeistungArt());
		xml = xml.replaceAll("%LEISTUNG_ORT%", exportConfiguration.getLeistungOrt());
		xml = xml.replaceAll("%LEISTUNG_EMPFAENGER%", exportConfiguration.getLeistungEmpfaenger());
		return xml;
	}
	
	private String toString(BigDecimal bd, int decimals) {
		bd = bd.setScale(decimals, RoundingMode.HALF_UP);
		return bd.toString();
	}

	private BigDecimal minutesToDay(int minutes) {
		BigDecimal dailyHours = new BigDecimal(this.configService.getConfig().getDailyHours());
		BigDecimal hours = new BigDecimal(minutes).divide(new BigDecimal(60), 3, RoundingMode.HALF_UP).divide(dailyHours, 3, RoundingMode.HALF_UP);
		return hours;
	}

	private String fixRemark(String remark) {
		remark = remark.replace("&", "&amp;");
		return remark;
	}

	private String fillXmlTimetable(String xml, List<TimetableEntry> timetableEntryList) {
		if (timetableEntryList.size() == 0) {
			logger.error("Timetable has no entries");
			return null;
		}
		String[] dayArray = timetableEntryList.get(0).getDay().split("-");
		String year = dayArray[0];
		String month = dayArray[1];

		String dailyEntry = "            <DATA>\n"
				+ "                <VERGUETUNGSART>%VERGUETUNGSART%</VERGUETUNGSART>\n"
				+ "                <MONAT>%MONAT%</MONAT>\n"
				+ "                <WOCHENTAG>%WOCHENTAG%</WOCHENTAG>\n"
				+ "                <FEIERTAG>%FEIERTAG%</FEIERTAG>\n"
				+ "                <ZEIT_VON>%ZEIT_VON%</ZEIT_VON>\n"
				+ "                <ZEIT_BIS>%ZEIT_BIS%</ZEIT_BIS>\n"
				+ "                <PAUSE>%PAUSE%</PAUSE>\n"
				+ "                <DESCRIPTION>%DESCRIPTION%</DESCRIPTION>\n"
				+ "                <ERFASSTEZEIT>%ERFASSTEZEIT%</ERFASSTEZEIT>\n"
				+ "                <ANERKANNTEZEIT>%ANERKANNTEZEIT%</ANERKANNTEZEIT><EINHEIT/>\n"
				+ "                <ZUSCHLAG_NACHT>%ZUSCHLAG_NACHT%</ZUSCHLAG_NACHT>\n"
				+ "                <ZUSCHLAG_SA>%ZUSCHLAG_SA%</ZUSCHLAG_SA>\n"
				+ "                <ZUSCHLAG_SO>%ZUSCHLAG_SO%</ZUSCHLAG_SO>\n"
				+ "                <ZUSCHLAG_FEIERT>%ZUSCHLAG_FEIERT%</ZUSCHLAG_FEIERT>\n"
				+ "                <ZUSCHLAG_GESAMT>%ZUSCHLAG_GESAMT%</ZUSCHLAG_GESAMT>\n"
				+ "                <BEREIT_WERKTAG>0.000</BEREIT_WERKTAG>\n"
				+ "                <BEREIT_SA>0.000</BEREIT_SA>\n"
				+ "                <BEREIT_SO>0.000</BEREIT_SO>\n"
				+ "                <BEREIT_GESAMT>0.000</BEREIT_GESAMT>\n"
				+ "            </DATA>";


		xml = xml.replaceAll("%MONAT%", month);
		xml = xml.replaceAll("%JAHR%", year);
		xml = xml.replaceAll("%NACHNAME%", this.configService.getConfig().getLastname());
		xml = xml.replaceAll("%VORNAME%", this.configService.getConfig().getFirstname());

		// initialize totals (days)
		BigDecimal totalWork = new BigDecimal(0);
		BigDecimal totalApproved = new BigDecimal(0);
		BigDecimal totalDivergent = new BigDecimal(0);
		BigDecimal totalBonus = new BigDecimal(0);
		BigDecimal totalStandby = new BigDecimal(0);
		BigDecimal totalHour = new BigDecimal(0);

		BigDecimal bonusNightTotal = new BigDecimal(0);
		BigDecimal bonusSaturdayTotal = new BigDecimal(0);
		BigDecimal bonusHolidayTotal = new BigDecimal(0); // also contains sundays
		BigDecimal bonusTotalTotal = new BigDecimal(0);

		String dailyEntriesXml = "";
		for (TimetableEntry timetableEntry : timetableEntryList) {
			String dailyXml = dailyEntry;

			// prepare data
			String day = timetableEntry.getDay().substring(8, 10);
			String dayOfWeek = "0" + LocalDate.of(Integer.valueOf(year), Month.of(Integer.valueOf(month)), Integer.valueOf(day)).getDayOfWeek().getValue();
			// this should be changed to "Ja" during work on a holiday
			String holiday = "Nein";
			String timeFrom = timetableEntry.getTimeFrom().substring(11, 16);
			String timeTo = timetableEntry.getTimeTo().substring(11, 16);
			int breakAmountMinutes = timetableEntry.getBreakAmountMinutes();
			BigDecimal recordedTime = minutesToDay(timetableEntry.getAmountMinutes() - breakAmountMinutes);
			BigDecimal approvedTime = recordedTime;
			if (approvedTime.compareTo(new BigDecimal(1)) > 0) {
				approvedTime = new BigDecimal(1);
			}
			BigDecimal divergentTime = recordedTime.subtract(approvedTime);

			// TODO calculate special work hours
			BigDecimal bonusNight = new BigDecimal(0);
			BigDecimal bonusSaturday = new BigDecimal(0);
			BigDecimal bonusSunday = new BigDecimal(0);
			BigDecimal bonusHoliday = new BigDecimal(0);
			BigDecimal bonusTotal = new BigDecimal(0);
			BigDecimal standbyWeekday = new BigDecimal(0);
			BigDecimal standbySaturday = new BigDecimal(0);
			BigDecimal standbySunday = new BigDecimal(0);
			BigDecimal standbyHoliday = new BigDecimal(0);

			// calculate totals
			totalWork = totalWork.add(recordedTime);
			totalApproved = totalApproved.add(approvedTime);
			totalDivergent = totalDivergent.add(divergentTime);

			dailyXml = dailyXml.replaceAll("%VERGUETUNGSART%", this.compensationType);
			dailyXml = dailyXml.replaceAll("%MONAT%", timetableEntry.getDay().substring(0, 10));
			dailyXml = dailyXml.replaceAll("%WOCHENTAG%", dayOfWeek);
			dailyXml = dailyXml.replaceAll("%FEIERTAG%", holiday);
			dailyXml = dailyXml.replaceAll("%ZEIT_VON%", timeFrom);
			dailyXml = dailyXml.replaceAll("%ZEIT_BIS%", timeTo);
			dailyXml = dailyXml.replaceAll("%PAUSE%", String.valueOf(breakAmountMinutes));
			dailyXml = dailyXml.replaceAll("%DESCRIPTION%", this.fixRemark(timetableEntry.getRemark()));
			dailyXml = dailyXml.replaceAll("%ERFASSTEZEIT%", toString(recordedTime, 3));
			dailyXml = dailyXml.replaceAll("%ANERKANNTEZEIT%", toString(approvedTime, 3));
			dailyXml = dailyXml.replaceAll("%ZUSCHLAG_NACHT%", toString(bonusNight, 3));
			dailyXml = dailyXml.replaceAll("%ZUSCHLAG_SA%", toString(bonusSaturday, 3));
			dailyXml = dailyXml.replaceAll("%ZUSCHLAG_SO%", toString(bonusSunday, 3));
			dailyXml = dailyXml.replaceAll("%ZUSCHLAG_FEIERT%", toString(bonusHoliday, 3));
			dailyXml = dailyXml.replaceAll("%ZUSCHLAG_GESAMT%", toString(bonusTotal, 3));
			dailyXml = dailyXml.replaceAll("%BEREIT_WERKTAG%", toString(standbyWeekday, 3));
			dailyXml = dailyXml.replaceAll("%BEREIT_SA%", toString(standbySaturday, 3));
			dailyXml = dailyXml.replaceAll("%BEREIT_SO%", toString(standbySunday, 3));
			dailyXml = dailyXml.replaceAll("%BEREIT_GESAMT%", toString(standbyHoliday, 3));

			dailyEntriesXml += dailyXml;
		}
		xml = xml.replaceAll("%EINGABETAG_DATA%", dailyEntriesXml);

		xml = xml.replaceAll("%GESAMT_ARBEIT%", toString(totalApproved, 3));
		xml = xml.replaceAll("%GESAMT_ZUSCHLAG%", toString(bonusTotalTotal, 3));
		xml = xml.replaceAll("%GESAMT_BEREITSCHAFT%", toString(totalStandby, 3));
		xml = xml.replaceAll("%GESAMT_ABWEICHEND%", toString(totalDivergent, 3));
		xml = xml.replaceAll("%GESAMT_TAG%", toString(totalWork, 3));
		xml = xml.replaceAll("%GESAMT_STUNDE%", toString(totalHour, 3));
		xml = xml.replaceAll("%GESAMT_ANERKANNT%", toString(totalApproved, 3));

		xml = xml.replaceAll("%ZUSCHLAG_NACHT%", toString(bonusNightTotal, 3));
		xml = xml.replaceAll("%ZUSCHLAG_SAMSTAG%", toString(bonusSaturdayTotal, 3));
		xml = xml.replaceAll("%ZUSCHLAG_FEIERTAG%", toString(bonusHolidayTotal, 3));
		xml = xml.replaceAll("%ZUSCHLAG_GESAMT%", toString(bonusTotalTotal, 3));

		String notice = "";
		if (totalWork.compareTo(totalApproved) > 0) {
			notice = this.overtimeNotice;
		}
		xml = xml.replaceAll("%NOTICE%", notice);
		xml = xml.replaceAll("%DATE_US%", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.YYYY")));

		return xml;
	}
	
	private void createXml(Export exportConfiguration, List<TimetableEntry> timetableEntryList) {

		String xmlPath = this.inPath + exportConfiguration.getXmlIn();
		byte[] data;
		String xml;
		
		// read file
		try {			
			data = Files.readAllBytes(Paths.get(xmlPath));
			xml = new String(data, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Error reading file " + xmlPath);
			return;
		}
		
		// set base data
		xml = this.fillXmlBaseData(xml, exportConfiguration);
		
		// fill timetable
		xml = this.fillXmlTimetable(xml, timetableEntryList);

		// write xml data
		this.writeXmlData(xml);
	}

	private void writeXmlData(String xml) {
		String filename = this.outPath + this.xmlFilename;
		byte[] strToBytes = xml.getBytes(StandardCharsets.UTF_8);
		FileOutputStream outputStream;

		try {
			outputStream = new FileOutputStream(filename);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			logger.error("Could not find file for output: " + filename);
			return;
		}

		try {
			outputStream.write(strToBytes);
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Could not write temporary xml file " + filename);
			return;
		}
	}

	private String buildExportPath(String date, String filename) {
		filename = filename.substring(0, filename.length() - 4);
		String path = this.outPath + date.substring(0, 7) + "_" + filename + "_" + LocalDate.now().toString() + ".pdf";
		return path;
	}

	private void writeExport(String date, Export exportConfiguration) {

		// fill pdf
		File inputPdf = new File(this.inPath + exportConfiguration.getPdfIn()); 
		File outputPdf = new File(this.buildExportPath(date, exportConfiguration.getPdfIn()));
		File xmlFile = new File(this.outPath + this.xmlFilename);

		try {
			this.fillXmlInPdf(xmlFile, inputPdf, outputPdf);
		} catch (IOException | DocumentException e) {
			e.printStackTrace();
			logger.error("Could not create PDF");
		}
	}

	private void removeXmlData() {
		File xmlFile = new File(this.outPath + this.xmlFilename);
		xmlFile.delete();
	}

	public void fillXmlInPdf(File xmlFile, File inputPdf, File outputPdf) throws IOException, DocumentException, FileNotFoundException {
		PdfStamper stamper = null;
		try {
			PdfReader reader = new PdfReader(inputPdf.getAbsolutePath());
			stamper = new PdfStamper(reader, new FileOutputStream(outputPdf), '\0', true);
			AcroFields afields = stamper.getAcroFields();
			XfaForm xfa = afields.getXfa();
			xfa.fillXfaForm(new FileInputStream(xmlFile));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				stamper.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void export(List<TimetableEntry> timetableEntryList, Export exportConfiguration) {
		// build XML
		this.createXml(exportConfiguration, timetableEntryList);

		// write export
		this.writeExport(timetableEntryList.get(0).getDay(), exportConfiguration);

		// remove temp xml
		this.removeXmlData();
	}

}
