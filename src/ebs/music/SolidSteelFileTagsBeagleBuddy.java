package ebs.music;

import com.beaglebuddy.mp3.MP3;
import com.beaglebuddy.mp3.enums.Genre;
import com.mpatric.mp3agic.Mp3File;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Aleksey Dubov
 * Date: 3/7/13
 * Time: 10:52 PM
 * Copyright (c) 2013
 */
public class SolidSteelFileTagsBeagleBuddy {
	private String fileName;
	private MP3 file;

	private Date date;
	private String name;
	private String part1;
	private String part2;
	private String extension;

	public SolidSteelFileTagsBeagleBuddy(String fileName) {
		this.fileName = fileName;
		System.out.println(fileName);

		try {
			file = new MP3(fileName);
		} catch (Exception e) {
			file = null;
		}
	}

	public SolidSteelFileTagsBeagleBuddy(String fileName, Date date, String name, String part1, String part2,
										 String extension) {
		this(fileName);

		this.date = date;
		this.name = name;
		this.part1 = part1;
		this.part2 = part2;
		this.extension = extension;
	}

	public void updateTags() {
		String fileNewName;

		try {
			if (file.hasErrors()) {
				file.displayErrors(System.out);
				file.save();
			}

			String comment = file.getComments();
			String artist = file.getBand();
			String title = file.getTitle();
			if (comment != null || artist != null || title != null) {
				file.setComments((comment == null ? "" : comment) + "\n" + (artist == null ? "" : artist) + " - " +
						(title == null ? "" : title));
			}

			file.setTrack(part2 == null || part2.isEmpty() ? Integer.parseInt(part1.substring(0, 1)) :
					(Integer.parseInt(part1.substring(0, 1)) == 1 ? Integer.parseInt(part1.substring(0, 1)) : 2));
			file.setBand("Solid Steel");
			file.setTitle(name + " - " + MusicBase.TRUE_DATE_FORMAT.format(date) + " - " + MusicBase
					.getSolidSteelParts(part1, part2));
			file.setAlbum(MusicBase.TRUE_DATE_FORMAT.format(date));

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			file.setYear(calendar.get(Calendar.YEAR));
			file.setMusicType(Genre.ELECTRONIC);

			file.save();

			fileNewName = getNewSolidSteelFileName();
			System.out.println("->" + fileNewName);
			if (!new File(fileName).renameTo(new File(fileNewName))) {
				System.out.println("Cannot rename file: " + fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(fileName + " is not readable!");
		}
	}

	private String getBitrate(String fileName) {
		Mp3File mp3File;
		try {
			mp3File = new Mp3File(fileName);
		} catch (Exception e) {
			return "";
		}

		if (mp3File.isVbr()) {
			return "VBR";
		}

		return Integer.toString(mp3File.getBitrate());
	}

	public String getNewSolidSteelFileName() {
		String bitrate = file == null ? "" : getBitrate(fileName);

		return "Solid Steel - " +
				MusicBase.TRUE_DATE_FORMAT.format(date) + " - " +
				name +
				" [" + MusicBase.getSolidSteelParts(part1, part2) + "]" +
				" [" + bitrate + "]" +
				extension;
	}
}
