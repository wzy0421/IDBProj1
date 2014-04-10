package datatypes;

public class Invitation {
	
	private String eventId;
	private String userId;			// person to receive the invitation
	
	public void setEventId(String eid) {
		this.eventId = eid;
	}
	
	public void setInviteeId(String uid) {
		this.userId = uid;
	}
	
	public String getEventId() {
		return this.eventId;
	}
	
	public String getInviteeId() {
		return this.userId;
	}
}