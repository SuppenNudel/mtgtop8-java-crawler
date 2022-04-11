package de.rohmio.mtg.mtgtop8.api.endpoints;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.rohmio.mtg.mtgtop8.api.model.CompLevel;
import de.rohmio.mtg.mtgtop8.api.model.MtgTop8Format;


public class SearchEndpoint extends AbstractEndpoint<Integer> {

	private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

//	private static Map<String, Integer> scores = new HashMap<>();

	public SearchEndpoint() {
		super("search", Integer.class);
	}

	public SearchEndpoint format(MtgTop8Format format) {
		String key = "format";
		resetQueryParam(key);
		target = target.queryParam(key, format.getId());
		return this;
	}

	public SearchEndpoint mainboard(boolean mainboard) {
		String key = "MD_check";
		resetQueryParam(key);
		int value = mainboard ? 1 : 0;
		target = target.queryParam(key, value);
		return this;
	}

	public SearchEndpoint sideboard(boolean sideboard) {
		String key = "SB_check";
		resetQueryParam(key);
		int value = sideboard ? 1 : 0;
		target = target.queryParam(key, value);
		return this;
	}

	public SearchEndpoint startdate(LocalDate startdate) {
		String format = startdate.format(dateTimeFormat);
		return startdate(format);
	}

	public SearchEndpoint startdate(String startdate) {
		String key = "date_start";
		resetQueryParam(key);
		target = target.queryParam(key, startdate);
		return this;
	}

	public SearchEndpoint enddate(LocalDate enddate) {
		String format = enddate.format(dateTimeFormat);
		return enddate(format);
	}

	public SearchEndpoint enddate(String enddate) {
		String key = "date_end";
		resetQueryParam(key);
		target = target.queryParam(key, enddate);
		return this;
	}

	public SearchEndpoint cards(Collection<String> cardNames) {
		String key = "cards";
		resetQueryParam(key);

		List<String> newNames = new ArrayList<>();
		for(String cardName : cardNames) {
			String newName = cardName
					.replace("ö", "o")
					.replace("ä", "a")
					.replace("ü", "u")
					.replace("Æ", "Ae");
			newNames.add(newName);
		}
		String joined = String.join(System.lineSeparator(), newNames);
		target = target.queryParam(key, joined);
//		if(newNames.size() == 1) {
//			cardName = newNames.get(0);
//		}
		return this;
	}

//	private String cardName;

	public SearchEndpoint cards(String... cardNames) {
		return cards(Arrays.asList(cardNames));
	}


	public SearchEndpoint compLevel(List<CompLevel> compLevels) {
		String key = "compet_check[%s]";
		for(CompLevel complevel : CompLevel.values()) {
			resetQueryParam(String.format(key, complevel.getId()));
		}
		for(CompLevel complevel : compLevels) {
			String param = String.format(key, complevel.getId());
			target = target.queryParam(param, "1");
		}
		return this;
	}

	public SearchEndpoint compLevel(CompLevel... compLevels) {
		return compLevel(Arrays.asList(compLevels));
	}

	public SearchEndpoint page(int page) {
		String key = "current_page";
		resetQueryParam(key);
		target = target.queryParam(key, page);
		return this;
	}

	/*
	@Override
	public Integer get() {
		Integer score = scores.get(cardName);
		if(score == null) {
			score = super.get();
			scores.put(cardName, score);
		}
		return score;
	}
	*/

	@Override
	protected Integer parseReponse(Response response) {
		String html = response.readEntity(String.class);
		int deckCount = parseDocumentForDeckCount(html);
		return deckCount;
	}

	public final static int NO_MATCH = -2;
	public final static int TOO_MANY_CARDS = -3;

	private int parseDocumentForDeckCount(String html) {
		Document document = Jsoup.parse(html);
		if(document.toString().contains("No match for")) {
			// probably a double named card, where only the front side is wanted
			return NO_MATCH;
		} else if (document.toString().contains("Too many cards")) {
			// probably a split card, where only the front side got used
			return TOO_MANY_CARDS;
		}
		String sumText = document.select("table > tbody > tr > td > div[class=w_title]").text();
		sumText = sumText.replace("decks matching", "").trim();

		int allDecksCount = Integer.parseInt(sumText);

		return allDecksCount;
	}

}
