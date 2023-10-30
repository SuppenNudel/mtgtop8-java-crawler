package de.rohmio.mtg.mtgtop8.api.model;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.suppennudel.mtg.generic.MtgFormat;

public class SearchResultDeck {

	private static final Pattern URL_PATTERN = Pattern.compile("event\\?e=([0-9]+)&d=([0-9]+)&f=\\w+");

	private Integer deckId;
	private Integer eventId;

	private String deckName;
	private String url;
	private String player;
	private MtgFormat format;
	private String eventName;
	private CompLevel compLevel;
	private String rank;
	private LocalDate date;

	@Override
	public String toString() {
		return String.format("%s - %s - %s - %s - %s - %s - %s", deckName, player, format, eventName, compLevel, rank, date);
	}

	public MtgFormat getFormat() {
		return format;
	}
	public String getEventName() {
		return eventName;
	}
	public String getPlayer() {
		return player;
	}

	public void setDeckName(String deckName) {
		this.deckName = deckName;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setCompLevel(CompLevel compLevel) {
		this.compLevel = compLevel;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getDeckName() {
		return deckName;
	}
	public String getUrl() {
		return url;
	}

	public Integer getDeckId() {
		if(deckId == null) {
			matchUrl();
		}
		return deckId;
	}

	public Integer getEventId() {
		if(eventId == null) {
			matchUrl();
		}
		return eventId;
	}

	private void matchUrl() {
		Matcher matcher = URL_PATTERN.matcher(url);
		if(matcher.matches()) {
			eventId = Integer.parseInt(matcher.group(1));
			deckId = Integer.parseInt(matcher.group(2));
		}
	}

	public CompLevel getCompLevel() {
		return compLevel;
	}
	public String getRank() {
		return rank;
	}
	public LocalDate getDate() {
		return date;
	}

	public void setFormat(MtgFormat format) {
		this.format = format;
	}

}
