package de.rohmio.mtg.mtgtop8.api.model;

import java.util.ArrayList;
import java.util.List;

public class CompareResult {

	private String cardName;
	private List<Integer> cardAmounts = new ArrayList<>();

	public CompareResult(String cardName) {
		this.cardName = cardName;
	}

	public void addCardAmount(int amount) {
		cardAmounts.add(amount);
	}

	public List<Integer> getCardAmounts() {
		return cardAmounts;
	}

	public String getCardName() {
		return cardName;
	}

	public void merge(CompareResult compareResult) {
		for (int i = 0; i < cardAmounts.size(); ++i) {
			cardAmounts.set(i, cardAmounts.get(i) + compareResult.getCardAmounts().get(i));
		}
	}

}
