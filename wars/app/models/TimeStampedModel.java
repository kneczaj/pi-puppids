package models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeStampedModel extends BaseModel{
	
	private long creationTime;
	
	public TimeStampedModel() {
		creationTime = System.currentTimeMillis();
	}
	
	public long getCreationDate() {
		return creationTime;
	}
	
	public String getCreationDateTimeString() {
		Date date = new Date(this.getCreationDate());
		
		return (new SimpleDateFormat("dd.MM.yyyy HH:mm")).format(date);
	}
}
