package de.rohmio.mtg.mtgtop8.api;

import de.rohmio.mtg.mtgtop8.api.endpoints.SearchEndpoint;

public abstract class MtgTop8Api {

	public static int RATE_LIMIT = 50;

	/**
	 * @param cardName The exact card name to search for, case insenstive.
	 */
	public static SearchEndpoint search() {
		return new SearchEndpoint();
	}

}
