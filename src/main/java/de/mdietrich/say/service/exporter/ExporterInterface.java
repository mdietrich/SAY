package de.mdietrich.say.service.exporter;

import java.util.List;

import de.mdietrich.say.entity.configuration.Export;
import de.mdietrich.say.entity.sage.TimetableEntry;

public interface ExporterInterface {

	void export(List<TimetableEntry> entryList, Export exportConfiguration);
}
