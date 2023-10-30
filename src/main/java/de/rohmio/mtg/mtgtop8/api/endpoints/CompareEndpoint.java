package de.rohmio.mtg.mtgtop8.api.endpoints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.rohmio.mtg.mtgtop8.api.model.MtgTop8DeckList;
import io.github.suppennudel.mtg.generic.MtgDeckInfo;
import io.github.suppennudel.mtg.generic.MtgDeckInfo.ListSection;

public class CompareEndpoint extends AbstractEndpoint<List<MtgTop8DeckList>> {

	public CompareEndpoint() {
		super("compare", new GenericType<List<MtgTop8DeckList>>() {});
	}

	private String deckIds;

	public CompareEndpoint deckIds(int... deckIds) {
		String key = "l";
		resetQueryParam(key);
		StringBuilder sb = new StringBuilder();
		for (int deckId : deckIds) {
			sb.append("_" + deckId);
		}
		this.deckIds = sb.toString();
		target = target.queryParam(key, sb.toString());
		return this;
	}

	public CompareEndpoint deckIds(List<Integer> deckIds) {
		String key = "l";
		resetQueryParam(key);
		StringBuilder sb = new StringBuilder();
		for (Integer deckId : deckIds) {
			sb.append("_" + deckId);
		}
		this.deckIds = sb.toString();
		target = target.queryParam(key, sb.toString());
		return this;
	}

	@Override
	protected List<MtgTop8DeckList> parseReponse(Response response) {
		String html = response.readEntity(String.class);
		Document document = Jsoup.parse(html);

		List<MtgTop8DeckList> compareResult = parseDocumentForCompareResult(document);
		return compareResult;
	}

	@Override
	public List<MtgTop8DeckList> get() {
		try {
			String urlString = "https://mtgtop8.com/compare?l="+deckIds;
			//			URL url = new URL(urlString);
			//			Document document = Jsoup.parse(url, Integer.MAX_VALUE);
			Document document = Jsoup.connect(urlString).maxBodySize(0).timeout(60000).get();
			List<MtgTop8DeckList> compareResult = parseDocumentForCompareResult(document);
			return compareResult;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<MtgTop8DeckList> parseDocumentForCompareResult(Document document) {
		Map<Integer, MtgTop8DeckList> decks = new HashMap<>();
		Elements tableRows = document.select("html > body > div.page > div > div.page > table > tbody > tr");

		MtgDeckInfo.ListSection listSection = ListSection.MAIN;
		for (Element tableRow : tableRows) {
			String category = tableRow.select("td[align=center]").text();
			if("SIDEBOARDS".equals(category)) {
				listSection = ListSection.SIDEBOARD;
			}
			String cardName = tableRow.select("div.c2").text();
			if(!cardName.isEmpty()) {
				Elements cardNumberElements = tableRow.select("div.c");

				int deckNumber = 0;
				for (Element cardNumberElement : cardNumberElements) {
					++deckNumber;
					String amountText = cardNumberElement.text();
					if (!amountText.isEmpty()) {
						int amount = Integer.parseInt(amountText);
						MtgTop8DeckList deckList = decks.getOrDefault(deckNumber, new MtgTop8DeckList("mtgtop8 default deck name"));
						decks.putIfAbsent(deckNumber, deckList);
						deckList.putCard(cardName, amount, listSection);
					}
				}
			}
		}
		return new ArrayList<>(decks.values());
	}

	//		String sumText = document.select("table > tbody > tr > td > div[class=w_title]").text();
	//		Pattern pattern = Pattern.compile("([0-9]+) decks matching");
	//		Elements deckRows = document.select("table.Stable tr.hover_tr");

}
