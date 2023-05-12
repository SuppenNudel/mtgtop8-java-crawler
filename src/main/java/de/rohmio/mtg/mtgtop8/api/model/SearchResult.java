package de.rohmio.mtg.mtgtop8.api.model;

import java.util.List;

public class SearchResult {
	
	private int decksMatching;
	private List<SearchResultDeck> decks;
	
	
	public void setDecksMatching(int decksMatching) {
		this.decksMatching = decksMatching;
	}
	
	public int getDecksMatching() {
		return decksMatching;
	}
	
	public void setDecks(List<SearchResultDeck> decks) {
		this.decks = decks;
	}
	
	public List<SearchResultDeck> getDecks() {
		return decks;
	}

}
