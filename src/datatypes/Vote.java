package datatypes;

public class Vote {
	
	private String eventId;
	private String timeId;
	private String locationId;
	private String userId;
	
	public void setEventId(String eid) {
		this.eventId = eid;
	}
	
	public void setTimeId(String tid) {
		this.timeId = tid;
	}
	
	public void setLocationId(String lid) {
		this.locationId = lid;
	}

	public void setUserId(String uid) {
		this.userId = uid;
	}
	
	public String getUserId() {
		return this.userId;
	}
	
	public String getEventId() {
		return this.eventId;
	}
	
	public String getTimeId() {
		return this.timeId;
	}
	
	public String getLocationId() {
		return this.locationId;
	}
}