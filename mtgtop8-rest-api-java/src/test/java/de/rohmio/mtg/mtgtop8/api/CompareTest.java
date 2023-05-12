package de.rohmio.mtg.mtgtop8.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.rohmio.mtg.mtgtop8.api.endpoints.DeckList;
import de.rohmio.mtg.mtgtop8.api.model.CompLevel;
import de.rohmio.mtg.mtgtop8.api.model.MtgTop8Format;
import de.rohmio.mtg.mtgtop8.api.model.SearchResult;
import de.rohmio.mtg.mtgtop8.api.model.SearchResultDeck;

public class CompareTest {

	private final static String RELEASE_LATEST_STANDARD_SET = "29/04/2022";

	@Test
	public void multipleDecks() {
		int[] deckIds = { 480306, 480208, 480371, 479906, 479917, 478854, 478339, 478050, 477928, 476372 };
		List<DeckList> deckLists = MtgTop8Api.compare().deckIds(deckIds).get();
		List<Integer> cardAmounts = deckLists.stream().map(deck -> deck.getCombined().get("Battlefield Forge")).collect(Collectors.toList());
		List<Integer> expected = Arrays.asList(0, 0, 4, 0, 0, 0, 0, 4, 4, 4);
		assertEquals(expected, cardAmounts);
	}

	@Test
	public void cardInMainAndSide() {
		int deckId = 479917;
		List<DeckList> deckLists = MtgTop8Api.compare().deckIds(deckId).get();
		assertEquals(4, deckLists.get(0).getCombined().get("Bonecrusher Giant"));
	}

	@Test
	public void enduranceWith5InDeck() {
		int decksMatching = -1;
		int decksFound = 0;
		String cardName = "Endurance";
		for (int page = 1; decksMatching < 0 || decksFound < decksMatching; ++page) {
			SearchResult searchResult = MtgTop8Api.search().compLevel(CompLevel.COMPETITIVE, CompLevel.MAJOR, CompLevel.PROFESSIONAL).startdate("29/04/2022")
					.format(MtgTop8Format.MODERN).page(page).cards(cardName).get();
			decksMatching = searchResult.getDecksMatching();
			List<SearchResultDeck> decks = searchResult.getDecks();
			List<Integer> deckIds = decks.stream().map(SearchResultDeck::getDeckId).collect(Collectors.toList());

			List<DeckList> deckLists = MtgTop8Api.compare().deckIds(deckIds).get();
			List<Integer> cardAmounts = deckLists.stream().map(deck -> deck.getCombined().get(cardName)).collect(Collectors.toList());
			if(cardAmounts.contains(5)) {
				System.out.println();
			}

			decksFound += decks.size();
		}
	}

	@Test
	public void allDeckIds() {
		List<Integer> allDeckIds = new ArrayList<>();
		boolean noMore = false;
		for (int page = 1; !noMore; ++page) {
			SearchResult searchResult = MtgTop8Api.search().format(MtgTop8Format.PIONEER)
					.compLevel(CompLevel.COMPETITIVE, CompLevel.MAJOR, CompLevel.PROFESSIONAL).mainboard(true)
					.sideboard(true).startdate(RELEASE_LATEST_STANDARD_SET).page(page).get();

			List<SearchResultDeck> decks = searchResult.getDecks();
			if (decks.size() < 25) {
				noMore = true;
			}

			List<Integer> deckIds = decks.stream().map(SearchResultDeck::getDeckId).collect(Collectors.toList());
			allDeckIds.addAll(deckIds);
		}
		List<DeckList> compareResults = MtgTop8Api.compare().deckIds(allDeckIds).get();
		System.out.println();
	}

	@Test
	public void compare() {
		// cardname, max
		Map<String, Integer> allCompareResults = new HashMap<>();

		boolean noMore = false;
		for (int page = 1; !noMore; ++page) {
			SearchResult searchResult = MtgTop8Api.search().format(MtgTop8Format.PIONEER)
					.compLevel(CompLevel.COMPETITIVE, CompLevel.MAJOR, CompLevel.PROFESSIONAL).mainboard(true)
					.sideboard(true).startdate(RELEASE_LATEST_STANDARD_SET).page(page).get();

			List<SearchResultDeck> decks = searchResult.getDecks();
			if (decks.size() < 25) {
				noMore = true;
			}

			List<Integer> deckIds = decks.stream().map(SearchResultDeck::getDeckId).collect(Collectors.toList());

			List<DeckList> compareResults = MtgTop8Api.compare().deckIds(deckIds).get();

			//			compareResults.forEach(deck -> {
			//				Integer integer = allCompareResults.get(key);
			//				Integer max = value.getCardAmounts().stream().max((int1, int2) -> int1.compareTo(int2)).get();
			//				if (integer == null) {
			//					allCompareResults.put(key, max);
			//				} else {
			//					if (max > integer) {
			//						allCompareResults.put(key, max);
			//					}
			//				}
			//			});

		}
		Set<String> keySet = allCompareResults.keySet();
		ArrayList<String> sorted = new ArrayList<>(keySet);
		Collections.sort(sorted);
		for (String cardName : sorted) {
			Integer max = allCompareResults.get(cardName);
			System.out.println(max + " " + cardName);
		}
	}

}
