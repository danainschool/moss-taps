package edu.gatech.edtech;

public class SoftwareLanguage {

	private String languageName;
	private String extension;
	private String parameter;
	public SoftwareLanguage(String languageName, String extension,
			String parameter) {
		super();
		this.languageName = languageName;
		this.extension = extension;
		this.parameter = parameter;
	}
	public String getLanguageName() {
		return languageName;
	}
	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
}
