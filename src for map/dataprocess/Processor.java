package dataprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Processor {

	/**
	 * "/Users/sl/Downloads/icwsm_2011/checkin_4sq_format.txt"
	 * 
	 * @param file1
	 * @param file2
	 * @param outfile
	 */
	public static void joinCheckinUserData(String file1, String file2,
			String outfile) {
		BufferedReader r1 = null;
		BufferedReader r2 = null;
		BufferedWriter writer = null;
		try {
			r1 = new BufferedReader(new FileReader(new File(file1)));
			r2 = new BufferedReader(new FileReader(new File(file2)));
			writer = new BufferedWriter(new FileWriter(outfile, false));
			String userstr = "";
			Map<String, Integer> userInfo = new HashMap<>();
			while ((userstr = r2.readLine()) != null) {
				String[] user = userstr.split("\\s+");
				userInfo.put(user[0], Integer.parseInt(user[2]));
			}
			r2.close();

			String checkinstr = "";

			while ((checkinstr = r1.readLine()) != null) {
				String uid = checkinstr.substring(0, checkinstr.indexOf(","));
				writer.append(checkinstr.substring(0, checkinstr.length()-1)
						+ ","
						+ (userInfo.get(uid) != null ? userInfo.get(uid)
								.intValue() : 0));
				writer.newLine();
			}
			writer.flush();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void mergeCheckinData(String file, String outfile) {
		BufferedReader r1 = null;
		BufferedWriter writer = null;
		try {
			r1 = new BufferedReader(new FileReader(new File(file)));
			writer = new BufferedWriter(new FileWriter(outfile, false));
			String userstr = "";
			Map<String, Integer> userInfo = new HashMap<>();
			String checkinstr = "";
			Map<String, List<CheckinUser>> checkMap = new HashMap<String, List<CheckinUser>>();

			while ((checkinstr = r1.readLine()) != null) {
				String[] checkinfo = checkinstr.split(",");
				int urlpos = 0;
				String name = "";
				if (checkinfo.length == 7 && (urlpos = checkinfo[5].indexOf("http://4sq")) >= 0) {
					String url = checkinfo[5].substring(urlpos);
					if (urlpos >= 7) {
						name = checkinfo[5].substring(7, urlpos);
					}
					CheckinUser cu = new CheckinUser(checkinfo[0],
							checkinfo[1], name, url, checkinfo[6],
							checkinfo[4], Double.parseDouble(checkinfo[2]),
							Double.parseDouble(checkinfo[3]), 0, 1);
					if (checkMap.containsKey(cu.url)) {
						checkMap.get(cu.url).add(cu);
					} else {
						List<CheckinUser> clist = new ArrayList<CheckinUser>();
						clist.add(cu);
						checkMap.put(cu.url, clist);
					}
				}
			}

			Set<String> keys = checkMap.keySet();
			for (String key : keys) {
				List<CheckinUser> cul = checkMap.get(key);
				if (cul != null && cul.size() > 0) {
					CheckinUser cu = cul.get(0);
					cu.checkinCount = cul.size();
					writer.append(cu.toString());
					writer.newLine();
				}
			}
			writer.flush();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (r1 != null) {
					r1.close();
				}
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void mergeCheckinData2(String file, String outfile) {
		BufferedReader r1 = null;
		BufferedWriter writer = null;
		try {
			r1 = new BufferedReader(new FileReader(new File(file)));
			writer = new BufferedWriter(new FileWriter(outfile, false));
			String userstr = "";
			Map<String, Integer> userInfo = new HashMap<>();
			String checkinstr = "";
			Map<String, CheckinUser> checkMap = new HashMap<>();

			while ((checkinstr = r1.readLine()) != null) {
				String[] checkinfo = checkinstr.split(",");
				int urlpos = 0;
				String name = "";
				if (checkinfo.length == 8 && (urlpos = checkinfo[5].indexOf("http://4sq")) >= 0) {
					String url = checkinfo[5].substring(urlpos);
					if (urlpos >= 7) {
						name = checkinfo[5].substring(7, urlpos);
					}
					CheckinUser cu = new CheckinUser(checkinfo[0],
							checkinfo[1], name, url, checkinfo[6],
							checkinfo[4], Double.parseDouble(checkinfo[2]),
							Double.parseDouble(checkinfo[3]), Integer.parseInt(checkinfo[7]), 1);
					if (checkMap.containsKey(cu.url)) {
						CheckinUser original = checkMap.get(cu.url);
						original.follower += cu.follower;
						original.checkinCount += cu.checkinCount;
					} else {
						checkMap.put(cu.url, cu);
					}
				}
			}

			Set<String> keys = checkMap.keySet();
			for (String key : keys) {
				CheckinUser cu = checkMap.get(key);
				writer.append(cu.toCheckinMergeString());
				writer.newLine();
			}
			writer.flush();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (r1 != null) {
					r1.close();
				}
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static int calculateRowCount(String file) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(new File(file)));
		int count = 0;
		while (r.readLine() != null) {
			count++;
		}
		return count;
	}

	public static void main(String[] args) throws IOException {
		String file = "/Users/sl/Downloads/icwsm_2011/checkin_4sq_format.txt";
		String mergeout = "/Users/sl/Downloads/icwsm_2011/checkin_4sq_merge.txt";
		String jfile1 = mergeout;
		String jfile2 = "/Users/sl/Downloads/icwsm_2011/users_data.txt";
		String jout = "/Users/sl/Downloads/icwsm_2011/checkin_user_join.txt";
		Processor p = new Processor();
		//p.mergeCheckinData(file, mergeout);
		p.joinCheckinUserData(file, jfile2, jout);
		p.mergeCheckinData2(jout, mergeout);
		
		int mergcount = p.calculateRowCount(mergeout);
		int joincount = p.calculateRowCount(jout);
		System.out.println("merge count:"+mergcount);
		System.out.println("join count:"+joincount);

	}
}
