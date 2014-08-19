import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
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
			dos.writeBytes(key+"\n");
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
		try {
			Class.forName("com.mysql.jdbc.Driver");

			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT k FROM ctest ";
			ResultSet rs = stmt.executeQuery(sql);

			int key;
			while (rs.next()) {
				key = rs.getInt(1);
				System.out.println(key);
			}

			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		System.out.println("Goodbye!");		
	}
}
