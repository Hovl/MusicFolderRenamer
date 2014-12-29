package ebs.music;

import java.text.SimpleDateFormat;

/**
 * Created by Aleksey Dubov
 * Date: 14/02/20
 * Time: 13:53
 * Copyright (c) 2014
 */
public class TagsData {
	private String band;
	private SimpleDateFormat dateFormat;
	private String genre;
	private Integer genreNumber;

	public TagsData(String band, SimpleDateFormat dateFormat, String genre) {
		this.band = band;
		this.dateFormat = dateFormat;
		this.genre = genre;
	}

	public TagsData(String band, SimpleDateFormat dateFormat, Integer genreNumber) {
		this.band = band;
		this.dateFormat = dateFormat;
		this.genreNumber = genreNumber;
	}

	public String getBand() {
		return band;
	}

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	public String getGenre() {
		return genre;
	}

	public Integer getGenreNumber() {
		return genreNumber;
	}
}
