import java.io.BufferedReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public interface QueryTestCallback {
	public int executeQueries(Statement stmt, ResultSet rs, BufferedReader bis) throws NumberFormatException, IOException, SQLException;
	public String getTitle();
}
