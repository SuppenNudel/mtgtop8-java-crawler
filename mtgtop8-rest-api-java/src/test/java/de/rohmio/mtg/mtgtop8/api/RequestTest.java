package de.rohmio.mtg.mtgtop8.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.rohmio.mtg.mtgtop8.api.endpoints.SearchEndpoint;
import de.rohmio.mtg.mtgtop8.api.model.MtgTop8Format;

public class RequestTest {

	private SearchEndpoint endpoint;

	@BeforeEach
	public void generateBase() {
		endpoint = MtgTop8Api.search()
		.enddate("25/05/2020")
		.mainboard(true)
		.sideboard(true)
		.format(MtgTop8Format.STANDARD);
	}

	@Test
	public void noCardnameTest() throws IOException {
		int deckCount = endpoint.get();
		assertEquals(43809, deckCount);
	}


	@Test
	public void normalTest() throws IOException {
		int deckCount = endpoint.cards("Abrade").get();
		assertEquals(2122, deckCount);
	}

	@Test
	public void apostropheTest() throws IOException {
		int deckCount = endpoint.cards("Admiral's Order").get();
		assertEquals(18, deckCount);
	}

	@Test
	public void umlautTest() throws IOException {
		int deckCount = endpoint.cards("Jötun Grunt").get();
		assertEquals(2, deckCount);
	}

	@Test
	public void aeTest() throws IOException {
		int deckCount = endpoint.cards("Æther Adept").get();
		assertEquals(22, deckCount);
	}

}
