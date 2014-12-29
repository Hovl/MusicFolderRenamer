package ebs.music;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Aleksey Dubov
 * Date: 14/02/20
 * Time: 18:23
 * Copyright (c) 2014
 */
public class RenameBreezeblock {
	private static final Logger LOGGER = Logger.getLogger(RenameBreezeblock.class.getName());

	public static final MusicFileNamePattern[] BREEZEBLOCK_PATTERNS = new MusicFileNamePattern[]{
			new MusicFileNamePattern(Pattern.compile("(\\d\\d\\d\\d-\\d\\d-\\d\\d) .+ - (.+)(\\..+)"), //0
					MusicBase.TRUE_DATE_FORMAT, 1, 2, 0, 0, 3),
			new MusicFileNamePattern(Pattern.compile("0(\\d) - .+ - (.+)(\\..+)"), //1
					MusicBase.TRUE_DATE_FORMAT, 0, 2, 1, 0, 3),
			new MusicFileNamePattern(Pattern.compile("(\\d) - (.+)(\\..+)"), //2
					MusicBase.TRUE_DATE_FORMAT, 0, 2, 1, 0, 3),
			new MusicFileNamePattern(Pattern.compile("(\\d\\d\\d\\d-\\d\\d-\\d\\d) .+ - 0(\\d) - (.+)(\\..+)"), //3
					MusicBase.TRUE_DATE_FORMAT, 1, 3, 2, 0, 4),
			new MusicFileNamePattern(Pattern.compile("(\\d\\d\\d\\d-\\d\\d-\\d\\d) .+ - (.+) pt ?(\\d)(\\..+)"), //4
					MusicBase.TRUE_DATE_FORMAT, 1, 2, 3, 0, 4),
			new MusicFileNamePattern(Pattern.compile("(\\d\\d\\d\\d-\\d\\d-\\d\\d) .+ - (.+) - Part (\\d)(\\..+)"), //5
					MusicBase.TRUE_DATE_FORMAT, 1, 2, 3, 0, 4),
			new MusicFileNamePattern(Pattern.compile("(\\d\\d\\d\\d-\\d\\d-\\d\\d) .+ - (.+) Part (\\d)(\\..+)"), //6
					MusicBase.TRUE_DATE_FORMAT, 1, 2, 3, 0, 4),
			new MusicFileNamePattern(Pattern.compile("0(\\d) (.+)(\\..+)"), //7
					MusicBase.TRUE_DATE_FORMAT, 0, 2, 1, 0, 3),
			new MusicFileNamePattern(Pattern.compile("(\\d\\d\\d\\d-\\d\\d-\\d\\d) .+ - (.+) (\\d)(\\..+)"), //8
					MusicBase.TRUE_DATE_FORMAT, 1, 2, 3, 0, 4),
			new MusicFileNamePattern(Pattern.compile(".+ - 0?(\\d\\d?) - (.+)(\\..+)"), //9
					MusicBase.TRUE_DATE_FORMAT, 0, 2, 1, 0, 3),
			new MusicFileNamePattern(Pattern.compile(".+ - .0?(\\d\\d?). (.+)(\\..+)"), //10
					MusicBase.TRUE_DATE_FORMAT, 0, 2, 1, 0, 3),
	};

	public static final Pattern BREEZEBLOCK_FOLDER_PATTERN = Pattern.compile("(\\d\\d\\d\\d-\\d\\d-\\d\\d) .+ - (.+)");

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

		scanDirectory(baseDirectory, null, null);
	}

	private static void scanDirectory(File baseDirectory, Date bDate, String bName) {
		File[] subFiles = baseDirectory.listFiles();
		if (subFiles == null) {
			LOGGER.warning("Given directory is invalid! Nothing to check!");
			return;
		}

		for (File subFile : subFiles) {
			if (subFile.isDirectory()) {
				String ssDate = null;
				String ssName = null;

				Matcher matcher = BREEZEBLOCK_FOLDER_PATTERN.matcher(subFile.getName());
				while (matcher.find()) {
					ssDate = matcher.group(1);
					ssName = matcher.group(2);
				}

				try {
					scanDirectory(subFile, MusicBase.TRUE_DATE_FORMAT.parse(ssDate), ssName);
				} catch (ParseException e) {
					LOGGER.warning(e.getMessage());
				}
				continue;
			}

			TagsData tagsData = new TagsData("Breezeblock", MusicBase.TRUE_DATE_FORMAT, "Electronic");

			BeagleBuddyFileTagsEditor editor = null;


			for (MusicFileNamePattern pattern : BREEZEBLOCK_PATTERNS) {
				try {

					Matcher matcher = pattern.getPattern().matcher(subFile.getName());
					while (matcher.find()) {
						Date date = pattern.getDate() != 0 ?
								pattern.getDateFormat().parse(matcher.group(pattern.getDate())) : bDate;
						if (date == null) {
							continue;
						}
						String name = matcher.group(pattern.getName());
						String part1 = pattern.getPart1() == 0 ? "" : matcher.group(pattern.getPart1());
						String part2 = pattern.getPart2() == 0 ? "" : matcher.group(pattern.getPart2());

						MusicFileData fileData =
								new MusicFileData(
										date,
										bName == null ? name : (bName.equals(name) ? name : bName + " - " + name),
										part1,
										part2,
										matcher.group(pattern.getExtension()));
						editor = new BeagleBuddyFileTagsEditor(tagsData, subFile.getAbsolutePath(), fileData);
					}
					if (editor != null) {
						break;
					}
				} catch (ParseException e) {
					LOGGER.warning("File: " + subFile.getName() + " - " + e.getMessage());
				}
			}


			if (editor != null) {
				LOGGER.info(subFile.getName() + " -> " + editor.getNewFileName());
				editor.updateTags(true);
			}
		}
	}
}
