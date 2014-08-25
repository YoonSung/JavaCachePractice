import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;


public interface QueryTestCallback<T> {
	public String getTitle();
	public int lineReadTemplate(Statement stmt, ResultSet rs, Map<Integer, T> cache, int id, int miss) throws SQLException;
}
