package ebs.music;

/**
 * Created by Aleksey Dubov
 * Date: 14/02/20
 * Time: 13:51
 * Copyright (c) 2014
 */
public abstract class FileTagsEditor {
	protected TagsData tagsData;
	protected String fileName;
	protected MusicFileData musicFileData;

	protected FileTagsEditor(TagsData tagsData, String fileName, MusicFileData musicFileData) {
		this.tagsData = tagsData;
		this.fileName = fileName;
		this.musicFileData = musicFileData;
	}

	public TagsData getTagsData() {
		return tagsData;
	}

	public String getFileName() {
		return fileName;
	}

	public MusicFileData getMusicFileData() {
		return musicFileData;
	}

	public abstract void updateTags(boolean renameFile);

	public abstract String getNewFileName();
}
