package edu.gatech.edtech;

import java.net.URL;

public class MossReply {
	private URL mossURL;
	private SoftwareLanguage language;

	public MossReply(URL url, SoftwareLanguage sl){
		this.mossURL = url;
		this.language = sl;
	}
	public URL getMossURL() {
		return mossURL;
	}

	public SoftwareLanguage getLanguage() {
		return language;
	}
}
