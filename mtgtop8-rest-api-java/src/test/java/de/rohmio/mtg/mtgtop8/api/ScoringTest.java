package de.rohmio.mtg.mtgtop8.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.rohmio.mtg.mtgtop8.api.endpoints.SearchEndpoint;
import de.rohmio.mtg.mtgtop8.api.model.CompLevel;
import de.rohmio.mtg.mtgtop8.api.model.MtgTop8Format;

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
		endpoint.format(MtgTop8Format.MODERN);
		endpoint.compLevel(CompLevel.PROFESSIONAL);

		assertEquals(5, endpoint.get());

		endpoint.startdate("25/05/2018");
		assertEquals(26, endpoint.get());
	}

	@Test
	public void compareScoringMethods() throws IOException {
		endpoint.format(MtgTop8Format.MODERN);

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
		int decks = endpoint.get();
		for(MtgTop8Format format : MtgTop8Format.values()) {
			endpoint.format(format);
			allDecks += endpoint.get();
		}

		assertEquals(allDecks, decks);
	}

	@Test
	public void requestDecks() throws IOException, InterruptedException {
		endpoint.format(MtgTop8Format.MODERN);

		endpoint.cards("Lightning Bolt");
		int decks1 = endpoint.get();
		assertEquals(2193, decks1);


		endpoint.cards("Birds of Paradise");
		int decks2 = endpoint.get();
		assertEquals(415, decks2);


		endpoint.cards("Leyline of the Void");
		int decks3 = endpoint.get();
		assertEquals(954, decks3);


		endpoint.cards("Devoted Druid");
		int decks4 = endpoint.get();
		assertEquals(203, decks4);
	}

}
