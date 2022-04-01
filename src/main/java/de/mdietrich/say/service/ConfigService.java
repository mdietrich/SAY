package de.mdietrich.say.service;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mdietrich.say.entity.configuration.Configuration;

/**
 * Reads config file
 *
 */
@Service
@Scope("singleton")
public class ConfigService {

	private Configuration config;

	Logger logger = LoggerFactory.getLogger(ConfigService.class);

	private final String configurationPath = "./config.json";

	@PostConstruct
	private void readConfiguration() {
		logger.debug("Loading config...");
		ObjectMapper mapper = new ObjectMapper();
		try {
			this.config = mapper.readValue(new File(configurationPath), Configuration.class);
		} catch (IOException e) {
			logger.error("Could not open " + configurationPath);
		}
	}

	public Configuration getConfig() {
		return this.config;
	}
}
