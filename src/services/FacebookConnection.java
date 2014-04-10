package services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import datatypes.User;

public class FacebookConnection {

	private static final String fbGraphURL = "https://graph.facebook.com/me";

	public void getUserInfo(User user) {
		String accessToken = user.getAccessToken();
		String facebookId = user.getFacebookId();
		String query = fbGraphURL + "?access_token=" + accessToken;
		String response = this.getResponse(query);
		JSONObject data = null;

		try {
			data = new JSONObject(response);
			user.setFacebookId(facebookId);
			user.setName(data.get("name").toString());

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	String getResponse(String query) {
		String resp = "";

		try {
			String fbStringToSend = query;
			System.out.println("TEST con: " + fbStringToSend);
			URL fbURL = new URL(fbStringToSend);
			try {
				URLConnection urlConnect = fbURL.openConnection();
				HttpURLConnection httpCon = (HttpURLConnection) urlConnect;

				resp = String.valueOf(httpCon.getResponseCode());
				if (httpCon.getResponseCode() == 400) {
					System.out.println("FB through 400 error.");
				} else {
					BufferedReader in = new BufferedReader(
							new InputStreamReader(httpCon.getInputStream()));
					String inputLine, output = "";
					while ((inputLine = in.readLine()) != null) {
						output += inputLine;
					}
					in.close();
					System.out.println("Connection response: " + output);
					resp = output;
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return resp;
	}
}
