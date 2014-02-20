package ebs.music;

import java.util.Date;

/**
 * Created by Aleksey Dubov
 * Date: 14/02/20
 * Time: 14:20
 * Copyright (c) 2014
 */
public class MusicFileData {
	private Date date;
	private String name;
	private String part1;
	private String part2;
	private String extension;

	public MusicFileData(Date date, String name, String part1, String part2, String extension) {
		this.date = date;
		this.name = name;
		this.part1 = part1;
		this.part2 = part2;
		this.extension = extension;
	}

	public Date getDate() {
		return date;
	}

	public String getName() {
		return name;
	}

	public String getPart1() {
		return part1;
	}

	public String getPart2() {
		return part2;
	}

	public String getExtension() {
		return extension;
	}
}
