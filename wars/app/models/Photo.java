package models;

import com.google.code.morphia.annotations.Embedded;

@Embedded
public class Photo extends BaseModel{
	
	private byte[] thumbnail;
	
	public void setThumbnail(byte[] thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	public byte[] getThumbnail() {
		return thumbnail;
	}
}
