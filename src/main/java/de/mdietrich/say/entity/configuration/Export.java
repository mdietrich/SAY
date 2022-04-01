package de.mdietrich.say.entity.configuration;

/**
 * This objects holds the export configurations from config.json
 *
 */
public class Export {

	private String type;
	private String pdfIn;
	private String xmlIn;
	private String bestellNr;
	private String bestellPos;
	private String warenkorbNr;
	private String kontierungsObjekt;
	private String kreditorenNr;
	private String leistungArt;
	private String leistungOrt;
	private String leistungEmpfaenger;

	public String getPdfIn() {
		return pdfIn;
	}

	public void setPdfIn(String pdfIn) {
		this.pdfIn = pdfIn;
	}

	public String getXmlIn() {
		return xmlIn;
	}

	public void setXmlIn(String xmlIn) {
		this.xmlIn = xmlIn;
	}

	public String getBestellNr() {
		return bestellNr;
	}

	public void setBestellNr(String bestellNr) {
		this.bestellNr = bestellNr;
	}

	public String getBestellPos() {
		return bestellPos;
	}

	public void setBestellPos(String bestellPos) {
		this.bestellPos = bestellPos;
	}

	public String getWarenkorbNr() {
		return warenkorbNr;
	}

	public void setWarenkorbNr(String warenkorbNr) {
		this.warenkorbNr = warenkorbNr;
	}

	public String getKontierungsObjekt() {
		return kontierungsObjekt;
	}

	public void setKontierungsObjekt(String kontierungsObjekt) {
		this.kontierungsObjekt = kontierungsObjekt;
	}

	public String getKreditorenNr() {
		return kreditorenNr;
	}

	public void setKreditorenNr(String kreditorenNr) {
		this.kreditorenNr = kreditorenNr;
	}

	public String getLeistungArt() {
		return leistungArt;
	}

	public void setLeistungArt(String leistungArt) {
		this.leistungArt = leistungArt;
	}

	public String getLeistungOrt() {
		return leistungOrt;
	}

	public void setLeistungOrt(String leistungOrt) {
		this.leistungOrt = leistungOrt;
	}

	public String getLeistungEmpfaenger() {
		return leistungEmpfaenger;
	}

	public void setLeistungEmpfaenger(String leistungEmpfaenger) {
		this.leistungEmpfaenger = leistungEmpfaenger;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Export [type=" + type + ", pdfIn=" + pdfIn + ", xmlIn=" + xmlIn + ", bestellNr=" + bestellNr + ", bestellPos=" + bestellPos + ", warenkorbNr=" + warenkorbNr + ", kontierungsObjekt=" + kontierungsObjekt + ", kreditorenNr="
				+ kreditorenNr + ", leistungArt=" + leistungArt + ", leistungOrt=" + leistungOrt + ", leistungEmpfaenger=" + leistungEmpfaenger + "]";
	}

}
