package de.rohmio.mtg.mtgtop8.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.rohmio.mtg.mtgtop8.api.endpoints.CompareEndpoint;
import de.rohmio.mtg.mtgtop8.api.model.CompLevel;
import de.rohmio.mtg.mtgtop8.api.model.CompareResult;
import de.rohmio.mtg.mtgtop8.api.model.MtgTop8Format;
import de.rohmio.mtg.mtgtop8.api.model.SearchResult;
import de.rohmio.mtg.mtgtop8.api.model.SearchResultDeck;

public class CompareTest {

	private final static String RELEASE_LATEST_STANDARD_SET = "29/04/2022";

	@Test
	public void multipleDecks() {
		int[] deckIds = { 480306, 480208, 480371, 479906, 479917, 478854, 478339, 478050, 477928, 476372 };
		Map<String, CompareResult> map = new CompareEndpoint().deckIds(deckIds).get();
		CompareResult compareResult = map.get("Battlefield Forge");
		List<Integer> expected = Arrays.asList(0, 0, 4, 0, 0, 0, 0, 4, 4, 4);
		assertEquals(expected, compareResult.getCardAmounts());
	}

	@Test
	public void cardInMainAndSide() {
		int deckId = 479917;
		Map<String, CompareResult> map = new CompareEndpoint().deckIds(deckId).get();
		assertEquals(Arrays.asList(4), map.get("Bonecrusher Giant").getCardAmounts());
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
			List<Integer> deckIds = decks.stream().map(d -> d.getDeckId()).collect(Collectors.toList());

			Map<String, CompareResult> map = new CompareEndpoint().deckIds(deckIds).get();
			CompareResult compareResult = map.get(cardName);
			if(compareResult.getCardAmounts().contains(5)) {
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

			List<Integer> deckIds = decks.stream().map(d -> d.getDeckId()).collect(Collectors.toList());
			allDeckIds.addAll(deckIds);
		}
		CompareEndpoint compareEndpoint = new CompareEndpoint();
		Map<String, CompareResult> compareResults = compareEndpoint.deckIds(allDeckIds).get();
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

			List<Integer> deckIds = decks.stream().map(d -> d.getDeckId()).collect(Collectors.toList());

			CompareEndpoint compareEndpoint = new CompareEndpoint();
			Map<String, CompareResult> compareResults = compareEndpoint.deckIds(deckIds).get();

			compareResults.forEach(new BiConsumer<String, CompareResult>() {
				@Override
				public void accept(String key, CompareResult value) {
					Integer integer = allCompareResults.get(key);
					Integer max = value.getCardAmounts().stream().max((int1, int2) -> int1.compareTo(int2)).get();
					if (integer == null) {
						allCompareResults.put(key, max);
					} else {
						if (max > integer) {
							allCompareResults.put(key, max);
						}
					}
				}
			});

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
