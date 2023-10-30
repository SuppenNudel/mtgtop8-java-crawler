package de.rohmio.mtg.mtgtop8.api;

import java.util.logging.Level;

import de.rohmio.mtg.mtgtop8.api.endpoints.CompareEndpoint;
import de.rohmio.mtg.mtgtop8.api.endpoints.SearchEndpoint;
import io.github.suppennudel.mtg.generic.MtgFormat;

public abstract class MtgTop8Api {

	public static int RATE_LIMIT = 50;
	public static Level LOG_LEVEL = Level.ALL;

	/**
	 * @return the endpoint
	 */
	public static SearchEndpoint search() {
		return new SearchEndpoint();
	}

	public static CompareEndpoint compare() {
		return new CompareEndpoint();
	}

	public static String convert(MtgFormat format) {
		switch (format) {
		case STANDARD: return "ST";
		case PIONEER: return "PI";
		case MODERN: return "MO";
		case LEGACY: return "LE";
		default:
			throw new UnsupportedOperationException(format + " is not supported");
		}
	}

}
