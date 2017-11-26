package uniko.iwvi.fgbas.magoetz.sbo.util;

import java.util.Locale;

public enum Language {

	GERMAN("de", Locale.GERMAN), ENGLISH("en", Locale.ENGLISH), FRENCH("fr", Locale.FRENCH);
	
	public static Language ofCode(String code) {
		for(Language l : values()) {
			if(l.getCode().equals(code)) {
				return l;
			}
		}
		return null;
	}
	
	public static Language getDefault() {
		return GERMAN;
	}

	private Language(String code, Locale locale) {
		this.code = code;
		this.locale = locale;
	}
	
	private String code;

	private Locale locale;
	
	public String getCode() {
		return this.code;
	}

	public Locale getLocale() {
		return locale;
	}

}
