package de.rohmio.mtg.mtgtop8.api.endpoints;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.rohmio.mtg.mtgtop8.api.model.CompareResult;

public class CompareEndpoint extends AbstractEndpoint<Map<String, CompareResult>> {

	public CompareEndpoint() {
		super("compare", new GenericType<Map<String, CompareResult>>() {});
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
	protected Map<String, CompareResult> parseReponse(Response response) {
		String html = response.readEntity(String.class);
		Document document = Jsoup.parse(html);

		Map<String, CompareResult> compareResult = parseDocumentForCompareResult(document);
		return compareResult;
	}
	
	@Override
	public Map<String, CompareResult> get() {
		try {
			URL url = new URL("https://mtgtop8.com/compare?l="+deckIds);
			System.out.println("requesting: "+url);
			Document document = Jsoup.parse(url, 1000000);
			Map<String, CompareResult> compareResult = parseDocumentForCompareResult(document);
			return compareResult;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Map<String, CompareResult> parseDocumentForCompareResult(Document document) {
		Map<String, CompareResult> compareResults = new HashMap<>();
		Elements tableRows = document.select("html > body > div.page > div > div.page > table > tbody > tr");
		for (Element tableRow : tableRows) {
			String imgSrc = tableRow.select("td img").attr("src");
			if(imgSrc.equals("graph/checked.png")) {
				String cardName = tableRow.select("div.c2").text();
				CompareResult compareResult = new CompareResult(cardName);
				Elements cardNumberElements = tableRow.select("div.c");
				for (Element cardNumberElement : cardNumberElements) {
					String text = cardNumberElement.text();
					int amount;
					if (text.isBlank()) {
						amount = 0;
					} else {
						amount = Integer.parseInt(text);
					}
					compareResult.addCardAmount(amount);
				}
				if(compareResults.containsKey(cardName)) {
					compareResults.get(cardName).merge(compareResult);
				} else {
					compareResults.put(cardName, compareResult);
				}
			}
		}
		return compareResults;
	}

//		String sumText = document.select("table > tbody > tr > td > div[class=w_title]").text();
//		Pattern pattern = Pattern.compile("([0-9]+) decks matching");
//		Elements deckRows = document.select("table.Stable tr.hover_tr");

}
