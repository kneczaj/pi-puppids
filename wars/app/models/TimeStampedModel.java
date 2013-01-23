package models;

public class TimeStampedModel extends BaseModel{
	
	private long creationTime;
	
	public TimeStampedModel() {
		creationTime = System.currentTimeMillis();
	}
	
	public long getCreationDate() {
		return creationTime;
	}
}
