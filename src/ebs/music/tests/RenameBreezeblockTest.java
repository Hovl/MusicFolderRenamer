package ebs.music.tests;

import ebs.music.MusicFileNamePattern;
import ebs.music.RenameBreezeblock;
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
public class RenameBreezeblockTest extends TestCase {
	public void testPattern0() throws ParseException {
		String testName = "1997-06-23 Breezeblock - Death in Vegas.mp3";
		testPattern(0, testName, 1997, 6, 23, "", "", "Death in Vegas", ".mp3");
	}

	public void testPattern1() throws ParseException {
		String testName = "01 - James - Homeboy (Breezeblock Session).mp3";
		testPattern(1, testName, 0, 0, 0, "1", "", "Homeboy (Breezeblock Session)", ".mp3");
	}

	public void testPattern2() throws ParseException {
		String testName = "4 - Interview.mp3";
		testPattern(2, testName, 0, 0, 0, "4", "", "Interview", ".mp3");
	}

	public void testPattern3() throws ParseException {
		String testName = "1997-09-22 Breezeblock - 03 -  DJ Set by DJ Touche(The Wiseguys).mp3";
		testPattern(3, testName, 1997, 9, 22, "3", "", " DJ Set by DJ Touche(The Wiseguys)", ".mp3");
	}

	public void testPattern4() throws ParseException {
		String testName = "1997-10-20 Breezeblock - Dub Pistols pt 1.mp3";
		testPattern(4, testName, 1997, 10, 20, "1", "", "Dub Pistols", ".mp3");

		testName = "1997-10-22 Breezeblock - Glamorous Hooligan pt1.mp3";
		testPattern(4, testName, 1997, 10, 22, "1", "", "Glamorous Hooligan", ".mp3");
	}

	public void testPattern5() throws ParseException {
		String testName = "1997-12-15 Breezeblock - Primal Scream - Part 2.mp3";
		testPattern(5, testName, 1997, 12, 15, "2", "", "Primal Scream", ".mp3");
	}

	public void testPattern6() throws ParseException {
		String testName = "1998-07-00 Breezeblock - Add N To X Part 1.mp3";
		testPattern(6, testName, 1998, 6, 30, "1", "", "Add N To X", ".mp3");
	}

	public void testPattern7() throws ParseException {
		String testName = "02 mix.mp3";
		testPattern(7, testName, 0, 0, 0, "2", "", "mix", ".mp3");
	}

	public void testPattern8() throws ParseException {
		String testName = "1999-01-18 Breezeblock - Bentley Rhythm Ace 1.mp3";
		testPattern(8, testName, 1999, 1, 18, "1", "", "Bentley Rhythm Ace", ".mp3");
	}

	public void testPattern9() throws ParseException {
		String testName = "Radio 1 Breezeblock 1999 - 01 - Hosted By Goldie.mp3";
		testPattern(9, testName, 0, 0, 0, "1", "", "Hosted By Goldie", ".mp3");

		testName = "Radio 1 Breezeblock 1999 - 17 - Hosted By Goldie.mp3";
		testPattern(9, testName, 0, 0, 0, "17", "", "Hosted By Goldie", ".mp3");
	}

	public void testPattern10() throws ParseException {
		String testName = "Up, Bustle - (10) Richard Egues - Descarga Con Cate.mp3";
		testPattern(10, testName, 0, 0, 0, "10", "", "Richard Egues - Descarga Con Cate", ".mp3");
	}

	private void testPattern(Integer patternID, String testName, int year, Integer month, int day,
							 String p1,
							 String p2,
							 String name, String ext) throws ParseException {
		MusicFileNamePattern pattern = RenameBreezeblock.BREEZEBLOCK_PATTERNS[patternID];

		Matcher matcher = pattern.getPattern().matcher(testName);
		assertTrue(matcher.find());

		if (year > 0) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(pattern.getDateFormat().parse(matcher.group(pattern.getDate())));

			assertEquals(testName, year, calendar.get(Calendar.YEAR));
			assertEquals(testName, month - 1, calendar.get(Calendar.MONTH));
			assertEquals(testName, day, calendar.get(Calendar.DAY_OF_MONTH));
		}

		String part1 = pattern.getPart1() == 0 ? "" : matcher.group(pattern.getPart1());
		String part2 = pattern.getPart2() == 0 ? "" : matcher.group(pattern.getPart2());
		assertEquals(testName, p1, part1);
		assertEquals(testName, p2, part2);

		assertEquals(testName, name, matcher.group(pattern.getName()));

		assertEquals(testName, ext, matcher.group(pattern.getExtension()));
	}
}
