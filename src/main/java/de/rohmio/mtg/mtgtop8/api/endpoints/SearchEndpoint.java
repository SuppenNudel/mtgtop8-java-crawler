package de.rohmio.mtg.mtgtop8.api.endpoints;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.rohmio.mtg.mtgtop8.api.MtgTop8Api;
import de.rohmio.mtg.mtgtop8.api.model.CompLevel;
import de.rohmio.mtg.mtgtop8.api.model.SearchResult;
import de.rohmio.mtg.mtgtop8.api.model.SearchResultDeck;
import io.github.suppennudel.mtg.generic.MtgFormat;


public class SearchEndpoint extends AbstractEndpoint<SearchResult> {

	private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	//	private static Map<String, Integer> scores = new HashMap<>();

	public SearchEndpoint() {
		super("search", SearchResult.class);
	}

	public SearchEndpoint format(MtgFormat format) {
		String key = "format";
		resetQueryParam(key);
		String formatId = MtgTop8Api.convert(format);
		target = target.queryParam(key, formatId);
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

	public SearchEndpoint archetype(int archetypeId, MtgFormat format) {
		String key = "archetype_sel["+MtgTop8Api.convert(format)+"]";
		resetQueryParam(key);
		target = target.queryParam(key, archetypeId);
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

	public SearchResult getAllAvailable() {
		SearchResult searchResult = null;
		for (int page=1; searchResult == null || searchResult.getDecks().size() < searchResult.getDecksMatching(); ++page) {
			System.out.print("requesting page "+page);
			SearchResult newSearchResult = page(page).get();
			if (newSearchResult.getDecks().isEmpty()) {
				System.out.println(" EMPTY");
				break;
			}
			System.out.println(" OK");
			if(searchResult == null) {
				searchResult = newSearchResult;
			} else {
				searchResult.getDecks().addAll(newSearchResult.getDecks());
			}
		}
		return searchResult;
	}

	@Override
	protected SearchResult parseReponse(Response response) {
		String html = response.readEntity(String.class);
		Document document = Jsoup.parse(html);

		SearchResult searchResult = new SearchResult();
		int deckCount = parseDocumentForDeckCount(document);
		searchResult.setDecksMatching(deckCount);

		List<SearchResultDeck> decks = parseDocumentForDecks(document);
		searchResult.setDecks(decks);


		return searchResult;
	}

	public final static int NO_MATCH = -2;
	public final static int TOO_MANY_CARDS = -3;
	public final static int OTHER_ERRROR = -4;

	private int parseDocumentForDeckCount(Document document) {
		if(document.toString().contains("No match for")) {
			// probably a double named card, where only the front side is wanted
			return NO_MATCH;
		} else if (document.toString().contains("Too many cards")) {
			// probably a split card, where only the front side got used
			return TOO_MANY_CARDS;
		}


		String sumText = document.select("table > tbody > tr > td > div[class=w_title]").text();
		Pattern pattern = Pattern.compile("([0-9]+) decks matching");
		Matcher matcher = pattern.matcher(sumText);
		if(matcher.matches()) {
			String countStr = matcher.group(1);
			int decksMatching = Integer.parseInt(countStr);
			return decksMatching;
		}
		return OTHER_ERRROR;
	}

	private List<SearchResultDeck> parseDocumentForDecks(Document document) {
		ArrayList<SearchResultDeck> decks = new ArrayList<>();
		Elements deckRows = document.select("table.Stable tr.hover_tr");
		for(Element deckRow : deckRows) {
			SearchResultDeck searchResultDeck = new SearchResultDeck();
			//			deckRow.select(evaluator)
			Elements columns = deckRow.select("td");
			Element deckNameElement = columns.get(1);
			String deckUrl = deckNameElement.select("a").attr("href");
			String deckName = deckNameElement.text();
			//			Element player = columns.get(2);
			String formatString = columns.get(3).text();
			MtgFormat format = MtgFormat.reverseConvert(formatString);
			//			Element event = columns.get(4);
			CompLevel compLevel = null;
			Elements elementsByTag = columns.get(5).getElementsByTag("img");
			int size = elementsByTag.size();
			switch (size) {
			case 1:
				if(elementsByTag.toString().contains("bigstar")) {
					compLevel = CompLevel.PROFESSIONAL;
				} else {
					compLevel = CompLevel.REGULAR;
				}
				break;
			case 2:
				compLevel = CompLevel.COMPETITIVE;
				break;
			case 3:
				compLevel = CompLevel.MAJOR;
				break;
			default:
				System.err.println("comp level error");
				break;
			}
			//			if(level.)
			String rank = columns.get(6).text();
			String dateText = columns.get(7).text();
			LocalDate date = LocalDate.parse(dateText, DateTimeFormatter.ofPattern("dd/MM/yy"));

			searchResultDeck.setCompLevel(compLevel);
			searchResultDeck.setDate(date);
			searchResultDeck.setRank(rank);
			searchResultDeck.setDeckName(deckName);
			searchResultDeck.setUrl(deckUrl);
			searchResultDeck.setFormat(format);

			decks.add(searchResultDeck);
		}
		return decks;
	}

}
