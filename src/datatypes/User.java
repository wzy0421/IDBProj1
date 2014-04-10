package datatypes;

import services.FacebookConnection;

public class User {
	private String facebookId;
	private String name;
	private String accessToken;

	public User(String fid, String accesstoken) {
		facebookId = fid;
		accessToken = accesstoken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This method gets the user info from Facebook
	 * 
	 * @throws Exception
	 */
	public void login() {

		FacebookConnection fbConn = new FacebookConnection();
		fbConn.getUserInfo(this);
	}

}
