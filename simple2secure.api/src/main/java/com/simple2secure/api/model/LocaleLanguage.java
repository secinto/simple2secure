package com.simple2secure.api.model;

public enum LocaleLanguage {
	ENGLISH("en"), 
	GERMAN("de");
	
	public final String label;
	
    private LocaleLanguage(String label) {
        this.label = label;
    }
    
    public static LocaleLanguage valueOfLabel(String label) {
        for (LocaleLanguage e : values()) {
            if (e.label.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
