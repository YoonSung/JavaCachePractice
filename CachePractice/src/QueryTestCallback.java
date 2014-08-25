import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public interface QueryTestCallback {
	public String getTitle();
	public int lineReadTemplate(Statement stmt, ResultSet rs, int id, int miss) throws SQLException;
}
