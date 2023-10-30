package de.rohmio.mtg.mtgtop8.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.rohmio.mtg.mtgtop8.api.endpoints.SearchEndpoint;
import de.rohmio.mtg.mtgtop8.api.model.CompLevel;
import de.rohmio.mtg.mtgtop8.api.model.SearchResult;
import io.github.suppennudel.mtg.generic.MtgFormat;

public class ScoringTest {

	private SearchEndpoint endpoint;

	@BeforeEach
	public void generateBase() {
		endpoint = MtgTop8Api.search()
				.startdate("25/05/2019")
				.enddate("25/05/2020")
				.mainboard(true)
				.sideboard(true)
				.cards("Lightning Bolt");
	}

	@Test
	public void differenStartDate() throws IOException {
		endpoint.format(MtgFormat.MODERN);
		endpoint.compLevel(CompLevel.PROFESSIONAL);

		assertEquals(5, endpoint.get());

		endpoint.startdate("25/05/2018");
		assertEquals(26, endpoint.get());
	}

	@Test
	public void compareScoringMethods() throws IOException {
		endpoint.format(MtgFormat.MODERN);

		// all
		//		System.out.println(calculateScore(mtgTop8Search.getDecks()));

		endpoint.compLevel(CompLevel.PROFESSIONAL);
		assertEquals(5, endpoint.get());

		endpoint.compLevel(CompLevel.MAJOR);
		assertEquals(227, endpoint.get());

		endpoint.compLevel(CompLevel.COMPETITIVE);
		assertEquals(814, endpoint.get());

		endpoint.compLevel(CompLevel.REGULAR);
		assertEquals(1147, endpoint.get());
	}

	@Test
	public void allFormats() throws IOException {
		int allDecks = 0;
		SearchResult decks = endpoint.get();
		for(MtgFormat format : MtgFormat.values()) {
			endpoint.format(format);
			allDecks += endpoint.get().getDecksMatching();
		}

		assertEquals(allDecks, decks.getDecksMatching());
	}

	@Test
	public void requestDecks() throws IOException, InterruptedException {
		endpoint.format(MtgFormat.MODERN);

		endpoint.cards("Lightning Bolt");
		int decks1 = endpoint.get().getDecksMatching();
		assertEquals(2193, decks1);


		endpoint.cards("Birds of Paradise");
		int decks2 = endpoint.get().getDecksMatching();
		assertEquals(415, decks2);


		endpoint.cards("Leyline of the Void");
		int decks3 = endpoint.get().getDecksMatching();
		assertEquals(954, decks3);


		endpoint.cards("Devoted Druid");
		int decks4 = endpoint.get().getDecksMatching();
		assertEquals(203, decks4);
	}

}
