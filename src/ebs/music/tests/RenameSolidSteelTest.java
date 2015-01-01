package ebs.music.tests;

import ebs.music.MusicFileNamePattern;
import ebs.music.RenameSolidSteel;
import junit.framework.TestCase;

import java.text.ParseException;
import java.util.Calendar;
import java.util.regex.Matcher;

/**
 * Created by Aleksey Dubov
 * Date: 14/02/20
 * Time: 15:00
 * Copyright (c) 2014
 */
public class RenameSolidSteelTest extends TestCase {
	public void testPattern12() throws ParseException {
		String testName = "Ninja Tune - Solid Steel Radio Show 6-9-2013 Part 1 + 2 - Letherette.mp3";
		testPattern(12, testName, 2013, 9, 6, "1", "2", "Letherette", ".mp3");

		testName = "Ninja Tune - Solid Steel Radio Show 6-9-2013 Part 3 + 4 - DK + Thur Deephrey.mp3";
		testPattern(12, testName, 2013, 9, 6, "3", "4", "DK + Thur Deephrey", ".mp3");

		testName = "Ninja Tune - Solid Steel Radio Show 13-9-2013 Part 1 + 2 - London Elektricity.mp3";
		testPattern(12, testName, 2013, 9, 13, "1", "2", "London Elektricity", ".mp3");

		testName = "Solid Steel Radio Show 4-10-2013 Part 1 + 2 - DJ Food.mp3";
		testPattern(12, testName, 2013, 10, 4, "1", "2", "DJ Food", ".mp3");
	}

	public void testPattern13() throws ParseException {
		String testName = "Solid Steel Radio Show 1-11-2013 Part 1 - Four Tet.mp3";
		testPattern(13, testName, 2013, 11, 1, "1", "", "Four Tet", ".mp3");
	}

	public void testPattern14() throws ParseException {
		String testName = "Solid Steel Radio Show 29-11-2013 - Part 1 + 2 - Toddla T.mp3";
		testPattern(14, testName, 2013, 11, 29, "1", "2", "Toddla T", ".mp3");
	}

	public void testPattern15() throws ParseException {
		String testName = "Solid Steel Radio Show 312014 Part 1 + 2 - Mr Scruff + Illum Sphere.mp3";
		testPattern(15, testName, 2014, 1, 3, "1", "2", "Mr Scruff + Illum Sphere", ".mp3");
	}

	public void testPattern16() throws ParseException {
		String testName = "Solid Steel Radio Show 30112014 Part 1 + 2 - Mr Scruff + Illum Sphere.mp3";
		testPattern(16, testName, 2014, 11, 30, "1", "2", "Mr Scruff + Illum Sphere", ".mp3");
	}

	public void testPattern17() throws ParseException {
		String testName = "Solid Steel Radio Show 3122014 Part 1 + 2 - Mr Scruff + Illum Sphere.mp3";
		testPattern(17, testName, 2014, 12, 3, "1", "2", "Mr Scruff + Illum Sphere", ".mp3");
	}

	private void testPattern(Integer patternID, String testName, int year, Integer month, int day,
							 String p1,
							 String p2,
							 String name, String ext) throws ParseException {
		MusicFileNamePattern pattern = RenameSolidSteel.SOLID_STEEL_PATTERNS[patternID];

		Matcher matcher = pattern.getPattern().matcher(testName);
		assertTrue(matcher.find());

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(pattern.getDateFormat().parse(matcher.group(pattern.getDate())));

		assertEquals(testName, year, calendar.get(Calendar.YEAR));
		assertEquals(testName, month - 1, calendar.get(Calendar.MONTH));
		assertEquals(testName, day, calendar.get(Calendar.DAY_OF_MONTH));

		String part1 = pattern.getPart1() == 0 ? "" : matcher.group(pattern.getPart1());
		String part2 = pattern.getPart2() == 0 ? "" : matcher.group(pattern.getPart2());
		assertEquals(testName, p1, part1);
		assertEquals(testName, p2, part2);

		assertEquals(testName, name, matcher.group(pattern.getName()));

		assertEquals(testName, ext, matcher.group(pattern.getExtension()));
	}
}
