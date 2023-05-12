package de.rohmio.mtg.mtgtop8.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CompLevel {
	
	@JsonProperty("R")
	REGULAR,

	@JsonProperty("C")
	COMPETITIVE,

	@JsonProperty("M")
	MAJOR,

	@JsonProperty("P")
	PROFESSIONAL;
	
	public String getId() {
		try {
			JsonProperty annotation = getClass().getField(name()).getAnnotation(JsonProperty.class);
			return annotation.value();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
