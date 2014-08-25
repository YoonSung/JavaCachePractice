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
	
	//case 1,2
	static final HashMap<Integer, String> cache = new HashMap<Integer, String>();
	
	//case 3
	static final HashMap<Integer, DataNode> cache3 = new HashMap<Integer, DataNode>();
	private static final int EXECUTE_QUERY_NUM = 10000;
	static DataNode root;

	/*
	 * 
	 * TODO 1. 캐시가 없을때 2. 캐시가 있을때 (용량 무제한) 3. 캐쉬가 있을때 (용량 7% - 700개)
	 */
	public static void main(String[] args) throws IOException {
		createKeyValueStore();
		saveKeyValueStoreToFile();
		
		QueryTestCallback case1 = new QueryTestCallback() {
			
			@Override
			public int executeQueries(Statement stmt, ResultSet rs, BufferedReader bis) throws NumberFormatException, IOException, SQLException {
				String readLine = null;
				int id = 0;
				String sql = null;
				int miss = 0;
				
				while ((readLine = bis.readLine()) != null) {
					id = Integer.parseInt(readLine);
					sql = "SELECT k FROM ctest WHERE k = " + id;
					rs = stmt.executeQuery(sql);
				}
				
				return miss;
			}

			@Override
			public String getTitle() {
				return "캐시가 없을때";
			}
		};
		
		selectWithEvaluation(case1);
		
		
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
		/*
		while ((readLine = bis.readLine()) != null) {
			
			id = Integer.parseInt(readLine);
			
			if (cache.containsKey(id)) {
				cache.get(id);
			} else {
				sql = "SELECT v FROM ctest WHERE k = " + id;
				//System.out.println("id: " + id);
				rs = stmt.executeQuery(sql);
				
				if (rs.next()) {
					//System.out.println("rs.getInt(1): " + rs.getString(1));
					cache.put(id, rs.getString(1));
				}
				
				
			}
		}
		*/
		/**********************************************/
		
		/**********************************************/
		//3. Cache With 7% Memory (= 700 values)
		/*
		while ((readLine = bis.readLine()) != null) {
			
			id = Integer.parseInt(readLine);
			if (cache3.containsKey(id)) {
				cache3.get(id);
			} else {
				sql = "SELECT v FROM ctest WHERE k = " + id;
				
				rs = stmt.executeQuery(sql);
				
				if (rs.next()) {
					DataNode newNode = new DataNode(); 
					newNode.value = rs.getString(1);
					
					switch (cache3.size()) {
					case 0:
						newNode.prev = newNode;
						newNode.next = newNode;
						root = newNode;
						break;
					case 700:
						DataNode beforeLastNode = root.prev.prev;
						beforeLastNode.next = newNode;
						newNode.prev = beforeLastNode; 
								
						cache.remove(root.prev);
						root.prev = newNode;
						newNode.next = root;
						root = newNode;
						break;
					default:
						root.prev.next = newNode;
						newNode.next = root;
						break;
					}
					
					cache3.put(id, newNode);
				}
			}
		}
		*/
		/**********************************************/
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

	private static void selectWithEvaluation(QueryTestCallback callback) {
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

			File file = new File(FILE_PATH);
			FileReader fis = new FileReader(file);
			bis = new BufferedReader(fis);
			
			testTemplate(stmt, rs, bis, callback);

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

	private static void testTemplate(Statement stmt, ResultSet rs, BufferedReader bis, QueryTestCallback callback) throws NumberFormatException, IOException, SQLException {
		long startTime = System.currentTimeMillis();
		
		
		int miss = callback.executeQueries(stmt, rs, bis);
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println(callback.getTitle());
		System.out.println("TotalTime    : "+totalTime);
		System.out.println("SuccessQuery : "+EXECUTE_QUERY_NUM);
		System.out.println("FailQuery    : "+(miss));
		System.out.println("QPS          : "+(EXECUTE_QUERY_NUM/(totalTime/1000)));
	}
}

class DataNode {
	DataNode next;
	DataNode prev;
	String value;
}