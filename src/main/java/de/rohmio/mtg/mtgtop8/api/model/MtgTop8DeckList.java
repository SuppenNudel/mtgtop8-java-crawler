package de.rohmio.mtg.mtgtop8.api.model;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import io.github.suppennudel.mtg.generic.MtgDeckInfo;

public class MtgTop8DeckList extends MtgDeckInfo {

	public MtgTop8DeckList(String name) {
		super(name);

		setDate(LocalDate.now());
	}

	public void putCard(String cardName, int amount, ListSection listSection) {
		Map<String, Integer> theMap;
		switch (listSection) {
		case MAIN: theMap = getMain(); break;
		case SIDEBOARD: theMap = getSide(); break;
		default:
			throw new UnsupportedOperationException(listSection+ " is not supported");
		}
		theMap.put(cardName, amount);
	}


	@Override
	public void parse(File file) throws IOException {
		// TODO Auto-generated method stub

	}

}
