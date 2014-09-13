package ebs.music;

import java.io.File;

/**
 * Created by Aleksey Dubov.
 * Date: 10/26/11
 * Time: 7:44 PM
 * Copyright (c) 2011
 */
public class RenameYear {
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
		if(subFiles == null) {
			System.out.println(basePath + " is not a directory! Quiting...");
			return;
		}

		for (File subFile : subFiles) {
			System.out.println("File to rename: " + subFile.getName());

			String fileNameWithoutExtension;
			if(subFile.isDirectory()) {
				fileNameWithoutExtension = subFile.getName();
			} else {
				int extPos = subFile.getName().lastIndexOf(".");
				fileNameWithoutExtension =
						extPos > 0 ? subFile.getName().substring(0, extPos) : subFile.getName();
			}
			System.out.println("File name without extension: " + fileNameWithoutExtension);

			int qStartPos = fileNameWithoutExtension.lastIndexOf(" [");
			if (qStartPos > 0) {
				String quality = subFile.getName().substring(qStartPos, subFile.getName().length());
				System.out.println("Quality: " + quality);

				String fileNameWithoutExtensionAndQuality = fileNameWithoutExtension.substring(0, qStartPos);
				System.out.println(" File name without extension and quality: " + fileNameWithoutExtensionAndQuality);

				int yearPos = fileNameWithoutExtensionAndQuality.lastIndexOf(" [");
				if (yearPos > 0) {
					String year = fileNameWithoutExtensionAndQuality.substring(yearPos + 2,
							fileNameWithoutExtensionAndQuality.length() - 1);
					System.out.println("Year: " + year);

					String fileNameWithoutExtAndQualityAndYear = fileNameWithoutExtensionAndQuality.substring(0,
							yearPos);
					System.out.println("File name without extension, quality and year: " + fileNameWithoutExtAndQualityAndYear);

					String fileNewName =
							fileNameWithoutExtAndQualityAndYear.replaceFirst(" - ", " - " + year + " - ") + quality;
					System.out.println("New Name: " + fileNewName);

					System.out.println("Renamed: " + subFile.renameTo(new File(basePath, fileNewName)));
				}
			}
		}
	}
}
