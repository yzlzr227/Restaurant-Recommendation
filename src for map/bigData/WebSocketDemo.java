package bigData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import datapresent.CheckinInfoToShow;
import datapresent.DataPartition;

@ServerEndpoint("/echo")
public class WebSocketDemo {
	private double earth_radius = 3960.0;
	private double degrees_to_radians = Math.PI / 180.0;
	private double radians_to_degrees = 180.0 / Math.PI;

	@OnMessage
	public void echoTextMessage(Session session, String msg, boolean last) {
		try {
			if (session.isOpen()) {
				System.out.println(msg);
				if (msg.equalsIgnoreCase("first")) {
					msg = initData();
				} else {
					String[] res = msg.split(" ");
					double lat = Double.valueOf(res[0]);
					double lng = Double.valueOf(res[1]);
					int limit = Integer.valueOf(res[2]);
					// int miles = Integer.valueOf(res[2]);
					msg = getData(lat, lng, 1,limit);
				}
				session.getBasicRemote().sendText(msg, last);
			}
		} catch (IOException e) {
			try {
				session.close();
			} catch (IOException e1) {
				// Ignore
			}
		}
	}

	public String initData() {
		List<CheckinInfoToShow> result = DataPartition.defaultQuery(10);
		System.out.println("default query: "+result.size());
		return toJson(result,1000000);
	}

	public String getData(double latitude, double longitude, double miles,int limit) {
		double distanceLat = (miles / earth_radius) * radians_to_degrees;
		double r = earth_radius * Math.cos(latitude * degrees_to_radians);
		double distanceLng = (miles / r) * radians_to_degrees;
		double leftCornerLat = latitude - distanceLat;
		double leftCornerLng = longitude - distanceLng;
		double rightCornerLat = latitude + distanceLat;
		double rightCornerLng = longitude + distanceLng;

		System.out.println("inputLat: "+latitude+" inputLng: "+longitude);
		System.out.println("leftcornerLat: "+leftCornerLat+" leftCornerLng: "+leftCornerLng);
		System.out.println("rightCornerLat: "+rightCornerLat + " rightCornerLng: "+rightCornerLng);
		List<CheckinInfoToShow> result = DataPartition.queryCheckinfo(
				leftCornerLat, rightCornerLat, leftCornerLng, rightCornerLng);
		System.out.println("regular query: "+result.size());
		return toJson(result,limit);
	}

	public String toJson(List<CheckinInfoToShow> result,int limit) {
		JSONArray arr = new JSONArray();
		JSONObject tmpJson;
		int cnt = 0;
		try {
			for (CheckinInfoToShow check : result) {
				if(cnt++ > limit) break;
				tmpJson = new JSONObject();
				tmpJson.put("latitude", String.valueOf(check.latitude));
				tmpJson.put("longitude", String.valueOf(check.longitude));
				tmpJson.put("name", check.name);
				arr.put(tmpJson);
			}
			System.out.println(cnt);
		} catch (JSONException e) {
			// error handling
		}
		return arr.toString();
	}

}
