package ebs.music;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.Mp3File;
import ebs.music.MusicBase;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Aleksey Dubov
 * Date: 3/7/13
 * Time: 10:52 PM
 * Copyright (c) 2013
 */
public class SolidSteelFileTagsMp3agic {
	private String fileName;
	private Mp3File file;

	private Date date;
	private String name;
	private String part1;
	private String part2;
	private String extension;

	public SolidSteelFileTagsMp3agic(String fileName) {
		this.fileName = fileName;
		System.out.println(fileName);

		try {
			file = new Mp3File(fileName);
		} catch (Exception e) {
			file = null;
		}
	}

	public SolidSteelFileTagsMp3agic(String fileName, Date date, String name, String part1, String part2,
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
			ID3v2 id3;

			if (file.hasId3v2Tag()) {
				id3 = file.getId3v2Tag();
			} else {
				id3 = new ID3v24Tag();
				file.setId3v2Tag(id3);
			}

			String comment = id3.getComment();
			String artist = id3.getArtist();
			String title = id3.getTitle();
			if (comment != null || artist != null || title != null) {
				id3.setComment((comment == null ? "" : comment) + "\n" + (artist == null ? "" : artist) + " - " +
						(title == null ? "" : title));
			}

			id3.setTrack(part2 == null || part2.isEmpty() ? part1 : (Integer.parseInt(part1) == 1 ? part1 : "2"));
			id3.setArtist("Solid Steel");
			id3.setTitle(name + " - " + MusicBase.TRUE_DATE_FORMAT.format(date) + " - " + MusicBase
					.getParts(part1, part2));
			id3.setAlbum(MusicBase.TRUE_DATE_FORMAT.format(date));

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			id3.setYear(Integer.toString(calendar.get(Calendar.YEAR)));
			id3.setGenre(52);

			fileNewName = getNewSolidSteelFileName();
			System.out.println("->" + fileNewName);

			file.save(fileNewName);

			if(!new File(fileName).delete()) {
				System.out.println("Cannot delete original file: " + fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(fileName + " is not readable!");
		}
	}

	private String getBitrate(Mp3File file) {
		if (file.isVbr()) {
			return "VBR";
		}

		return Integer.toString(file.getBitrate());
	}

	public String getNewSolidSteelFileName() {
		String bitrate = file == null ? "" : getBitrate(file);

		return "Solid Steel - " +
				MusicBase.TRUE_DATE_FORMAT.format(date) + " - " +
				name +
				" [" + MusicBase.getParts(part1, part2) + "]" +
				" [" + bitrate + "]" +
				extension;
	}
}
