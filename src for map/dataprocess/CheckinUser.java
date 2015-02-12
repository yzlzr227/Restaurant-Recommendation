package dataprocess;

public class CheckinUser implements Comparable<CheckinUser>{
	private static String seperator = ",";

	public String userId;

	public String tweetId;

	public String placeName;

	public String url;

	public String other;

	public String date;

	public double latitude;

	public double longitude;

	public int follower;

	public int checkinCount;

	
	public CheckinUser(String userId, String tweetId, String placeName,
			String url, String other, String date, double latitude,
			double longitude, int follower, int checkin) {
		super();
		this.userId = userId;
		this.tweetId = tweetId;
		this.placeName = placeName;
		this.url = url;
		this.other = other;
		this.date = date;
		this.latitude = latitude;
		this.longitude = longitude;
		this.follower = follower;
		this.checkinCount = checkin;
	}

	@Override
	public String toString() {

		return userId + seperator + tweetId + seperator + latitude + seperator
				+ longitude + seperator + url + seperator
				+ placeName.replace(seperator, "")
				+ seperator + checkinCount + (follower > 0 ? seperator+follower : "-");

	}

	public String toCheckinMergeString() {
		return latitude + seperator
				+ longitude + seperator + url + seperator
				+ placeName.replace(seperator, "")
				+ seperator + checkinCount + seperator+follower;
	}
	
	@Override
	public int compareTo(CheckinUser o) {
//		if (o.url.compareTo(url) == 0) {
//			
//		}
		if (follower > o.follower) {
			return 1;
		} else if (follower == o.follower) {
			return checkinCount - o.checkinCount;
		} else {
			return -1;
		}
	}
	
	
	@Override
	public int hashCode() {
		return url.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		CheckinUser cu = (CheckinUser)obj;
		return url.equals(cu.url);
	}

	
}
