package datapresent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataPartition {

	/**
	 * List<CheckinInfoToShow>
	 */
	public static double scale = 0.5;

	public static int MapRow = (int) (180 / scale);

	public static int MapCol = (int) (360 / scale);

	public static List[][] DATAMAP = new List[MapRow][MapCol];

	static {
		for (int i = 0; i < MapRow; i++) {
			for (int j = 0; j < MapCol; j++) {
				DATAMAP[i][j] = new ArrayList<CheckinInfoToShow>();
			}
		}
	}

	public static void partitionData(String inputfile) {
		BufferedReader reader = null;
		int total = 0;
		int count = 0;
		try {
			reader = new BufferedReader(new FileReader(new File(inputfile)));
			String data = "";
			while ((data = reader.readLine()) != null) {
				total++;
				String[] datas = data.split(",");
				if (datas.length != 6) {
					continue;
				}
				CheckinInfoToShow node = new CheckinInfoToShow();
				node.latitude = Double.parseDouble(datas[0]);
				node.longitude = Double.parseDouble(datas[1]);
				node.url = datas[2];
				node.name = datas[3];
				node.checkinCount = Integer.parseInt(datas[4]);
				node.follower = Integer.parseInt(datas[5]);
				int[] mapindex = calculateMapIndex(node.latitude,
						node.longitude);
				if (mapindex[0] < MapRow && mapindex[1] < MapCol) {
					DATAMAP[mapindex[0]][mapindex[1]].add(node);
					count++;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("total passing data:" + total);
			System.out.println("valid data:" + count);
		}
	}

	public static List<CheckinInfoToShow> queryCheckinfo(double la1,
			double la2, double lo1, double lo2) {

		int[] begin = calculateMapIndex(la1, lo1);
		int[] end = calculateMapIndex(la2, lo2);

		List<int[]> rowrange = new ArrayList<int[]>();
		List<int[]> colrange = new ArrayList<int[]>();
		if (begin[0] <= end[0]) {
			rowrange.add(new int[] { begin[0], end[0] });
		} else {
			rowrange.add(new int[] { 0, begin[0] });
			rowrange.add(new int[] { end[0], MapRow - 1 });
		}
		if (begin[1] <= end[1]) {
			colrange.add(new int[] { begin[1], end[1] });
		} else {
			colrange.add(new int[] { 0, begin[1] });
			colrange.add(new int[] { end[1], MapCol - 1 });
		}
		List<CheckinInfoToShow> result = new ArrayList<CheckinInfoToShow>();

		for (int[] row : rowrange) {
			for (int i = row[0]; i <= row[1]; i++) {
				for (int[] col : colrange) {
					for (int j = col[0]; j <= col[1]; j++) {
						result.addAll(DATAMAP[i][j]);
					}
				}
			}
		}

		Collections.sort(result);
		return result;
	}

	public static void sortingData() {
		for (int i = 0; i < MapRow; i++) {
			for (int j = 0; j < MapCol; j++) {
				Collections.sort(DATAMAP[i][j]);
			}
		}
	}

	public static int[] calculateMapIndex(double latitude, double longitude) {
		double la = latitude + 90;
		double longi = longitude + 180;
		int laIndex = (int) Math.floor(la / scale);
		int logiIndex = (int) Math.floor(longi / scale);
		return new int[] { laIndex, logiIndex };
	}

	public static void printDataSize() {
		int count = 0;
		for (int i = 0; i < MapRow; i++) {
			for (int j = 0; j < MapCol; j++) {
				count += DATAMAP[i][j].size();
				System.out.println("(" + i + "," + j + "), "
						+ DATAMAP[i][j].size());
			}
		}
		System.out.println("total size of DATAMAP :" + count);
	}
	
	public static List<CheckinInfoToShow> defaultQuery(int numperaera) {
		List<CheckinInfoToShow> result = new ArrayList<CheckinInfoToShow>();
		for (int i = 0; i < MapRow; i++) {
			for (int j = 0; j < MapCol; j++) {
				List templ = DATAMAP[i][j];
				Collections.sort(templ);
				for (int k = 0; k < numperaera && k < templ.size(); k++) {
					result.add((CheckinInfoToShow)templ.get(k));
				}
			}
		}
		return result;
	}

	public static void main(String[] args) {
		String file = "/Users/sl/Downloads/icwsm_2011/checkin-for-analyze.txt";
		partitionData(file);
		sortingData();
		//printDataSize();
		// 28.380786,-81.409222 39.568357,2.644309
		List l = queryCheckinfo(28.380786, 39.568357, -81.409222, 2.644309);
		System.out.println("query size:" + l.size());
	}
}
