package de.rohmio.mtg.mtgtop8.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.rohmio.mtg.mtgtop8.api.endpoints.SearchEndpoint;
import de.rohmio.mtg.mtgtop8.api.model.CompLevel;
import de.rohmio.mtg.mtgtop8.api.model.SearchResult;
import de.rohmio.mtg.mtgtop8.api.model.SearchResultDeck;
import io.github.suppennudel.mtg.generic.MtgFormat;

public class RequestTest {

	private SearchEndpoint endpoint;

	@BeforeEach
	public void generateBase() {
		endpoint = MtgTop8Api.search()
				.enddate("25/05/2020")
				.mainboard(true)
				.sideboard(true)
				.format(MtgFormat.STANDARD);
	}

	@Test
	public void deckTableTest() {
		List<SearchResultDeck> allDecks = new ArrayList<>();
		boolean noMore = false;
		for(int page = 1; !noMore; ++page) {
			SearchResult searchResult = MtgTop8Api.search()
					.startdate("29/04/2022")
					.enddate(LocalDate.of(2022, 7, 20))
					.mainboard(true)
					.sideboard(true)
					.compLevel(CompLevel.COMPETITIVE, CompLevel.MAJOR, CompLevel.PROFESSIONAL)
					.format(MtgFormat.PIONEER)
					//			.cards("Bonecrusher Giant")
					.page(page)
					.get();
			List<SearchResultDeck> decks = searchResult.getDecks();
			allDecks.addAll(decks);
			if(decks.size() < 25) {
				noMore = true;
			}
			decks.get(0).getDeckId();
			if(page == 1) {
				assertEquals(25, decks.size());
			}
		}
		System.out.println();
		allDecks.forEach(d -> System.out.print("_"+d.getDeckId()));
		assertEquals(472, allDecks.size());
	}

	@Test
	public void noCardnameTest() throws IOException {
		SearchResult searchResult = endpoint.get();
		assertEquals(43833, searchResult.getDecksMatching());
	}


	@Test
	public void normalTest() throws IOException {
		SearchResult searchResult = endpoint.cards("Abrade").get();
		assertEquals(2122, searchResult.getDecksMatching());
	}

	@Test
	public void apostropheTest() throws IOException {
		SearchResult searchResult = endpoint.cards("Admiral's Order").get();
		assertEquals(18, searchResult.getDecksMatching());
	}

	@Test
	public void umlautTest() throws IOException {
		SearchResult searchResult = endpoint.cards("Jötun Grunt").get();
		assertEquals(2, searchResult.getDecksMatching());
	}

	@Test
	public void aeTest() throws IOException {
		SearchResult searchResult = endpoint.cards("Æther Adept").get();
		assertEquals(22, searchResult.getDecksMatching());
	}

}
