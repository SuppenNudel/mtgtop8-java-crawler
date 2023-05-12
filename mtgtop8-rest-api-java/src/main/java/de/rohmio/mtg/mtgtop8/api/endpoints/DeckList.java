package de.rohmio.mtg.mtgtop8.api.endpoints;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DeckList {

	private Map<String, Integer> combined;

	private Map<String, Integer> main = new HashMap<>();
	private Map<String, Integer> side = new HashMap<>();

	protected void putCard(String cardName, int amount, boolean toMain) {
		Map<String, Integer> theMap = toMain ? main : side;
		theMap.put(cardName, amount);
	}

	private void combine() {
		combined = new HashMap<>();
		List<Map<String, Integer>> maps = Arrays.asList(main, side);
		for(Map<String, Integer> map : maps) {
			for (Entry<String, Integer> entry : map.entrySet()) {
				combined.merge(entry.getKey(), entry.getValue(), (current, toAdd) -> current + toAdd);
			}
		}
	}

	public Map<String, Integer> getCombined() {
		if(combined == null) {
			combine();
		}
		return combined;
	}

}
