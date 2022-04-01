package de.mdietrich.say.entity.sage;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Sage portal login form
 *
 */
public class PortalLoginForm {

	Logger logger = LoggerFactory.getLogger(PortalLoginForm.class);

	@JsonProperty(value = "__EVENTTARGET")
	private String eventTarget = "";

	@JsonProperty(value = "__EVENTARGUMENT")
	private String eventArgument = "";

	@JsonProperty(value = "__VIEWSTATE")
	private String viewState;

	@JsonProperty(value = "__VIEWSTATEGENERATOR")
	private String viewStateGenerator;

	@JsonProperty(value = "__EVENTVALIDATION")
	private String eventValidation;

	@JsonProperty(value = "ctl00$cphContent$hfNTLoginFailed")
	private String loginFailed = "0";

	@JsonProperty(value = "ctl00$cphContent$loginRedirectUrl")
	private String loginRedirectUrl = "";

	@JsonProperty(value = "ctl00$cphContent$txtUsername")
	private String txtUsername;

	@JsonProperty(value = "ctl00$cphContent$txtPassword")
	private String txtPassword;

	@JsonProperty(value = "ctl00$cphContent$cmdLogin")
	private String cmdLogin = "Anmelden";

	@JsonProperty(value = "DXScript")
	private String dxScript = "1_289,1_184,1_283,1_210,1_187,1_208,1_216,1_181,1_197";

	@JsonProperty(value = "DXCss")
	private String dxCss = "1_49,1_16,0_3953,0_3957,/mportal/Lib/Themes/Office2010Silver/Editors/sprite.css.ashx,/mportal/Lib/Themes/Office2010Silver/Editors/styles.css.ashx,favicon.ico,/mportal/Lib/Styles.ashx";

	public String getEventTarget() {
		return eventTarget;
	}

	public void setEventTarget(String eventTarget) {
		this.eventTarget = eventTarget;
	}

	public String getEventArgument() {
		return eventArgument;
	}

	public void setEventArgument(String eventArgument) {
		this.eventArgument = eventArgument;
	}

	public String getViewState() {
		return viewState;
	}

	public void setViewState(String viewState) {
		this.viewState = viewState;
	}

	public String getViewStateGenerator() {
		return viewStateGenerator;
	}

	public void setViewStateGenerator(String viewStateGenerator) {
		this.viewStateGenerator = viewStateGenerator;
	}

	public String getEventValidation() {
		return eventValidation;
	}

	public void setEventValidation(String eventValidation) {
		this.eventValidation = eventValidation;
	}

	public String getLoginFailed() {
		return loginFailed;
	}

	public void setLoginFailed(String loginFailed) {
		this.loginFailed = loginFailed;
	}

	public String getLoginRedirectUrl() {
		return loginRedirectUrl;
	}

	public void setLoginRedirectUrl(String loginRedirectUrl) {
		this.loginRedirectUrl = loginRedirectUrl;
	}

	public String getTxtUsername() {
		return txtUsername;
	}

	public void setTxtUsername(String txtUsername) {
		this.txtUsername = txtUsername;
	}

	public String getTxtPassword() {
		return txtPassword;
	}

	public void setTxtPassword(String txtPassword) {
		this.txtPassword = txtPassword;
	}

	public String getCmdLogin() {
		return cmdLogin;
	}

	public void setCmdLogin(String cmdLogin) {
		this.cmdLogin = cmdLogin;
	}

	public String getDxScript() {
		return dxScript;
	}

	public void setDxScript(String dxScript) {
		this.dxScript = dxScript;
	}

	public String getDxCss() {
		return dxCss;
	}

	public void setDxCss(String dxCss) {
		this.dxCss = dxCss;
	}

	@Override
	public String toString() {
		return "PortalLoginForm [eventTarget=" + eventTarget + ", eventArgument=" + eventArgument + ", viewState=" + viewState + ", viewStateGenerator=" + viewStateGenerator + ", eventValidation=" + eventValidation + ", loginFailed="
				+ loginFailed + ", loginRedirectUrl=" + loginRedirectUrl + ", txtUsername=" + txtUsername + ", txtPassword=" + txtPassword + ", cmdLogin=" + cmdLogin + ", dxScript=" + dxScript + ", dxCss=" + dxCss + "]";
	}

	/**
	 * Build BodyPublisher form from object data
	 * 
	 * @return
	 */
	public BodyPublisher toFormData() {
		var builder = new StringBuilder();
		for (Field field : this.getClass().getDeclaredFields()) {
			if (field != null) {
				JsonProperty prop = field.getAnnotation(JsonProperty.class);
				if (prop != null) {
					String propValue = prop.value();
					String value = "";
					try {
						value = (String) field.get(this);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
						continue;
					}

					if (propValue != null && !propValue.isEmpty()) {
						if (builder.length() > 0) {
							builder.append("&");
						}
						builder.append(URLEncoder.encode(propValue, StandardCharsets.UTF_8));
						builder.append("=");
						if (value != null) {
							builder.append(URLEncoder.encode(value, StandardCharsets.UTF_8));
						} else {
							System.out.println(propValue);
						}
					}
				}
			}

		}
		return BodyPublishers.ofString(builder.toString());
	}

}
