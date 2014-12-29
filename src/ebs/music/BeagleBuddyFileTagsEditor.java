package ebs.music;

import com.beaglebuddy.mp3.MP3;
import com.beaglebuddy.mp3.enums.Genre;
import com.mpatric.mp3agic.Mp3File;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Aleksey Dubov
 * Date: 14/02/20
 * Time: 14:26
 * Copyright (c) 2014
 */
public class BeagleBuddyFileTagsEditor extends FileTagsEditor {
	private MP3 file;

	protected BeagleBuddyFileTagsEditor(TagsData tagsData, String fileName, MusicFileData musicFileData) {
		super(tagsData, fileName, musicFileData);

		try {
			file = new MP3(fileName);
		} catch (Exception e) {
			file = null;
		}
	}

	@Override
	public void updateTags(boolean renameFile) {
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

			String part1 = musicFileData.getPart1();
			String part2 = musicFileData.getPart2();
			Date date = musicFileData.getDate();

			Integer part = 1;
			if(part1 != null && !part1.isEmpty()) {
				if (part1.length() == 1) {
					part = Integer.parseInt(part1);
				} else {
					part = Integer.parseInt(part1.substring(0, 1));
				}
			}

			if(part2 != null && !part2.isEmpty()) {
				if (part2.length() == 1) {
					part = Integer.parseInt(part2);
				} else {
					part = Integer.parseInt(part2.substring(0, 1));
				}
			}

			String parts = MusicBase.getParts(part1, part2);

			file.setTrack(part);
			file.setBand(tagsData.getBand());
			file.setTitle(musicFileData.getName() + " - " +
					MusicBase.TRUE_DATE_FORMAT.format(date) +
					(parts.length() > 0 ? " - " + parts : ""));
			file.setAlbum(MusicBase.TRUE_DATE_FORMAT.format(date));

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			file.setYear(calendar.get(Calendar.YEAR));
			file.setMusicType(Genre.valueOf(tagsData.getGenre().toUpperCase()));

			file.save();

			fileNewName = getNewFileName();
			System.out.println("->" + fileNewName);

			if (renameFile && !new File(fileName).renameTo(new File(fileNewName))) {
				System.out.println("Cannot rename file: " + fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(fileName + " is not readable!");
		}
	}

	@Override
	public String getNewFileName() {
		String parts = MusicBase.getParts(musicFileData.getPart1(), musicFileData.getPart2());
		return tagsData.getBand() + " - " +
				MusicBase.TRUE_DATE_FORMAT.format(musicFileData.getDate()) + " - " +
				musicFileData.getName() +
				(parts.length() > 0 ? (" [" + parts + "]") : "") +
				" [" + (file == null ? "" : getBitrate(fileName)) + "]" +
				musicFileData.getExtension();
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
}
