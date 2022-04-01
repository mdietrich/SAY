package de.mdietrich.say.service;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import javax.annotation.PostConstruct;
import javax.security.sasl.AuthenticationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.mdietrich.say.exception.UnauthorizedException;

/**
 * Service class to handle all requests
 *
 */
@Service
public class RequestService {

	Logger logger = LoggerFactory.getLogger(RequestService.class);

	private HttpClient httpClient;
	private int timeoutSeconds = 60;

	@PostConstruct
	private void initHttpClient() {
		this.httpClient = HttpClient.newBuilder().cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL)).followRedirects(HttpClient.Redirect.ALWAYS).build();
	}



	/**
	 * Request caller
	 * 
	 * @param request
	 * @param expectedStatusCode
	 * @return
	 * @throws AuthenticationException
	 */
	private String callPage(HttpRequest request, int expectedStatusCode) throws UnauthorizedException {
		String result = "";
		HttpResponse<String> response = null;
		try {
			response = this.httpClient.send(request, BodyHandlers.ofString());

		} catch (IOException e) {
			logger.error("Could not send request.");
			e.printStackTrace();
		} catch (InterruptedException e) {
			logger.error("Could not send request.");
			e.printStackTrace();
		}

		if (response.statusCode() != expectedStatusCode) {
			if (response.statusCode() == 401) {
				throw new UnauthorizedException(response.body());
			}
			logger.error("Got status code " + response.statusCode() + " but expected " + expectedStatusCode);
			logger.error(response.body());
			return null;
		}
		result = response.body();

		return result;
	}

	/**
	 * Sage get request
	 * 
	 * @param url
	 * @param expectedStatusCode
	 * @return
	 * @throws AuthenticationException
	 */
	public String getPage(String url, int expectedStatusCode) throws UnauthorizedException {
		logger.debug("Getting " + url + " ...");
		String result = "";
		try {
			HttpRequest request = HttpRequest.newBuilder(new URI(url)).GET().timeout(Duration.ofSeconds(this.timeoutSeconds)).build();
			result = this.callPage(request, expectedStatusCode);
		} catch (URISyntaxException e) {
			logger.error("Could not build http request");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Sage get request (200)
	 * 
	 * @param url
	 * @return
	 * @throws AuthenticationException
	 */
	public String getPage(String url) throws UnauthorizedException {
		int expectedStatusCode = 200;
		return this.getPage(url, expectedStatusCode);
	}

	/**
	 * Sage post request
	 * 
	 * @param url
	 * @param bodyPublisher
	 * @param expectedStatusCode
	 * @param contentType
	 * @return
	 * @throws AuthenticationException
	 */
	public String postPage(String url, BodyPublisher bodyPublisher, int expectedStatusCode, String contentType) throws UnauthorizedException {
		logger.debug("Posting " + url + " ...");
		String result = "";
		try {
			HttpRequest request = HttpRequest.newBuilder(new URI(url)).POST(bodyPublisher).timeout(Duration.ofSeconds(this.timeoutSeconds)).headers("Content-Type", contentType).build();
			result = this.callPage(request, expectedStatusCode);
		} catch (URISyntaxException e) {
			logger.error("Could not build http request");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Sage put request
	 * 
	 * @param url
	 * @param bodyPublisher
	 * @param expectedStatusCode
	 * @param contentType
	 * @return
	 * @throws AuthenticationException
	 */
	public String putPage(String url, BodyPublisher bodyPublisher, int expectedStatusCode, String contentType) throws UnauthorizedException {
		logger.debug("Putting " + url + " ...");
		String result = "";
		try {
			HttpRequest request = HttpRequest.newBuilder(new URI(url)).PUT(bodyPublisher).timeout(Duration.ofSeconds(this.timeoutSeconds)).headers("Content-Type", contentType).build();
			result = this.callPage(request, expectedStatusCode);
		} catch (URISyntaxException e) {
			logger.error("Could not build http request");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Sage delete request
	 * 
	 * @param url
	 * @param expectedStatusCode
	 * @param contentType
	 * @return
	 * @throws AuthenticationException
	 */
	public String deletePage(String url, int expectedStatusCode, String contentType) throws UnauthorizedException {
		logger.debug("Deleting " + url + " ...");
		String result = "";
		try {
			HttpRequest request = HttpRequest.newBuilder(new URI(url)).DELETE().headers("Content-Type", contentType).build();
			result = this.callPage(request, expectedStatusCode);
		} catch (URISyntaxException e) {
			logger.error("Could not build http request");
			e.printStackTrace();
		}
		return result;
	}
}
