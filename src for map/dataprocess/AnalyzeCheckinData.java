package dataprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AnalyzeCheckinData {

	public AnalyzeCheckinData() {
		this.data = new InfoObject[180][360];
		for (int i = 0; i < 180; i++) {
			for (int j = 0; j < 360; j++) {
				data[i][j] = new InfoObject();
			}
		}
	}

	public long[] countRelatedData(String file) throws IOException {
		long[] result = new long[2];
		BufferedReader reader = new BufferedReader(new FileReader(
				new File(file)));
		String line = "";
		while ((line = reader.readLine()) != null) {
			if (line.indexOf("http://4sq.com") >= 0) {
				result[0]++;
				if (line.indexOf("I'm at") >= 0) {
					result[1]++;
				}
			}
		}
		return result;
	}

	public void analyzeInfoObjectByArea(String file)
			throws NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(
				new File(file)));
		String line = "";
		long count = 0;
		while ((line = reader.readLine()) != null) {
			try {
				String[] info = line.split("\\s+");
				double latitude = Double.parseDouble(info[2]);
				double longitude = Double.parseDouble(info[3]);
				int row = (int) Math.floor(latitude + 90);
				int clown = (int) Math.floor(longitude + 180);
				data[row][clown].addInfo(new Information(latitude, longitude,
						""));
			} catch (Exception e) {
				e.printStackTrace();
			}
			count++;
			if (count > 10000) {
				System.out.println("parsing count:" + count);
			}
		}
		System.out.println("parsing count:" + count);
	}

	public InfoObject[][] data;

	public void filterData(String file) {
		BufferedReader reader = null;
		BufferedWriter writer = null;
		int passcount = 0;
		int processcount = 0;
		try {
			reader = new BufferedReader(new FileReader(new File(file)));
			writer = new BufferedWriter(new FileWriter("/Users/sl/Downloads/icwsm_2011/checkin_4sq_format.txt", false));
			String line = "";
			while ((line = reader.readLine()) != null) {
				if (line.indexOf("http://4sq.com") >= 0) {
					String[] datas = line.split("\t");
					if (datas.length != 7) {
						passcount++;
						continue;
					}
//					if (line.indexOf("I'm at") >= 0) {
					if (datas[5] != null) {
						datas[5].replace("I'm at ", "");
					}
					StringBuilder l = new StringBuilder();
					for (String s : datas) {
						l.append(s.replace(',', ' ')+",");
					}
					l.setLength(l.length()-3);
					writer.write(l.toString());
					writer.newLine();
					processcount++;
//					}
				}
			}
			writer.flush();
			System.out.println("passed data:"+passcount);
			System.out.println("processed count:"+processcount);
			
		} catch (Exception e) {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			if (null != writer) {
				try {
					writer.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) throws IOException {
		AnalyzeCheckinData acd = new AnalyzeCheckinData();
		String file = "/Users/sl/Downloads/icwsm_2011/checkin_4sq.txt";
		long[] r = acd.countRelatedData(file);
		System.out.println("4q count:" + r[0]);
		System.out.println("4q with at:" + r[1]);
//		acd.analyzeInfoObjectByArea(file);
		acd.filterData(file);
	}

}
