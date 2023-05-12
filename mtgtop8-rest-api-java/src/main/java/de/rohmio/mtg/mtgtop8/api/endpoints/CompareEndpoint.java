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

public class CompareEndpoint extends AbstractEndpoint<List<DeckList>> {

	public CompareEndpoint() {
		super("compare", new GenericType<List<DeckList>>() {});
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
	protected List<DeckList> parseReponse(Response response) {
		String html = response.readEntity(String.class);
		Document document = Jsoup.parse(html);

		List<DeckList> compareResult = parseDocumentForCompareResult(document);
		return compareResult;
	}

	@Override
	public List<DeckList> get() {
		try {
			String urlString = "https://mtgtop8.com/compare?l="+deckIds;
			//			URL url = new URL(urlString);
			//			Document document = Jsoup.parse(url, Integer.MAX_VALUE);
			Document document = Jsoup.connect(urlString).maxBodySize(0).timeout(60000).get();
			List<DeckList> compareResult = parseDocumentForCompareResult(document);
			return compareResult;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<DeckList> parseDocumentForCompareResult(Document document) {
		Map<Integer, DeckList> decks = new HashMap<>();
		Elements tableRows = document.select("html > body > div.page > div > div.page > table > tbody > tr");

		boolean main = true;
		for (Element tableRow : tableRows) {
			String category = tableRow.select("td[align=center]").text();
			if("SIDEBOARDS".equals(category)) {
				main = false;
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
						DeckList deckList = decks.getOrDefault(deckNumber, new DeckList());
						decks.putIfAbsent(deckNumber, deckList);
						deckList.putCard(cardName, amount, main);
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
