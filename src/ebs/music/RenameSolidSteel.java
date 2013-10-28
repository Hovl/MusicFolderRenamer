package ebs.music;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Aleksey Dubov
 * Date: 6/17/12
 * Time: 4:30 PM
 * Copyright (c) 2012
 */
public class RenameSolidSteel {
	private static final Logger LOGGER = Logger.getLogger(RenameSolidSteel.class.getName());

	{
		try {
			FileHandler handler = new FileHandler("/RenameSolidSteel%u.log");
			handler.setLevel(Level.ALL);
			LOGGER.addHandler(handler);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static final SimpleDateFormat SRC1_DATE_FORMAT = new SimpleDateFormat("dd.MM.yy");
	private static final SimpleDateFormat SRC4_DATE_FORMAT = new SimpleDateFormat("dd_MM_yyyy");
	private static final SimpleDateFormat SRC6_DATE_FORMAT = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
	private static final SimpleDateFormat SRC8_DATE_FORMAT = new SimpleDateFormat("dd-MM-yy");
	private static final SimpleDateFormat SRC11_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
	private static final SimpleDateFormat SRC12_DATE_FORMAT = new SimpleDateFormat("yyyy MMM dd", Locale.ENGLISH);
	private static final Pattern SRC12_DATE_PATTERN = Pattern.compile("(..._\\d\\d\\))-2cd[r]-(\\d\\d\\d\\d)");

	private static final SolidSteelPattern[] SOLID_STEEL_PATTERNS = new SolidSteelPattern[]{
			new SolidSteelPattern(Pattern.compile("(\\d\\d.\\d\\d.\\d\\d) - (.+) \\(pt.(\\d)\\)(\\..+)"), //1
					SRC1_DATE_FORMAT, 1, 2, 3, 0, 4),
			new SolidSteelPattern(Pattern.compile("(\\d\\d.\\d\\d.\\d\\d) - (.+) \\(pt.(\\d).+(\\d)\\)(\\..+)"), //2
					SRC1_DATE_FORMAT, 1, 2, 3, 4, 5),
			new SolidSteelPattern(Pattern.compile(".+(\\d\\d\\d\\d.\\d\\d.\\d\\d)\\.0(\\d|\\d.) (.+)\\)(\\..+)"), //3
					MusicBase.TRUE_DATE_FORMAT, 1, 3, 2, 0, 4),
			new SolidSteelPattern(Pattern.compile(".+ (\\d+.\\d+.\\d\\d\\d\\d).Part.(\\d)...(\\d) - (.+)(\\..+)"), //4
					SRC4_DATE_FORMAT, 1, 4, 2, 3, 5),
			new SolidSteelPattern(Pattern.compile(".+ (\\d\\d\\d\\d.\\d\\d.\\d\\d).+(\\d).(\\d). (.+)(\\..+)"), //5
					MusicBase.TRUE_DATE_FORMAT, 1, 4, 2, 3, 5),
			new SolidSteelPattern(Pattern.compile("(\\d\\d-...-\\d\\d).0(\\d) (.+).(\\..+)"), //6
					SRC6_DATE_FORMAT, 1, 3, 2, 0, 4),
			new SolidSteelPattern(Pattern.compile("0(\\d) - .+ - (\\d\\d.\\d\\d.\\d\\d) - (.+)(\\..+)"), //7
					SRC1_DATE_FORMAT, 2, 3, 1, 0, 4),
			new SolidSteelPattern(Pattern.compile("0(\\d)..+.-.(.+)..(\\d\\d-\\d\\d-\\d\\d).+(\\..+)"), //8
					SRC8_DATE_FORMAT, 3, 2, 1, 0, 4),
			new SolidSteelPattern(Pattern.compile("(\\d\\d.\\d\\d.\\d\\d) - (.+) \\(pt\\.(\\d).+(\\d)\\).+(\\..+)"), //9
					SRC1_DATE_FORMAT, 1, 2, 3, 4, 5),
			new SolidSteelPattern(Pattern.compile("(\\d\\d.\\d\\d.\\d\\d) - (.+)(\\..+)"), //10
					SRC1_DATE_FORMAT, 1, 2, 0, 0, 3),
			new SolidSteelPattern(Pattern.compile(".+(\\d\\d-\\d\\d-\\d\\d\\d\\d)-0(\\d) (.+).(\\..+)"), //11
					SRC11_DATE_FORMAT, 1, 3, 2, 0, 4),
			new SolidSteelPattern(Pattern.compile("0(\\d)..+.-.(.+)..(..._\\d\\d\\)-2cd[r]-\\d\\d\\d\\d)(.+)?(\\....)"), //12
					new SimpleDateFormat() {
						@Override
						public Date parse(String text) throws ParseException {
							Matcher matcher = SRC12_DATE_PATTERN.matcher(text);
							if (matcher.find()) {
								return SRC12_DATE_FORMAT.parse(matcher.group(2) + " " +
										matcher.group(1).replace("_", " "));
							}
							return null;
						}
					}, 3, 2, 1, 0, 5),
	};

	private static final Pattern SOLID_STEEL_FOLDER_PATTERN = Pattern.compile("\\d\\d.\\d\\d.\\d\\d - (.+)");

	public static void main(String[] args) {

		String basePath = new File(".").getAbsolutePath();

		if (args.length == 0) {
			LOGGER.warning("No basePath specified! Working with current.");
		}

		LOGGER.info("Current basePath: " + basePath);

		File baseDirectory = new File(basePath);
		if (!baseDirectory.isDirectory()) {
			LOGGER.warning("Base directory is not a directory! Quiting...");
			return;
		}

		scanDirectory(baseDirectory, null);
	}

	private static void scanDirectory(File baseDirectory, String name) {
		File[] subFiles = baseDirectory.listFiles();
		if (subFiles == null) {
			LOGGER.warning("Given directory is invalid! Nothing to check!");
			return;
		}

		for (File subFile : subFiles) {
			if(subFile.isDirectory()) {
				String ssName = null;

				Matcher matcher = SOLID_STEEL_FOLDER_PATTERN.matcher(subFile.getName());
				while (matcher.find()) {
					ssName = matcher.group(1);
				}

				scanDirectory(subFile, ssName);
				continue;
			}

			SolidSteelFileTagsBeagleBuddy fileTags = null;

			try {
				for (SolidSteelPattern pattern : SOLID_STEEL_PATTERNS) {
					Matcher matcher = pattern.getPattern().matcher(subFile.getName());
					while (matcher.find()) {
						Date date = pattern.getDateFormat().parse(matcher.group(pattern.getDate()));
						String part1 = pattern.getPart1() == 0 ? "" : matcher.group(pattern.getPart1());
						String part2 = pattern.getPart2() == 0 ? "" : matcher.group(pattern.getPart2());
						fileTags = new SolidSteelFileTagsBeagleBuddy(subFile.getAbsolutePath(), date,
								name == null ? matcher.group(pattern.getName()) : name, part1, part2,
								matcher.group(pattern.getExtension()
						));
					}
					if (fileTags != null) {
						break;
					}
				}
			} catch (ParseException e) {
				LOGGER.warning(e.getMessage());
				continue;
			}

			if (fileTags != null) {
				LOGGER.info(subFile.getName() + " -> " + fileTags.getNewSolidSteelFileName());
				fileTags.updateTags();
			}
		}
	}
}
