package ebs.music;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
* Created by Aleksey Dubov
* Date: 6/9/13
* Time: 3:39 AM
* Copyright (c) 2013
*/
public class SolidSteelPattern {
	private Pattern pattern;
	private SimpleDateFormat dateFormat;
	private Integer date;
	private Integer name;
	private Integer part1;
	private Integer part2;
	private Integer extension;

	public SolidSteelPattern(Pattern pattern, SimpleDateFormat dateFormat, Integer date, Integer name,
							 Integer part1, Integer part2, Integer extension) {
		this.pattern = pattern;
		this.dateFormat = dateFormat;
		this.date = date;
		this.name = name;
		this.part1 = part1;
		this.part2 = part2;
		this.extension = extension;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	public Integer getDate() {
		return date;
	}

	public Integer getName() {
		return name;
	}

	public Integer getPart1() {
		return part1;
	}

	public Integer getPart2() {
		return part2;
	}

	public Integer getExtension() {
		return extension;
	}
}
