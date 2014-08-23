import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Main {
	private static final int LOCALITY = 9;
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://10.73.45.51/cache";
	static final String FILE_PATH = "/Users/yoon/Desktop/test.txt";

	// Database credentials
	static final String USER = "hogu";
	static final String PASS = "hogu";

	static final List<Integer> dummyList = new ArrayList<Integer>();
	static final HashMap<Integer, String> cache = new HashMap<Integer, String>();

	/*
	 * 
	 * TODO 1. 캐시가 없을때 2. 캐시가 있을때 (용량 무제한) 3. 캐쉬가 있을때 (용량 7% - 700개)
	 */
	public static void main(String[] args) throws IOException {
		createKeyValueStore();
		saveKeyValueStoreToFile();
		selectWithEvaluation();
	}// end main

	private static void saveKeyValueStoreToFile() throws FileNotFoundException,
			IOException {
		File file = new File(FILE_PATH);
		FileOutputStream fos = new FileOutputStream(file);
		DataOutputStream dos = new DataOutputStream(fos);

		for (Integer key : dummyList) {
			dos.writeBytes(key + "\n");
		}

		dos.close();
		fos.close();
	}

	private static void createKeyValueStore() {

		Random rand = new Random();
		int max = 9000;
		int min = 1000;

		for (int i = 1; i <= 1000; i++) {
			for (int j = 1; j <= LOCALITY; j++) {
				dummyList.add(i);
			}
		}

		for (int i = 1; i <= 1000; i++) {
			dummyList.add(rand.nextInt((max - min) + 1) + min);
		}

		Collections.shuffle(dummyList);
	}

	private static void selectWithEvaluation() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		BufferedReader bis = null;

		try {

			Class.forName("com.mysql.jdbc.Driver");

			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			String sql;

			File file = new File(FILE_PATH);
			FileReader fis = new FileReader(file);
			bis = new BufferedReader(fis);

			String readLine = null;
			int id = 0;

			long startTime = System.currentTimeMillis();
			
			/**********************************************/
			//1. No Cache
			/*
			while ((readLine = bis.readLine()) != null) {
				id = Integer.parseInt(readLine);
				sql = "SELECT k FROM ctest WHERE k = " + id;
				rs = stmt.executeQuery(sql);
			}
			*/
			/**********************************************/
			
			/**********************************************/
			//2. Cache With Infinity Memory
			while ((readLine = bis.readLine()) != null) {
				
				id = Integer.parseInt(readLine);
				
				if (cache.containsKey(id)) {
					cache.get(id);
				} else {
					sql = "SELECT v FROM ctest WHERE k = " + id;
					System.out.println("id: " + id);
					rs = stmt.executeQuery(sql);
					System.out.println("rs: " + rs.toString());
					
					rs.next();
					System.out.println("rs.getInt(1): " + rs.getString(1));
					cache.put(id, rs.getString(1));
				}
			}
			/**********************************************/
			
			
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println(totalTime);

		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
			}

			try {
				if (bis != null)
					bis.close();
			} catch (IOException e) {
			}

			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {}
			
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {}
		}
		System.out.println("Goodbye!");
	}
}
