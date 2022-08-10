package de.rohmio.mtg.mtgtop8.api;

import java.util.logging.Level;

import de.rohmio.mtg.mtgtop8.api.endpoints.SearchEndpoint;

public abstract class MtgTop8Api {

	public static int RATE_LIMIT = 50;
	public static Level LOG_LEVEL = Level.ALL;

	/**
	 * @param cardName The exact card name to search for, case insenstive.
	 */
	public static SearchEndpoint search() {
		return new SearchEndpoint();
	}

}
