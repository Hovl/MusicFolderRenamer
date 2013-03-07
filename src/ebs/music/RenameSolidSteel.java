package ebs.music;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Aleksey Dubov
 * Date: 6/17/12
 * Time: 4:30 PM
 * Copyright (c) 2012
 */
public class RenameSolidSteel {
	private static final SimpleDateFormat SRC1_DATE_FORMAT = new SimpleDateFormat("dd.mm.yy");
	private static final SimpleDateFormat SRC4_DATE_FORMAT = new SimpleDateFormat("dd_mm_yyyy");
	private static final SimpleDateFormat TRUE_DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd");

	private static final Pattern PATTERN_1 = Pattern.compile("(\\d\\d.\\d\\d.\\d\\d) - (.+) \\(pt\\.(\\d)\\)(\\..+)");
	private static final Pattern PATTERN_2 =
			Pattern.compile("(\\d\\d.\\d\\d.\\d\\d) - (.+) \\(pt\\.(\\d).+(\\d)\\)(\\..+)");
	private static final Pattern PATTERN_3 =
			Pattern.compile(".+(\\d\\d\\d\\d.\\d\\d.\\d\\d)\\.0(\\d|\\d.) (.+)\\)(\\..+)");
	private static final Pattern PATTERN_4 =
			Pattern.compile(".+ (\\d+.\\d+.\\d\\d\\d\\d).Part.(\\d)...(\\d) - (.+)(\\..+)");
	private static final Pattern PATTERN_5 =
			Pattern.compile(".+ (\\d\\d\\d\\d.\\d\\d.\\d\\d).+(\\d).(\\d). (.+)(\\..+)");

	public static void main(String[] args) {
		String basePath = new File(".").getAbsolutePath();

		if (args.length == 0) {
			System.out.println("No basePath specified! Working with current.");
		}

		System.out.println("Current basePath: " + basePath);

		File baseDirectory = new File(basePath);
		if (!baseDirectory.isDirectory()) {
			System.out.println("Base directory is not a directory! Quiting...");
			return;
		}

		File[] subFiles = baseDirectory.listFiles();
		for (File subFile : subFiles) {
			String fileNewName = "";

			try {
				Matcher matcher = PATTERN_1.matcher(subFile.getName());
				while (matcher.find()) {
					Date date = SRC1_DATE_FORMAT.parse(matcher.group(1));
					fileNewName = compileNewName(date, matcher.group(2), matcher.group(3), matcher.group(4));
				}

				if (fileNewName.isEmpty()) {
					matcher = PATTERN_2.matcher(subFile.getName());
					while (matcher.find()) {
						Date date = SRC1_DATE_FORMAT.parse(matcher.group(1));
						fileNewName = compileNewName(date, matcher.group(2), matcher.group(3),
								matcher.group(4), matcher.group(5));
					}
				}

				if (fileNewName.isEmpty()) {
					matcher = PATTERN_3.matcher(subFile.getName());
					while (matcher.find()) {
						Date date = TRUE_DATE_FORMAT.parse(matcher.group(1));
						fileNewName = compileNewName(date, matcher.group(3), matcher.group(2), matcher.group(4));
					}
				}

				if (fileNewName.isEmpty()) {
					matcher = PATTERN_4.matcher(subFile.getName());
					while (matcher.find()) {
						Date date = SRC4_DATE_FORMAT.parse(matcher.group(1));
						fileNewName = compileNewName(date, matcher.group(4), matcher.group(2), matcher.group(3),
								matcher.group(5));
					}
				}

				if (fileNewName.isEmpty()) {
					matcher = PATTERN_5.matcher(subFile.getName());
					while (matcher.find()) {
						Date date = TRUE_DATE_FORMAT.parse(matcher.group(1));
						fileNewName = compileNewName(date, matcher.group(4), matcher.group(2), matcher.group(3),
								matcher.group(5));
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
				continue;
			}

			if (!fileNewName.isEmpty()) {
				System.out.println("New Name: " + fileNewName);

				System.out.println("Renamed: " + subFile.renameTo(new File(basePath, fileNewName)));
			}
		}
	}

	private static String compileNewName(Date date, String name, String part1, String extension) {
		return compileNewName(date, name, part1, "", extension);
	}

	private static String compileNewName(Date date, String name, String part1, String part2, String extension) {
		return "Solid Steel - " + TRUE_DATE_FORMAT.format(date) + " - " + name + " [part " + part1 + (part2.isEmpty()
				? "" : " & " + part2) + "] []" + extension;
	}
}