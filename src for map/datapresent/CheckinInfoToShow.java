package datapresent;


public class CheckinInfoToShow implements Comparable<CheckinInfoToShow>{
	public double latitude;
	
	public double longitude;
	
	public int follower;
	
	public int checkinCount;
	
	public String name;
	
	public String url;
	
	@Override
	public int compareTo(CheckinInfoToShow o) {
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
		CheckinInfoToShow cu = (CheckinInfoToShow)obj;
		return url.equals(cu.url);
	}
}
