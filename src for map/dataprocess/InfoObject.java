package dataprocess;

import java.util.ArrayList;
import java.util.List;

public class InfoObject {
	int count;
	
	List<Information> infolist = new ArrayList<>();
	
	public void addInfo(Information info) {
		infolist.add(info);
		count++;
	}
}
