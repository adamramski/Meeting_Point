package aramski;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class Tools {
	
	private static String userName ="root";
	private static String password ="";
	
	private static Connection getConnection() throws SQLException, Exception {
		
		Class.forName("com.mysql.jdbc.Driver").newInstance();
	    Connection conn = null;
	    Properties connectionProps = new Properties();
	    connectionProps.put("user", userName);
	    connectionProps.put("password", password);
	   
	    conn = DriverManager.getConnection(
	                   "jdbc:mysql://localhost:3306/mysociety",
	                   connectionProps);
	    
	    System.out.println("Connected to database");
	    return conn;
	}
	
	public static ResultSet executeQuery(String sql) throws SQLException, Exception {
		
		return getConnection().createStatement().executeQuery(sql);
	}
	
	public static void printSQLException(SQLException ex){
		for (Throwable e : ex) {
		      if (e instanceof SQLException) {
		        if (ignoreSQLException(((SQLException)e).getSQLState()) == false) {
		          e.printStackTrace(System.err);
		          System.err.println("SQLState: " + ((SQLException)e).getSQLState());
		          System.err.println("Error Code: " + ((SQLException)e).getErrorCode());
		          System.err.println("Message: " + e.getMessage());
		          Throwable t = ex.getCause();
		          while (t != null) {
		            System.out.println("Cause: " + t);
		            t = t.getCause();
		          }
		        }
		      }
		    }
	}
	
	public static boolean ignoreSQLException(String sqlState) {
	    if (sqlState == null) {
	      System.out.println("The SQL state is not defined!");
	      return false;
	    }
	    // X0Y32: Jar file already exists in schema
	    if (sqlState.equalsIgnoreCase("X0Y32"))
	      return true;
	    // 42Y55: Table already exists in schema
	    if (sqlState.equalsIgnoreCase("42Y55"))
	      return true;
	    return false;
	  }
}
