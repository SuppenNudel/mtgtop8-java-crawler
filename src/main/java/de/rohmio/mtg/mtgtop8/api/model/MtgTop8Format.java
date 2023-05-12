package de.rohmio.mtg.mtgtop8.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MtgTop8Format {

	@JsonProperty("")
	ALL,

	@JsonProperty("EXP")
	EXPLORER,

	@JsonProperty("ALCH")
	ALCHEMY,

	@JsonProperty("ST")
	STANDARD,

	@JsonProperty("PI")
	PIONEER,

	@JsonProperty("MO")
	MODERN,

	@JsonProperty("LE")
	LEGACY,

	@JsonProperty("VI")
	VINTAGE,

	@JsonProperty("EDH")
	DUEL_COMMANDER,

	@JsonProperty("cEDH")
	CEDH,

	@JsonProperty("EDHM")
	MTGO_COMMANDER,

	@JsonProperty("BL")
	BLOCK_CONSTRUCTED,

	@JsonProperty("EX")
	EXTENDED,

	@JsonProperty("PAU")
	PAUPER,

	@JsonProperty("PEA")
	PEASANT,

	@JsonProperty("HIGH")
	HIGHLANDER,

	@JsonProperty("CHL")
	CANADIAN_HIGHLANDER,

	@JsonProperty("HI")
	HISTORIC,

	@JsonProperty("LI")
	LIMITED,

	@JsonProperty("BL")
	BLOCK;

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
